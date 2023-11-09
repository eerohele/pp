(ns me.flowthing.pp-custom-type-test
  (:require [clojure.pprint :as cpp]
            [clojure.test :refer [deftest is]]
            [me.flowthing.pp.test :refer [pp]]))

(deftype T
  [xs]
  clojure.lang.Associative
  (assoc [_ k v]
    (T. (.assoc xs k v))))

(def obj-re
  #"#object\[me.flowthing.pp_custom_type_test.T 0[xX][0-9a-fA-F]+ \"me.flowthing.pp_custom_type_test.T@[0-9a-fA-F]+\"\]\n")

(deftest pprint-custom-type
  (is (re-matches obj-re (with-out-str (prn (T. {:a 1})))))
  (is (re-matches obj-re (with-out-str (cpp/pprint (T. {:a 1})))))
  (is (re-matches obj-re (pp (T. {:a 1}))))

  (binding [*print-level* 0]
    (is (re-matches obj-re (pp (T. {:a 1}))))))
