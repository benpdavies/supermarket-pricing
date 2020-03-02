(ns supermarket-pricing.core-test
  (:require [clojure.test :refer :all]
            [supermarket-pricing.core :refer :all]))

(deftest price-of-basket-is-calculated-correctly
  (testing "Price of basket calculator"
    (is (= "£3.40" (price-of-basket (create-receipt 4 2))))))
