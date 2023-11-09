(ns me.flowthing.pp-record-test
  (:require [clojure.test :refer [deftest is]]
            [me.flowthing.pp.test :refer [pp]]))

(defrecord R [x])
(defrecord R?*+! [])

(deftest print-record
  (is (= "#sci.impl.records.SciRecord{:x\n                            {:a\n                             1,\n                             :b\n                             2,\n                             :c\n                             3,\n                             :d\n                             4}}\n"
        (pp (->R {:a 1 :b 2 :c 3 :d 4}) :max-width 31)))
  (is (= "#sci.impl.records.SciRecord{}\n"
        (pp (->R?*+!)))))
