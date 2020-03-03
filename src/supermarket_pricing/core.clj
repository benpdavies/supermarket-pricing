(ns supermarket-pricing.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def prices
  {:beans  {:unit "item"
            :price-per-unit 0.50}
   :coke   {:unit "item"
            :price-per-unit 0.70}
   :onions {:unit "kg"
            :price-per-unit 1.99}})

(def eg-basket
  {:beans  3
   :coke   1
   :onions 0.2})

(def eg-receipt
  [{:name "beans"  :quantity 3   :unit "item" :price-per-unit 0.5}
   {:name "coke"   :quantity 1   :unit "item" :price-per-unit 0.7}
   {:name "onions" :quantity 0.2 :unit "kg"   :price-per-unit 1.99}])

(defn form-receipt
  [basket]
  (mapv #(do {:name (name %)
              :quantity (% basket)
              :unit (:unit (% prices))
              :price-per-unit (:price-per-unit (% prices))})
        (keys basket)))

(defn price-of-item
  [item]
  (* (:quantity item) (:price-per-unit item)))

(defn price-of-receipt
  [receipt]
  (->> receipt
       (map price-of-item)
       (apply +)
       (format "%.2f")
       (str "£")))
