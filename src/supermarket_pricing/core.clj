(ns supermarket-pricing.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn round
  "Round to two decimal places for currency"
  [d]
  (let [factor (Math/pow 10 2)]
    (/ (Math/round (* d factor)) factor)))

(def prices
  "Mock up of a database containing units and price per units for different products"
  {:beans  {:unit "tin"
            :price-per-unit 0.50}
   :coke   {:unit "can"
            :price-per-unit 0.70}
   :onions {:unit "kg"
            :price-per-unit 1.99}
   :ale1   {:unit "bottle"
            :price-per-unit 3}
   :ale2   {:unit "bottle"
            :price-per-unit 3}
   :ale3   {:unit "bottle"
            :price-per-unit 3}
   :ale4   {:unit "bottle"
            :price-per-unit 3}})

(def eg-basket
  "Example of a basket represented in a map data structure"
  {:beans  3
   :coke   3
   :onions 0.2
   :ale1   1
   :ale2   2
   :ale3   2
   :ale4   1})

(def eg-receipt
  "Example of a receipt created by 'form-receipt' represented in a map with two keys:
  'items'  - the items in the basket
  'offers' - the offers associated with the receipt"
  {:items  [{:name "beans"  :quantity 4   :unit "item"   :price-per-unit 0.5}
            {:name "coke"   :quantity 2   :unit "item"   :price-per-unit 0.7}
            {:name "onions" :quantity 0.7 :unit "kg"     :price-per-unit 1.99}
            {:name "ale1"   :quantity 1   :unit "bottle" :price-per-unit 3}
            {:name "ale2"   :quantity 2   :unit "bottle" :price-per-unit 3}
            {:name "ale3"   :quantity 2   :unit "bottle" :price-per-unit 3}
            {:name "ale4"   :quantity 1   :unit "bottle" :price-per-unit 3}]
   :offers [{:type "x-for-y"             :item "beans"                 :x 3        :y 2}
            {:type "fixed-price"         :item "coke"                  :quantity 2 :price 1}
            {:type "set-for-fixed-price" :item #{"ale1" "ale2" "ale3"} :quantity 3 :price 6}]})

(defn form-receipt
  "Function to form a receipt made up of information from:
  - prices database
  - customer's basket
  - any offers presented"
  [basket & offers]
  {:items (mapv #(do {:name (name %)
                      :quantity (% basket)
                      :unit (:unit (% prices))
                      :price-per-unit (:price-per-unit (% prices))})
                (keys basket))
   :offers (vec offers)})

(defn offer-fixed-price
  "Representation of a fixed-price offer: 'Get :quantity :item for :price pounds.'"
  [item quantity price]
  {:type "fixed-price"
   :item item
   :quantity quantity
   :price price})

(defn offer-x-for-y
  "Representation of an x-for-y offer: ':x units of :item for the price of :y'"
  [item x y]
  {:type "x-for-y"
   :item item
   :x x
   :y y})

(defn offer-set-for-fixed-price
  "Representation of a set-for-fixed-price offer: ':quantity units in set :offer-set for the price of :price'"
  [item quantity price]
  {:type "set-for-fixed-price"
   :item item
   :quantity quantity
   :price price})

(defn price-of-item
  "Calculate price of an :item in the items section of receipt"
  [item]
  (* (:quantity item) (:price-per-unit item)))

(defn savings-fixed-price
  "Function to calculate the saving associated with a fixed-price offer:
  1. Find the appropriate order for that offer
  2. Calculate the original price (as if the offer didn't exist)
  3. Calculate the reduced price including the fixed-price offer
  4. Find the difference"
  [items {:keys [item quantity price]}]
  (let [order     (first (filter #(= item (:name %)) items))
        og-price  (round (* (:quantity order) (:price-per-unit order)))
        new-price (round (+ (* (:price-per-unit order) (mod (:quantity order) quantity))
                            (* price (int (/ (:quantity order) quantity)))))]
    (round (- og-price new-price))))

(defn savings-set-for-fixed-price
  "Function to calculate the saving associated with a fixed-price offer:
  1. Find the appropriate order for that offer
  2. Calculate the original price (as if the offer didn't exist)
  3. Calculate the reduced price including the fixed-price offer
  4. Find the difference"
  [items {:keys [item quantity price] :as offer}]

  (let [order     (filter #(item (:name %)) items)
        og-price  (apply + (map #(round (* (:quantity %) (:price-per-unit %))) order))
        set-quantity (apply + (map :quantity order))
        set-price    (:price-per-unit (first order))
        new-price (round (+ (* set-price (mod set-quantity quantity))
                            (* price (int (/ set-quantity quantity)))))]
    (round (- og-price new-price))))

(defn savings-x-for-y
  "Function to calculate the saving associated with a x-for-y offer:
  1. Find the appropriate order for that offer
  2. Calculate the original price (as if the offer didn't exist)
  3. Calculate the reduced price including the x-for-y offer
  4. Find the difference"
  [items {:keys [item x y]}]
  (let [order     (first (filter #(= item (:name %)) items))
        og-price  (round (* (:quantity order) (:price-per-unit order)))
        new-price (round (+ (* (:price-per-unit order) (mod (:quantity order) x))
                            (* (* y (:price-per-unit order)) (int (/ (:quantity order) x)))))]
    (round (- og-price new-price))))

(defn calculate-all-savings
  "Function to calculate the total of all the different savings associated with the offers in the receipt"
  [{:keys [items offers]}]
  (apply + (for [{:keys [type] :as offer} offers]
             (case type
               "set-for-fixed-price" (savings-set-for-fixed-price items offer)
               "fixed-price"         (savings-fixed-price items offer)
               "x-for-y"             (savings-x-for-y items offer)
               :else                 0))))

(defn price-of-receipt
  "Function to find the final receipt price: sub-total - savings"
  [receipt]
  (let [sub-total (->> (:items receipt)
                       (map price-of-item)
                       (apply +)
                       round)
        savings   (calculate-all-savings receipt)]
    (str "£" (format "%.2f" (- sub-total savings)))))
