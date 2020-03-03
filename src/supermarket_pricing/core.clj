(ns supermarket-pricing.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;; Rounding to two decimal places function for currency
(defn round
  [d]
  (let [factor (Math/pow 10 2)]
    (/ (Math/round (* d factor)) factor)))

;; Mock up of a database containing units and price per units for different products
(def prices
  {:beans  {:unit "tin"
            :price-per-unit 0.50}
   :coke   {:unit "can"
            :price-per-unit 0.70}
   :onions {:unit "kg"
            :price-per-unit 1.99}})

;; Example of a basket represented in a map data structure
(def eg-basket
  {:beans  3
   :coke   3
   :onions 0.2})

;; Example of a receipt represented in a map with two keys:
;; 'items'  - the items in the basket
;; 'offers' - the offers associated with the receipt
(def eg-receipt
  {:items  [{:name "beans"  :quantity 4   :unit "item" :price-per-unit 0.5}
            {:name "coke"   :quantity 5   :unit "item" :price-per-unit 0.7}
            {:name "onions" :quantity 0.2 :unit "kg"   :price-per-unit 1.99}]
   :offers [{:type "fixed-price" :item "coke" :quantity 2 :price 1}]})

;; Function to form a receipt made up of information from:
;; - prices database
;; - customer's basket
;; - any offers presented
(defn form-receipt
  [basket & offers]
  {:items (mapv #(do {:name (name %)
                      :quantity (% basket)
                      :unit (:unit (% prices))
                      :price-per-unit (:price-per-unit (% prices))})
                (keys basket))
   :offers (vec offers)})

;; Representation of a fixed price offer: "Get :quantity :item for :price pounds."
(defn offer-fixed-price
  [item quantity price]
  {:type "fixed-price"
   :item item
   :quantity quantity
   :price price})

;; Calculate price of an item in the :items section of receipt
(defn price-of-item
  [item]
  (* (:quantity item) (:price-per-unit item)))

;; Function to calculate the saving associated with a fixed price offer:
;; 1. Find the appropriate item for that offer
;; 2. Calculate the original price (as if the offer didn't exist)
;; 3. Calculate the reduced price including the offer
;; 4. Find the difference
(defn savings-fixed-price
  [items {:keys [item quantity price]}]
  (let [order     (first (filter #(= item (:name %)) items))
        og-price  (round (* (:quantity order) (:price-per-unit order)))
        new-price (round (+ (* (:price-per-unit order) (mod (:quantity order) quantity))
                            (* price (int (/ (:quantity order) quantity)))))]
    (round (- og-price new-price))))

;; Function to calculate the total of all the different savings associated with the offers in the receipt
(defn calculate-savings
  [{:keys [items offers]}]
  (apply + (for [{:keys [type] :as offer} offers]
             (case type
               "fixed-price" (savings-fixed-price items offer)
               :else 0))))

;; Function to find the final receipt price: sub-total - savings
(defn price-of-receipt
  [receipt]
  (let [sub-total (->> (:items receipt)
                       (map price-of-item)
                       (apply +)
                       round)
        savings   (calculate-savings receipt)]
    (str "£" (format "%.2f" (- sub-total savings)))))
