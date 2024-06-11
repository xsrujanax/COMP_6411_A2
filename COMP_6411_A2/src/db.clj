(ns db
  (:require [clojure.string :as string]))

; Load and split data from a file
(defn load-file-data [filename]
  (->> filename
       slurp
       (string/split-lines)
       (map #(string/split % #"\|"))))

; Atoms to store the parsed data
(def customers (atom {}))
(def products (atom {}))
(def sales (atom []))

; Parse customer data into a map indexed by customer ID
(defn parse-customers [raw-data]
  (into {} (map (fn [[id name address phone]]
                  {id {:name name :address address :phone phone}})
                raw-data)))

; Parse product data into a map indexed by product ID
(defn parse-products [raw-data]
  (into {} (map (fn [[id desc cost]]
                  {id {:description desc :cost (read-string cost)}})
                raw-data)))

; Parse sales data into a vector of maps
(defn parse-sales [raw-data]
  (map (fn [[id cust-id prod-id count]]
         {:sales-id id :cust-id cust-id :prod-id prod-id :count (read-string count)})
       raw-data))

; Initialize data by loading and parsing files
(defn initialize-data []
  (reset! customers (parse-customers (load-file-data "cust.txt")))
  (reset! products (parse-products (load-file-data "prod.txt")))
  (reset! sales (parse-sales (load-file-data "sales.txt"))))

; Functions to display data
(defn display-customers-data []
  (doseq [[id customer] @customers]
    (println (str id ": " customer))))

(defn display-products-data []
  (doseq [[id product] @products]
    (println (str id ": " product))))

(defn display-sales-data []
  (doseq [sale @sales]
    (let [customer-name ((@customers (get sale :cust-id)) :name)
          product-desc ((@products (get sale :prod-id)) :description)]
      (println (str (:sales-id sale) ": [" customer-name " " product-desc " " (:count sale) "]")))))

; Calculate total sales for a given customer
(defn calculate-total-sales [customer-name]
  (let [total-sales (reduce (fn [total sale]
                              (if (= ((@customers (get sale :cust-id)) :name) customer-name)
                                (+ total (* (@products (get sale :prod-id) :cost) (get sale :count)))
                                total))
                            0
                            @sales)]
    (println (str customer-name ": $" total-sales))))

; Calculate total count for a given product
(defn calculate-total-count [product-description]
  (let [total-count (reduce (fn [total sale]
                              (if (= ((@products (get sale :prod-id)) :description) product-description)
                                (+ total (get sale :count))
                                total))
                            0
                            @sales)]
    (println (str product-description ": " total-count))))
