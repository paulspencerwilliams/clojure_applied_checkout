(ns clojure-applied-checkout.carts
  (:require
    [clojure-applied-checkout.money :refer [make-money +$ *$]]))

(defrecord CatalogItem [number dept desc price])
(defrecord Cart [number customer line-items settled?])
(defrecord LineItem [quantity catalog-item price])
(defrecord Customer [cname email membership-number])

(def carts [(->Cart 116
                    (->Customer
                      "Danny Turner"
                      "danny@fullhouse.example.com"
                      28374)
                    [(->LineItem
                       3
                       (->CatalogItem
                         664
                         :clothing
                         "polo shirt L"
                         (make-money 2515 :usd))
                       (make-money 7545 :usd))
                     (->LineItem
                       1
                       (->CatalogItem
                         621
                         :clothing
                         "khaki pants"
                         (make-money 3500 :usd))
                       (make-money 3500 :usd))]
                    true)
            (->Cart 116
                    (->Customer
                      "Paul Williams"
                      "wat@wat.wat"
                      28374)
                    [(->LineItem
                       4
                       (->CatalogItem
                         343
                         :computers
                         "bigun"
                         (make-money 313 :usd))
                       (make-money 1252 :usd))]
                    true)])

(defn- line-summary
  [line-item]
  {:dept  (get-in line-item [:catalog-item :dept])
   :total (:price line-item)})

(defn- dept-total
  [m k v]
  (assoc m k (reduce +$ (map :total v))))

(defn revenue-by-dept []
  (->> (filter :settled? carts)
       (mapcat :line-items)
       (map line-summary)
       (group-by :dept)
       (reduce-kv dept-total {})))
