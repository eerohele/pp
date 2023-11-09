(ns me.flowthing.pp-struct-test
  (:require [clojure.test :refer [deftest]]
            [me.flowthing.pp.test :refer [$]]))

(deftest pprint-struct
  ($ (struct (create-struct :q/a :q/b :q/c) 1 2 3)))
