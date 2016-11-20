(ns clojure-applied-checkout.state.family-async
  (:require [clojure-applied-checkout.state.store :as store]))

(def shopping-list (ref #{}))
(def assignments (ref #{}))
(def shopping-cart (ref #{}
                        :validator #(not (contains? % :candy))))

(defn init []
  (store/init {:eggs   2 :bacon 3 :apples 3
               :candy  5 :soda 2 :milk 1
               :bread  3 :carrots 1 :potatoes 1
               :cheese 3})
  (dosync
    (ref-set shopping-list #{:milk :butter :bacon :eggs
                             :carrots :potatoes :cheese :apples})
    (ref-set assignments {})
    (ref-set shopping-cart #{})))

(defn assignment
  [child]
  (get @assignments child))

(defn buy-candy
  []
  (dosync
    (commute shopping-cart conj (store/grab :candy))))

(defn collect-assignment
  [child]
  (let [item (assignment child)]
    (dosync
      (alter shopping-cart conj item)
      (alter assignments dissoc child)
      (ensure shopping-list))
    item))

(defn assign-item-to-child
  [child]
  (let [item (first @shopping-list)]
    (dosync
      (alter assignments assoc child item)
      (alter shopping-list disj item))))

(defn dawdle
  []
  (let [t (rand-int 5000)]
    (Thread/sleep t)
    (maybe? buy-candy)))

(defn send-child-for-item
  [child item q]
  (println child "is searching for an item")
  (dawdle)
  (collect-assignment child)
  (>!! q child))

(defn report
  []
  (println "store inventory" @store/inventory)
  (println "shopping-list" @shopping-list)
  (println "assignments" @assignments)
  (println "shopping-cart" @shopping-cart))

(defn go-shopping
  []
  (init)
  (report)
  (let [kids (chan 10)]
    (doseq [k my-kids]
      (>!! kids k))
    (go-loop [kid (<! kids)]
             (if (seq @shopping-list)
               (do
                 (go
                   (send-child-for-item kid (assign-item-to-child kid) kids))
                 (recur (<! kids)))
               (do
                 (println "done shopping.")
                 (report))))))


