(ns rays.core-test
  (:require [clojure.test :refer :all]
            [rays.core :refer :all]))

(deftest test-quadratic
  (testing "Quadractic calculator"
    (is (= [4.0 -1.0] (quadratic 1 -3 -4)))
    (is (= [-3.0 -0.5] (quadratic 2 7 3)))
    ))
