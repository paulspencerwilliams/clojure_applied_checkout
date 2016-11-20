(ns clojure-applied-checkout.state.me
  (:require [clojure-applied-checkout.state.store :as store]))

(defn go-shopping-naive
  "Returns a list of items purchased"
  [shopping-list]
  (loop [[item & items] shopping-list cart []]
    (if item
      (recur items (conj cart item))
      cart)))

(defn shop-for-an-item
  [cart item]
  (if (store/grab item)
    (conj cart item)
    cart))

(defn go-shopping
  [shopping-list]
  (reduce shop-for-an-item [] shopping-list))
