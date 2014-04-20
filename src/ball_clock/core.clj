(ns ball-clock.core)

(defmethod print-method clojure.lang.PersistentQueue [q w]
  (print-method '<- w) (print-method (seq q) w) (print-method '-< w))

(def ^:private minutes-per-cycle (* 12 60))

(defn carry-balls [clock]
  (as-> clock state
        (let [ones (:ones state)]
          (if (> 5 (count ones))
            state
            (-> state
                (assoc :ones [])
                (update-in [:fives] conj (peek ones))
                (update-in [:queue] into (->> ones (iterate pop) (map peek) (take 5) next)))))
        (let [fives (:fives state)]
          (if (> 12 (count fives))
            state
            (-> state
                (assoc :fives [])
                (update-in [:hours] conj (peek fives))
                (update-in [:queue] into (->> fives (iterate pop) (map peek) (take 12) next)))))
        (let [hours (:hours state)]
          (if (> 12 (count hours))
            state
            (-> state
                (assoc :hours [])
                (update-in [:queue] into (->> hours (iterate pop) (map peek) (take 12) next))
                (update-in [:queue] conj (peek hours)))))))

 (defn increment-minutes
   [{:keys [ones fives hours queue] :as state}]
   (if (seq queue)
     (-> state
         (update-in [:queue] pop)
         (update-in [:ones] conj (peek queue))
         carry-balls)))

 (defn maximum-minutes
  "Returns the maximum minutes the clock can keep track of, up to 12 hours (720 minutes)"
  ([clock maximum]
     (->> clock
          (iterate increment-minutes)
          rest
          (take-while #(seq %))
          (take maximum)
          count))
  ([clock]
     (maximum-minutes clock minutes-per-cycle)))

(defn increment [clock minutes]
  (->> clock
       (iterate increment-minutes)
       next
       (take minutes)
       last))

(defn- full-cycle? [clock]
  (= (maximum-minutes clock) minutes-per-cycle))

(defn ball-clock [balls]
  {:ones [] :fives [] :hours []
   :queue (into clojure.lang.PersistentQueue/EMPTY (range 0 balls))})

(defn next-arrangement
  "Runs the clock through a simulation of a 12-hour cycle"
  [clock]
  {:pre [(full-cycle? clock)]}
  (increment clock minutes-per-cycle))

(defn unique-cycles [{:keys [queue] :as clock}]
  {:pre [(apply < queue)]}
  (/
   (->> clock
        (iterate next-arrangement)
        next
        (take-while #(not (apply < (:queue %))))
        count
        inc                  ; one more for returning to initial state
        )
   2))
