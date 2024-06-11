(ns db
  (:require [clojure.string :as string]))

(defn load-file-data [filename]
  (->> filename
       slurp
       (string/split-lines)
       (map #(string/split % #"\|" -1))))

(def customers (atom {}))
(def products (atom {}))
(def sales (atom []))

(defn parse-customers [raw-data]
  (into {} (map (fn [[id name address phone]]
                  [id {:name name :address address :phone phone}])
                raw-data)))

(defn parse-products [raw-data]
  (into {} (map (fn [[id desc cost]]
                  [id {:description desc :cost (read-string cost)}])
                raw-data)))

(defn parse-sales [raw-data]
  (map (fn [[id cust-id prod-id count]]
         {:sales-id id :cust-id cust-id :prod-id prod-id :count (read-string count)})
       raw-data))

(defn initialize-data []
  (reset! customers (parse-customers (load-file-data "cust.txt")))
  (reset! products (parse-products (load-file-data "prod.txt")))
  (reset! sales (parse-sales (load-file-data "sales.txt"))))

(defn display-customers-data []
  (dorun (map #(println (str (first %) ": [\"" (:name (second %)) "\" \"" (:address (second %)) "\" \"" (:phone (second %)) "\"]"))
              (sort-by first @customers))))

(defn display-products-data []
  (dorun (map #(println (str (first %) ": [\"" (:description (second %)) "\" $" (:cost (second %)) "]"))
              (sort-by first @products))))

(defn display-sales-data []
  (dorun (map (fn [sale]
                (let [customer-name ((@customers (get sale :cust-id)) :name)
                      product-desc ((@products (get sale :prod-id)) :description)]
                  (println (str (:sales-id sale) ": [\"" customer-name "\" \"" product-desc "\" " (:count sale) "]"))))
              (sort-by :sales-id @sales))))

(defn calculate-total-sales [customer-name]
  (let [total-sales (reduce (fn [total sale]
                              (if (= ((@customers (get sale :cust-id)) :name) customer-name)
                                (+ total (* ((@products (get sale :prod-id)) :cost) (:count sale)))
                                total))
                            0
                            @sales)]
    (println (str customer-name ": $" total-sales))))

(defn calculate-total-count [product-description]
  (let [total-count (reduce (fn [total sale]
                              (if (= ((@products (get sale :prod-id)) :description) product-description)
                                (+ total (:count sale))
                                total))
                            0
                            @sales)]
    (println (str product-description ": " total-count))))
