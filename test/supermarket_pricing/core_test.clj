(ns supermarket-pricing.core-test
  (:require [clojure.test :refer :all]
            [supermarket-pricing.core :refer :all]))

(def test-basket
  {:beans  4
   :coke   2
   :onions 0.7})

(deftest price-of-basket-is-calculated-correctly
  (testing "Price of basket calculator"
    (is (= "£4.79" (price-of-receipt (form-receipt test-basket))))))
