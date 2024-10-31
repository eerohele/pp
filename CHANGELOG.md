# Change log

All notable changes to this project will be documented in this file.

## UNRELEASED

- Improve printing of Java arrays.

  pp now prints Java arrays like clojure.pprint, but without commas:

  ```clojure
  user=> (clojure.pprint/pprint (char-array [\c \p \p]))
  [\c, \p, \p]
  user=> (me.flowthing.pp/pprint (char-array [\p \p]))
  [\p \p]
  ```

- Improve performance (by avoiding boxed math)

## 2024-09-09.69

- Print map entries (`clojure.lang.MapEntry`) like vectors everywhere except within maps #6

## 2024-01-04.60

- Print top-level map entries (`clojure.lang.MapEntry`) like vectors #5

  Prior to this change, pp printed top-level map entries without delimiters:

  ```clojure
  user=> (pp/pprint (clojure.lang.MapEntry. :a :b))
  :a :b
  nil
  ```

  After:

  ```clojure
  user=> (pp/pprint (clojure.lang.MapEntry. :a :b))
  [:a :b]
  nil
  ```

## 2024-01-04.57

- DOA, ignore

## 2023-11-25.47

- Add ClojureScript support
- Add explicit (unit tested) Babashka support
- Add `:map-entry-separator` option

  The `:map-entry-separator` option lets you tell pp not to print
  commas. For example:

  ```clojure
  user=> (pp/pprint {:a 1 :b 2} {:map-entry-separator ""})
  {:a 1 :b 2}
  nil
  ```

- Fix `*flush-on-newline*` behavior

  If `*flush-on-newline` is set to `true` (the default), like clojure.pprint, pp now only flushes after pretty-printing the entire form, instead of flushing after every newline.

- Fix reader macro edge case
- [Slightly improved performance](https://github.com/eerohele/pp/actions/runs/6990886744/job/19020851938#step:6:202)

## 2023-10-05.5

- Initial release
