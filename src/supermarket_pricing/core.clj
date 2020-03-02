(ns supermarket-pricing.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def prices
  {"beans" 0.5
   "coke"  0.7})

(def example-receipt
  [{:name "beans" :quantity 3} {:name "coke" :quantity 1}])

(defn create-receipt
  [no-beans no-coke]
  [{:name "beans" :quantity no-beans} {:name "coke" :quantity no-coke}])

(defn price-of-item
  [item]
  (* (get prices (:name item)) (:quantity item)))

(defn price-of-basket
  [receipt]
  (->> receipt
       (map price-of-item)
       (apply +)
       (format "%.2f")
       (str "£")))
