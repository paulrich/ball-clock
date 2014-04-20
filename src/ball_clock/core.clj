(ns ball-clock.core)

(defmethod print-method clojure.lang.PersistentQueue [q w]
  (print-method '<- w) (print-method (seq q) w) (print-method '-< w))

(def ^:private minutes-per-cycle (* 12 60))

(defn- carry-balls [clock]
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
  ([{:keys [ones fives hours queue] :as state}]
     "Increment the minutes field of the clock"
     (if (seq queue)
       (-> state
           (update-in [:queue] pop)
           (update-in [:ones] conj (peek queue))
           carry-balls)))
  ([clock n]
     "Increment the minutes field of the clock n times"
     (->> clock
          (iterate increment-minutes)
          next
          (take n)
          last)))

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

(defn- full-cycle? [clock]
  (= (maximum-minutes clock) minutes-per-cycle))

(defn ball-clock [balls]
  {:ones [] :fives [] :hours []
   :queue (into clojure.lang.PersistentQueue/EMPTY (range 0 balls))})

(defn- next-arrangement
  "Runs the clock through a simulation of a 12-hour cycle"
  [clock]
  {:pre [(full-cycle? clock)]}
  (increment-minutes clock minutes-per-cycle))

(defn- cycle-transform [{:keys [queue] :as clock}]
  (let [next-queue (->> clock next-arrangement :queue (zipmap (range)))]
    #(let [old-queue (:queue %)]
       (assoc %
         :queue
         (->> old-queue
              (replace next-queue)
              (into clojure.lang.PersistentQueue/EMPTY))))))

(defn unique-cycles
  "Given a number of balls in a ball-clock, calculates how many unique 12-hour cycles the clock can produce"
  [balls]
  (let [clock (ball-clock balls)]
   (->> clock
        (iterate (cycle-transform clock))
        rest
        (take-while #(not (apply < (:queue %))))
        count
        inc)))