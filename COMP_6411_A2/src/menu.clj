(ns menu
  (:require [clojure.string :as string]
            [db :refer [initialize-data display-customers-data display-products-data display-sales-data calculate-total-sales calculate-total-count]]))

(defn display-menu []
  (loop []
    (print "\u001b[2J")  ; ANSI escape sequence to clear the screen
    (println "\n*** Sales Menu ***")
    (println "1. Display Customer Table")
    (println "2. Display Product Table")
    (println "3. Display Sales Table")
    (println "4. Total Sales for Customer")
    (println "5. Total Count for Product")
    (println "6. Exit")
    (print "Enter an option: ")
    (flush)  ; Ensure that the print statement is output before blocking on read-line
    (let [choice (read-line)]
      (println "[DEBUG] choice entered:" choice)  ; Debug output to see what's read
      (case choice
        "1" (do (display-customers-data) (recur))
        "2" (do (display-products-data) (recur))
        "3" (do (display-sales-data) (recur))
        "4" (do (println "Enter customer name:")
                (let [customer-name (read-line)]
                  (calculate-total-sales customer-name))
                (recur))
        "5" (do (println "Enter product description:")
                (let [product-description (read-line)]
                  (calculate-total-count product-description))
                (recur))
        "6" (do (println "Good Bye") (System/exit 0))
        (do (println "Invalid option, please try again")
            (recur))))))

; Initialize the data
(db/initialize-data)

; Start the menu
(display-menu)
