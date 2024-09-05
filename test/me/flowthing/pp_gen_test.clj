(ns me.flowthing.pp-gen-test
  (:require [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :refer [for-all]]
            [me.flowthing.pp.test :refer [pp cpp]]))

(defspec roundtrip 10000
  (for-all [x gen/any-printable-equatable
            print-dup gen/boolean
            map-entry-separator (gen/elements #{"," ""})]
    (= x
      (read-string
        (binding [*print-length* nil
                  *print-level* nil
                  *print-dup* print-dup]
          (pp x :map-entry-separator map-entry-separator))))))

(defspec level-zero-map-entry 10000
  (for-all [max-width gen/nat
            print-level gen/nat
            print-length gen/nat
            {:keys [me v]}
            (gen/fmap (fn [[k v]] {:me (clojure.lang.MapEntry. k v) :v [k v]})
              (gen/tuple
                gen/any-printable-equatable
                gen/any-printable-equatable))]
    (= (pp me :max-width max-width :print-level print-level :print-length print-length)
      (pp v :max-width max-width :print-level print-level :print-length print-length))))

(defspec pp-vs-cpp-map-entry 1000
  (for-all [print-level gen/nat
            print-length gen/nat
            x (gen/fmap (fn [[k v]] (clojure.lang.MapEntry. k v))
                (gen/tuple
                  gen/any-printable-equatable
                  gen/any-printable-equatable))]
    (= (pp x :max-width ##Inf :print-level print-level :print-length print-length)
      (cpp x :max-width ##Inf :print-level print-level :print-length print-length))))

(defspec pp-vs-cpp-vec 1000
  (for-all [print-level gen/nat
            print-length gen/nat
            x (gen/vector gen/int)]
    (= (pp x :print-level print-level :print-length print-length)
      (cpp x :print-level print-level :print-length print-length))))

(defspec pp-vs-cpp-map-1 1000
  (for-all [print-length gen/nat
            print-level gen/nat
            x (gen/map gen/keyword gen/int)]
    (= (pp x :print-level print-level :print-length print-length)
      (cpp x :print-level print-level :print-length print-length))))

(defspec pp-vs-cpp-map-2 1000
  (for-all [print-length gen/nat
            print-level gen/nat
            print-namespace-maps gen/boolean
            x (gen/map gen/keyword-ns gen/int)]
    (= (pp x :print-level print-level :print-length print-length :print-namespace-maps print-namespace-maps)
      (cpp x :print-level print-level :print-length print-length :print-namespace-maps print-namespace-maps))))

(defspec pp-vs-cpp-map-3 75
  (for-all [print-namespace-maps gen/boolean
            x (gen/map (gen/one-of [gen/keyword-ns gen/symbol-ns])
                gen/any-printable-equatable)]
    (= (pp x :max-width ##Inf :print-namespace-maps print-namespace-maps)
      (cpp x :max-width ##Inf :print-namespace-maps print-namespace-maps))))

;; A generative test that checks that pp/print and cpp/print print any
;; gen/any-printable-equatable the same way would be great, but
;; cpp/print sometimes prints things in miser mode even when there's
;; enough space to use linear mode, and I don't want to have my impl do
;; that.
;;
;; With infinite max width, however, me.flowthing.pp prints everything
;; the same way as clojure.pprint.
(defspec pp-vs-cpp-inf-max-width 1000
  (for-all [x gen/any-printable-equatable]
    (= (pp x :max-width ##Inf) (cpp x :max-width ##Inf))))

(def ^:private print-readably-edge-case
  "clojure.pprint prints this differently than prn or pp/pprint, causing the
  generative test below to fail sometimes"
  (apply str (map char [9 133])))

(defspec print-readably 1000
  (for-all [x (gen/one-of [gen/string (gen/vector gen/char)])
            print-readably gen/boolean]
    (or (= x print-readably-edge-case)
      (= (pp x :print-readably print-readably) (cpp x :print-readably print-readably)))))
