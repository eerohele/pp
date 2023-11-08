(ns me.flowthing.pp-test
  (:require [clojure.pprint :as cpp]
            [clojure.test :refer [deftest is]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer [for-all]]
            [time-literals.read-write :as time-literals]
            [me.flowthing.pp :as sut]))

(time-literals/print-time-literals-clj!)

(defn pp-str
  [x]
  (with-out-str (sut/pprint x)))

(defmacro $
  "Given an input and printing options, check that the SUT prints the
  input the same way as clojure.pprint/pprint."
  [input &
   {:keys [print-length print-level print-meta print-readably print-namespace-maps max-width]
    :or {print-length nil
         print-level nil
         print-meta false
         print-readably true
         print-namespace-maps false
         max-width 72}}]
  `(is (= (binding [cpp/*print-right-margin* ~max-width
                    *print-length* ~print-length
                    *print-level* ~print-level
                    *print-meta* ~print-meta
                    *print-readably* ~print-readably
                    *print-namespace-maps* ~print-namespace-maps]
            (with-out-str (cpp/pprint ~input)))
         (binding [*print-length* ~print-length
                   *print-level* ~print-level
                   *print-meta* ~print-meta
                   *print-readably* ~print-readably
                   *print-namespace-maps* ~print-namespace-maps]
           (with-out-str (sut/pprint ~input {:max-width ~max-width}))))))

(comment ($ {:a 1}) ,,,)

(deftest pprint
  ;; Basic
  ($ {})
  ($ [nil nil])
  ($ {:a 1})
  ($ '(1 nil))
  ($ {:a 1 :b 2 :c 3 :d 4} :max-width 24)
  ($ {:args [{:op :var :assignable? true}]} :max-width 24)
  ($ {:a 1 :b 2 :c 3 :d 4 :e 5} :max-width 24)
  ($ {:a 1 :b 2 :c 3 :d 4} :max-width 0)
  ($ {:a 1 :b 2 :c 3 :d 4 :e {:f 6}} :max-width 24)
  ($ {:a 1
      :b 2
      :c 3
      :d 4
      :e {:a 1 :b 2 :c 3 :d 4 :e {:f 6 :g 7 :h 8 :i 9 :j 10}}}
    :max-width 24)

  ;; Queues
  ($ (clojure.lang.PersistentQueue/EMPTY))
  ($ (conj (clojure.lang.PersistentQueue/EMPTY) 1))
  ($ (conj (clojure.lang.PersistentQueue/EMPTY) 1 2 3) :print-length 1)
  ($ (conj (clojure.lang.PersistentQueue/EMPTY) 1 2 3) :print-level 1)
  ($ (conj (clojure.lang.PersistentQueue/EMPTY) 1 2 3) :print-length 1 :print-level 1)
  ($ (conj (clojure.lang.PersistentQueue/EMPTY) 1 2 3) :max-width 6)

  ;; Max width
  ($ {:a 1 :b 2 :c 3 :d 4} :max-width 0)

  ;; Meta
  ($ (with-meta {:a 1} {:b 2}) :print-meta true)
  ($ (with-meta {:a 1} {:b 2}) :print-meta true :max-width 2)

  ;; Print level
  ($ {} :print-level 0)
  ($ {:a 1} :print-level 0)
  ($ {:a {:b 2}} :print-level 1)
  ($ {:a {:b 2}} :print-level 2)
  ($ {:a {:b 2}} :print-level 3)
  ($ {{:a 1} :b} :print-level 1)
  ($ {{:a 1} :b} :print-level 2)
  ($ {{:a 1} :b} :print-level 3)
  ($ '(:a (:b (:c (:d)))) :print-level 0)
  ($ '(:a (:b (:c (:d)))) :print-level 1)
  ($ '(:a (:b (:c (:d)))) :print-level 2)
  ($ '(:a (:b (:c (:d)))) :print-level 3)
  ($ '(:a (:b (:c (:d)))) :print-level 4)
  ($ '(:a (:b (:c (:d)))) :print-level 5)

  ;; Print length
  ($ '() :print-length 0)
  ($ [] :print-length 0)
  ($ #{} :print-length 0)
  ($ {} :print-length 0)
  ($ (range) :print-length 0)
  ($ (range) :print-length 1)
  ($ '(1 2 3) :print-length 0)
  ($ '(1 2 3) :print-length 1)
  ($ '(1 2 3) :print-length 2)
  ($ '(1 2 3) :print-length 3)
  ($ '(1 2 3) :print-length 4)

  ;; Print level and print length
  ($ {} :print-level 0 :print-length 0)
  ($ {} :print-level 1 :print-length 0)
  ($ {} :print-level 0 :print-length 1)
  ($ {} :print-level 1 :print-length 1)

  ($ {:a 1 :b 2} :print-level 0 :print-length 0)
  ($ {:a 1 :b 2} :print-level 1 :print-length 0)
  ($ {:a 1 :b 2} :print-level 0 :print-length 1)
  ($ {:a 1 :b 2} :print-level 1 :print-length 1)

  ;; Width
  ($ {[]
      [-1000000000000000000000000000000000000000000000000000000000000000N]}
    :max-width 72)

  ;; Reader macros
  ($ #'map)
  ($ '(#'map))
  ($ '#{#'map #'mapcat})
  ($ '{:arglists (quote ([xform* coll])) :added "1.7"})
  ($ '@(foo))
  ($ ''foo)
  ($ '~foo)
  ($ '('#{boolean char floats}) :max-width 23)
  ($ '('#{boolean char floats}) :max-width 23 :print-level 0)
  ($ '('#{boolean char floats}) :max-width 23 :print-length 0)
  ($ '('#{boolean char floats}) :max-width 23 :print-length 3)

  ;; Namespace maps
  ($ {:a/b 1} :print-namespace-maps true)
  ($ {:a/b 1 :a/c 2} :print-namespace-maps true)
  ($ {:a/b 1 :c/d 2} :print-namespace-maps true)
  ($ {:a/b {:a/b 1}} :print-namespace-maps true)
  ($ {'a/b 1} :print-namespace-maps true)
  ($ {'a/b 1 'a/c 3} :print-namespace-maps true)
  ($ {'a/b 1 'c/d 2} :print-namespace-maps true)
  ($ {'a/b {'a/b 1}} :print-namespace-maps true)
  ($ {:a/b 1} :print-namespace-maps false)
  ($ {:a/b 1 :a/c 2} :print-namespace-maps false)
  ($ {:a/b 1 :c/d 2} :print-namespace-maps false)
  ($ {:a/b {:a/b 1}} :print-namespace-maps false)
  ($ {'a/b 1} :print-namespace-maps false)
  ($ {'a/b 1 'a/c 3} :print-namespace-maps false)
  ($ {'a/b 1 'c/d 2} :print-namespace-maps false)
  ($ {'a/b {'a/b 1}} :print-namespace-maps false)

  ($ (struct (create-struct :q/a :q/b :q/c) 1 2 3))

  ($ #:a{:b 1 :c 2} :max-width 14 :print-namespace-maps true)
  ($ #{'a/b 1 'a/c 2} :max-width 14 :print-namespace-maps true)

  ;; Custom tagged literals
  ($ #time/date "2023-10-02")

  ;; Sorted maps
  ($ (sorted-map))
  ($ (sorted-map :a 1 :b 2))

  ($ (sorted-map :a 1 :b 2) :print-length 1)

  ;; Sorted sets
  ($ (sorted-set))
  ($ (sorted-set 1 2 3))
  ($ (sorted-set 1 2 3) :print-length 1)

  ;; Symbolic
  ($ ##Inf)
  ($ ##-Inf)
  ($ ##NaN))

(deftest pprint-meta
  ;; clojure.pprint prints this incorrectly with meta
  (binding [*print-meta* true *print-readably* false]
    (is (= "{:a 1}\n" (pp-str (with-meta {:a 1} {:b 2})))))

  (binding [*print-meta* true]
    (is (= "{:a 1}\n" (pp-str (with-meta {:a 1} {}))))))

(defrecord R [x])

(deftest pprint-record
  ;; unlike pr, clojure.pprint doesn't print records with the
  ;; fully-qualified record name in the prefix.
  (is (= (with-out-str (prn (->R 1)))
        (with-out-str (sut/pprint (->R 1)))))

  (is (= "#me.flowthing.pp_test.R{:x\n                        {:a 1,\n                         :b 2,\n                         :c 3,\n                         :d 4}}\n"
        (with-out-str (sut/pprint (->R {:a 1 :b 2 :c 3 :d 4})
                        {:max-width 31})))))

(deftest pprint-reader-macro-edge-cases
  ;; do not print the reader macro character if the collection following the
  ;; character exceeds print level
  (is (= "#\n"
        (binding [*print-level* 0]
          (with-out-str (sut/pprint '('#{boolean char floats}))))))
  (is (= "(#)\n"
        (binding [*print-level* 1]
          (with-out-str (sut/pprint '('#{boolean char floats}))))))
  (is (= "(#)\n"
        (binding [*print-length* 1 *print-level* 1]
          (with-out-str (sut/pprint '('#{boolean char floats}))))))

  ;; reader macro characters do not count towards *print-length*
  (is (= "(...)\n"
        (binding [*print-length* 0]
          (with-out-str (sut/pprint '('#{boolean char floats}))))))
  (is (= "('#{boolean ...})\n"
        (binding [*print-length* 1]
          (with-out-str (sut/pprint '('#{boolean char floats})))))))

(deftype T
  [xs]
  clojure.lang.Associative
  (assoc [_ k v]
    (T. (.assoc xs k v))))

(def obj-re
  #"#object\[me.flowthing.pp_test.T 0[xX][0-9a-fA-F]+ \"me.flowthing.pp_test.T@[0-9a-fA-F]+\"\]\n")

(deftest pprint-custom-type
  (is (re-matches obj-re (with-out-str (prn (T. {:a 1})))))
  (is (re-matches obj-re (with-out-str (cpp/pprint (T. {:a 1})))))
  (is (re-matches obj-re (with-out-str (sut/pprint (T. {:a 1})))))

  (binding [*print-level* 0]
    (is (re-matches obj-re (with-out-str (sut/pprint (T. {:a 1})))))))

(deftest pprint-dup
  (binding [*print-dup* true]
    (doseq [x [1
               1.0
               1N
               1M
               "foo"
               {:a 1}
               [:a :b :c]
               #{:a :b :c}
               (java.math.BigInteger. "1")
               #'map
               #(inc 1)
               (doto (java.util.HashMap.) (.put :a 1))
               \a
               1/2
               #"[a-z]"
               (find-ns 'user)
               (java.util.Date.)
               (java.util.UUID/randomUUID)
               (->R 1)]]
      (is (= (str (print-str x) \newline) (pp-str x))))))

(defspec roundtrip 10000
  (for-all [x gen/any-printable-equatable
            print-dup gen/boolean]
    (= x
      (read-string
        (with-out-str
          (binding [*print-length* nil
                    *print-level* nil
                    *print-dup* print-dup]
            (sut/pprint x)))))))

;; With infinite max width, prints everything the same way as prn.
(defspec print-linear 10000
  (for-all [x gen/any-printable-equatable]
    (=
      (with-out-str (sut/pprint x {:max-width ##Inf}))
      (with-out-str (prn x)))))

(defspec pp-vs-cpp-vec 1000
  (for-all [print-level gen/nat
            print-length gen/nat
            x (gen/vector gen/int)]
    (binding [*print-level* print-level
              *print-length* print-length]
      (= (with-out-str (sut/pprint x)) (with-out-str (cpp/pprint x))))))

(defspec pp-vs-cpp-map-1 1000
  (for-all [print-length gen/nat
            print-level gen/nat
            x (gen/map gen/keyword gen/int)]
    (binding [*print-level* print-level
              *print-length* print-length]
      (= (with-out-str (sut/pprint x)) (with-out-str (cpp/pprint x))))))

(defspec pp-vs-cpp-map-2 1000
  (for-all [print-length gen/nat
            print-level gen/nat
            print-namespace-maps gen/boolean
            x (gen/map gen/keyword-ns gen/int)]
    (binding [*print-level* print-level
              *print-length* print-length
              *print-namespace-maps* print-namespace-maps]
      (= (with-out-str (sut/pprint x)) (with-out-str (cpp/pprint x))))))

(defspec pp-vs-cpp-map-3 75
  (for-all [print-namespace-maps gen/boolean
            x (gen/map (gen/one-of [gen/keyword-ns gen/symbol-ns])
                gen/any-printable-equatable)]
    (binding [*print-namespace-maps* print-namespace-maps]
      (=
        (with-out-str
          (sut/pprint x {:max-width ##Inf}))
        (with-out-str
          (binding [cpp/*print-right-margin* ##Inf]
            (cpp/pprint x)))))))

;; A generative test that checks that pp/print and cpp/print print any
;; gen/any-printable-equatable the same way would be great, but
;; cpp/print sometimes prints things in miser mode even when there's
;; enough space to use linear mode, and I don't want to have my impl do
;; that.
;;
;; With infinite max width, however, me.flowthing.pp prints everything
;; the same way as clojure.pprint.
(defspec pp-vs-cpp-inf-max-with 1000
  (for-all [x gen/any-printable-equatable]
    (binding [cpp/*print-right-margin* ##Inf]
      (=
        (with-out-str (sut/pprint x {:max-width ##Inf}))
        (with-out-str (cpp/pprint x))))))

(defspec print-readably 1000
  (for-all [x (gen/one-of [gen/string (gen/vector gen/char)])
            print-readably gen/boolean]
    (binding [*print-readably* print-readably]
      (=
        (with-out-str (sut/pprint x))
        (with-out-str (cpp/pprint x))))))
