(ns supermarket-pricing.core-test
  (:require [clojure.test :refer :all]
            [supermarket-pricing.core :refer :all]))

(def test-basket
  {:beans  4
   :coke   2
   :onions 0.7})

(def test-receipt-empty
  {:items [] :offers []})

(def test-receipt-no-offers
  {:items  [{:name "beans"  :quantity 4   :unit "tin" :price-per-unit 0.5}
            {:name "coke"   :quantity 2   :unit "can" :price-per-unit 0.7}
            {:name "onions" :quantity 0.7 :unit "kg"   :price-per-unit 1.99}]
   :offers []})

(def test-receipt-fixed-price-offer
  {:items  [{:name "beans"  :quantity 4   :unit "tin" :price-per-unit 0.5}
            {:name "coke"   :quantity 2   :unit "can" :price-per-unit 0.7}
            {:name "onions" :quantity 0.7 :unit "kg"   :price-per-unit 1.99}]
   :offers [{:type "fixed-price" :item "coke" :quantity 2 :price 1}]})

(deftest receipt-is-formed-correctly
  (testing "Form empty receipt"
    (is (= test-receipt-empty (form-receipt {}))))
  (testing "Form receipt with no offers"
    (is (= test-receipt-no-offers (form-receipt test-basket))))
  (testing "Form receipt with fixed price offer"
    (is (= test-receipt-fixed-price-offer (form-receipt test-basket (offer-fixed-price "coke" 2 1))))))


(deftest price-of-basket-is-calculated-correctly
  (testing "Price of empty basket"
    (is (= "£0.00" (price-of-receipt test-receipt-empty))))
  (testing "Price of basket with no offers"
    (is (= "£4.79" (price-of-receipt test-receipt-no-offers))))
  (testing "Price of basket with fixed price offer"
    (is (= "£4.39" (price-of-receipt test-receipt-fixed-price-offer)))))
