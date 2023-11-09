(ns me.flowthing.pp.test
  (:require [clojure.pprint :as cpp]
            [clojure.test :refer [is]]
            [me.flowthing.pp :as sut]))

(defn pp
  [x & {:keys [print-length print-level print-meta print-readably print-namespace-maps]
        :or {print-length nil
             print-level nil
             print-meta false
             print-readably true
             print-namespace-maps false}
        :as opts}]
  (binding [*print-length* print-length
            *print-level* print-level
            *print-meta* print-meta
            *print-readably* print-readably
            *print-namespace-maps* print-namespace-maps]
    (with-out-str (sut/pprint x opts))))

(defn cpp
  [x & {:keys [print-length print-level print-meta print-readably print-namespace-maps max-width]
        :or {print-length nil
             print-level nil
             print-meta false
             print-readably true
             print-namespace-maps false
             max-width 72}}]
  (binding [cpp/*print-right-margin* max-width
            *print-length* print-length
            *print-level* print-level
            *print-meta* print-meta
            *print-readably* print-readably
            *print-namespace-maps* print-namespace-maps]
    (with-out-str (cpp/pprint x))))

(defmacro $
  "Given an input and printing options, check that the SUT prints the
  input the same way as clojure.pprint/pprint."
  [x &
   {:keys [print-length print-level print-meta print-readably max-width]
    :or {print-length nil
         print-level nil
         print-meta false
         print-readably true
         max-width 72}}]
  `(is (= (binding [cpp/*print-right-margin* ~max-width
                    *print-length* ~print-length
                    *print-level* ~print-level
                    *print-meta* ~print-meta
                    *print-readably* ~print-readably]
            (with-out-str (cpp/pprint ~x)))
         (binding [*print-length* ~print-length
                   *print-level* ~print-level
                   *print-meta* ~print-meta
                   *print-readably* ~print-readably]
           (with-out-str (sut/pprint ~x {:max-width ~max-width}))))))
