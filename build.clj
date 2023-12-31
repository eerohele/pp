(ns build
  (:require [clojure.string :as string]
            [clojure.tools.build.api :as build])
  (:import (java.time LocalDate)))

(defn tag
  [_]
  (let [version (format "%s.%s" (LocalDate/now) (build/git-count-revs nil))]
    (build/git-process {:git-args ["tag" "--message" version "--annotate" version]})
    {:git/tag version :git/sha (build/git-process {:git-args ["rev-parse" "--short" version]})}))

(comment
  (tag nil)
  ,,,)

(defn bump-coords
  [{:git/keys [tag sha]}]
  (build/process {:command-args ["mvn" "versions:set" (format "-DnewVersion=%s" tag)]})
  (spit "README.md"
    (->
      (slurp "README.md")
      (string/replace #"(?im)(\{:mvn/version \".+?\"\})" (format "{:mvn/version \"%s\"}" tag))
      (string/replace #"(?im)(\{:git/tag \".+?\" :git/sha \".+?\"\})"
        (format "{:git/tag \"%s\" :git/sha \"%s\"}" tag sha)))))

(defn release
  [_]
  (build/git-process {:git-args ["commit" "README.md" "--message" "Update README\n\n[skip ci]"]})
  (bump-coords (tag nil)))

(comment
  (release nil)
  ,,,)
