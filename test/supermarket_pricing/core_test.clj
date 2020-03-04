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

(def test-receipt-x-for-y-offer
  {:items  [{:name "beans"  :quantity 4   :unit "tin" :price-per-unit 0.5}
            {:name "coke"   :quantity 2   :unit "can" :price-per-unit 0.7}
            {:name "onions" :quantity 0.7 :unit "kg"   :price-per-unit 1.99}]
   :offers [{:type "x-for-y" :item "beans" :x 3 :y 2}]})

(def test-receipt-multiple-offers
  {:items  [{:name "beans"  :quantity 4   :unit "tin" :price-per-unit 0.5}
            {:name "coke"   :quantity 2   :unit "can" :price-per-unit 0.7}
            {:name "onions" :quantity 0.7 :unit "kg"   :price-per-unit 1.99}]
   :offers [{:type "fixed-price" :item "coke" :quantity 2 :price 1}
            {:type "x-for-y" :item "beans" :x 3 :y 2}]})

(def test-receipt-all-offers
  {:items  [{:name "beans"  :quantity 4   :unit "tin" :price-per-unit 0.5}
            {:name "coke"   :quantity 2   :unit "can" :price-per-unit 0.7}
            {:name "onions" :quantity 0.7 :unit "kg"   :price-per-unit 1.99}
            {:name "ale1"   :quantity 1   :unit "bottle" :price-per-unit 3}
            {:name "ale2"   :quantity 2   :unit "bottle" :price-per-unit 3}
            {:name "ale3"   :quantity 2   :unit "bottle" :price-per-unit 3}
            {:name "ale4"   :quantity 1   :unit "bottle" :price-per-unit 3}]
   :offers [{:type "fixed-price" :item "coke" :quantity 2 :price 1}
            {:type "x-for-y" :item "beans" :x 3 :y 2}
            {:type "set-for-fixed-price" :item  #{"ale1" "ale2" "ale3"} :quantity 3 :price 6}]})

(deftest receipt-is-formed-correctly
  (testing "Form empty receipt"
    (is (= test-receipt-empty (form-receipt {}))))
  (testing "Form receipt with no offers"
    (is (= test-receipt-no-offers (form-receipt test-basket))))
  (testing "Form receipt with fixed-price offer"
    (is (= test-receipt-fixed-price-offer (form-receipt test-basket (offer-fixed-price "coke" 2 1)))))
  (testing "Form receipt with x-for-y offer"
    (is (= test-receipt-x-for-y-offer (form-receipt test-basket (offer-x-for-y "beans" 3 2)))))
  (testing "Form receipt with fixed-price offer"
    (is (= test-receipt-multiple-offers (form-receipt test-basket (offer-fixed-price "coke" 2 1) (offer-x-for-y "beans" 3 2))))))


(deftest price-of-receipt-is-calculated-correctly
  (testing "Price of empty receipt"
    (is (= "£0.00" (price-of-receipt test-receipt-empty))))
  (testing "Price of receipt with no offers"
    (is (= "£4.79" (price-of-receipt test-receipt-no-offers))))
  (testing "Price of receipt with fixed-price offer"
    (is (= "£4.39" (price-of-receipt test-receipt-fixed-price-offer))))
  (testing "Price of receipt with x-for-y offer"
    (is (= "£4.29" (price-of-receipt test-receipt-x-for-y-offer))))
  (testing "Price of receipt with multiple offers"
    (is (= "£3.89" (price-of-receipt test-receipt-multiple-offers))))
  (testing "Price of receipt with all offers"
    (is (= "£18.89" (price-of-receipt test-receipt-all-offers)))))

(deftest receipt-is-processed-correctly
  (is (= {:items  [{:name "beans"  :quantity 4   :unit "tin" :price-per-unit 0.5}
                   {:name "coke"   :quantity 2   :unit "can" :price-per-unit 0.7}
                   {:name "onions" :quantity 0.7 :unit "kg"   :price-per-unit 1.99}
                   {:name "ale1"   :quantity 1   :unit "bottle" :price-per-unit 3}
                   {:name "ale2"   :quantity 2   :unit "bottle" :price-per-unit 3}
                   {:name "ale3"   :quantity 2   :unit "bottle" :price-per-unit 3}
                   {:name "ale4"   :quantity 1   :unit "bottle" :price-per-unit 3}]
          :offers [{:type "fixed-price" :item "coke" :quantity 2 :price 1}
                   {:type "x-for-y" :item "beans" :x 3 :y 2}
                   {:type "set-for-fixed-price" :item  #{"ale1" "ale2" "ale3"} :quantity 3 :price 6}]
          :prices {:sub-total "£22.79" :total-savings "£3.90" :total-to-pay "£18.89"}}
         (process-receipt test-receipt-all-offers))))
