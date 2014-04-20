(ns ball-clock.core-test
  (:require [clojure.test :refer :all]
            [ball-clock.core :refer :all]))

(deftest test-increment
  (testing "Incrementing the clock"
    (is (=
         (-> (ball-clock 5)
             (increment 1)
             (select-keys [:ones :queue]))
         {:ones [0] :queue [1 2 3 4]}))
    (is (=
         (-> (ball-clock 5)
             (increment 2)
             (select-keys [:ones :queue]))
         {:ones [0 1] :queue [2 3 4]}))
    (is (=
         (-> (ball-clock 5)
             (increment 6)
             (select-keys [:ones :fives :queue]))
         {:ones [3] :fives [4] :queue [2 1 0]})
        "Carry and return, five ball")
    (is (=
         (increment-minutes {:ones [0 1 2 3]
                             :fives [4 5 6 7 8 9 10 11 12 13 14]
                             :hours []
                             :queue (conj clojure.lang.PersistentQueue/EMPTY 15)})
         {:ones [] :fives [] :hours [15]
          :queue [3 2 1 0 14 13 12 11 10 9 8 7 6 5 4]})
        "Carry and return, hour ball")
    (is (=
         (increment-minutes {:ones [0 1 2 3]
                             :fives [4 5 6 7 8 9 10 11 12 13 14]
                             :hours [15 16 17 18 19 20 21 22 23 24 25]
                             :queue (conj clojure.lang.PersistentQueue/EMPTY 26)})
         {:ones [] :fives [] :hours []
          :queue [3 2 1 0 14 13 12 11 10 9 8 7 6 5 4
                  25 24 23 22 21 20 19 18 17 16 15 26]})
        "Carry and return all; full cycle")))

(deftest test-max
  (testing "Maximum minutes"
    (is (= (maximum-minutes (ball-clock 1)) 1) "trivial")
    (is (= (maximum-minutes (ball-clock 5)) 9) "carry to fives")
    (is (= (maximum-minutes (ball-clock 16)) 119) "carry to hours")
    (is (= (maximum-minutes (ball-clock 26)) 719) "almost enough")
    (is (= (maximum-minutes (ball-clock 27)) 720) "the max!")))
