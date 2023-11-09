#!/usr/bin/env bb

(require '[clojure.test :as t])
(require '[babashka.classpath :as cp])
(require '[babashka.deps :as deps])

(deps/add-deps '{:deps {org.clojure/test.check {:mvn/version "1.1.1"}
                        com.widdindustries/time-literals {:mvn/version "0.1.10"}}})

(cp/add-classpath "src:test")

(require 'me.flowthing.pp-test)
(require 'me.flowthing.pp-record-test)

(def test-results
  (t/run-tests 'me.flowthing.pp-test 'me.flowthing.pp-record-test))

(let [{:keys [fail error]} test-results]
  (when (pos? (+ fail error))
    (System/exit 1)))
