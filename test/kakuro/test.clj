(ns kakuro.test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :as ct]
            [kakuro.core :refer :all]))

(def grid1 [
            [(e) (d 4) (d 22) (e) (d 16) (d 3)]
            [(a 3) (v) (v) (da 16 6) (v) (v)]
            [(a 18) (v) (v) (v) (v) (v)]
            [(e) (da 17 23) (v) (v) (v) (d 14)]
            [(a 9) (v) (v) (a 6) (v) (v)]
            [(a 15) (v) (v) (a 12) (v) (v)]
           ])

(def grid2 [[(e) (d 23) (d 30) (e) (e) (d 27) (d 12) (d 16)]
            [(a 16) (v) (v) (e) (da 17 24) (v) (v) (v)]
            [(a 17) (v) (v) (da 15 29) (v) (v) (v) (v)]
            [(a 35) (v) (v) (v) (v) (v) (d 12) (e)]
            [(e) (a 7) (v) (v) (da 7 8) (v) (v) (d 7)]
            [(e) (d 11) (da 10 16) (v) (v) (v) (v) (v)]
            [(a 21) (v) (v) (v) (v) (a 5) (v) (v)]
            [(a 6) (v) (v) (v) (e) (a 3) (v) (v)]
            ])

(deftest drawrow
  (let [line [(da 3 4) (v) (v 1 2) (d 4) (e) (a 5) (v 4) (v 1)]
        result (draw-row line)]
    (print "drawrow")
    (println result)
    (->> result
         (= "    3\\ 4   123456789 12.......    4\\--     -----     --\\ 5       4         1    \n")
         (is))))

(deftest permutes
  (let [vs [(v) (v) (v)]
        results (permute-all vs 6)
        diff (filter all-different results)]
    (println results)
    (->> results
         count
         (= 10)
         (is))
    (->> diff
         count
         (= 6)
         (is))))

(deftest transposes
  (let [ints [[1, 2, 3, 4], [1, 2, 3, 4], [1, 2, 3, 4]]
        tr (transpose ints)]
    (println ints)
    (println tr)
    (is (= (count ints) (count (first tr))))
    (is (= (count (first ints)) (count tr)))))

(deftest isposs
  (let [vc (v 1 2 3)]
    (is (= true (is-possible? vc 2)))
    (is (= false (is-possible? vc 4)))))

(deftest test-solvestep
  (let [result (solve-step [(v 1 2) (v)] 5)]
    (print "solve step result ")
    (println result)
    (is (= (v 1 2) (first result)))
    (is (= (v 3 4) (second result)))))

(deftest test-gather
  (let [line [(da 3 4) (v) (v) (d 4) (e) (a 4) (v) (v)]
        result (gather-values line)]
    (print "gather ")
    (println result)
    (is (= 4 (count result)))
    (is (= (da 3 4) (-> result first first)))
    (is (= (d 4) (-> result rest rest first first)))
    (is (= (e) (-> result rest rest first second)))
    (is (= (a 4) (-> result rest rest first rest rest first)))))

(deftest test-grid1
  (let [solved (solver grid1)
        result (-> grid1 solver draw-grid)
        expected "     3         9    \n"]
    (println result)
    (is (= expected (.substring result (- (count result) (count expected)))))))

(deftest test-grid2
  (let [result (-> grid2 solver draw-grid)
        expected "     2         1    \n"]
    (is (= expected (.substring result (- (count result) (count expected)))))))

(ct/defspec test-transpose
  100
  (prop/for-all [vv (gen/not-empty (gen/vector (gen/vector
                                                 gen/int (first (gen/sample (gen/choose 0 10) 1))) (first (gen/sample (gen/choose 0 10) 1))))]
    (= vv (-> vv transpose transpose))))

