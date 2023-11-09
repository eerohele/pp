(ns me.flowthing.pp-record-test
  (:require [clojure.test :refer [deftest is]]
            [me.flowthing.pp.test :refer [pp]]))

(defrecord R [x])

(deftest pprint-record
  ;; unlike pr, clojure.pprint doesn't print records with the
  ;; fully-qualified record name in the prefix.
  (is (= (with-out-str (prn (->R 1))) (pp (->R 1))))

  (is (= "#me.flowthing.pp_record_test.R{:x\n                               {:a\n                                1,\n                                :b\n                                2,\n                                :c\n                                3,\n                                :d\n                                4}}\n"
        (pp (->R {:a 1 :b 2 :c 3 :d 4}) :max-width 31))))
