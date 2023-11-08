# Change log

All notable changes to this project will be documented in this file.

## UNRELEASED

- Add Maven (Clojars) release
- Fix reader macro edge case
- Add `:map-entry-separator` option

  The `:map-entry-separator` option lets you tell pp not to print
  commas. For example

  ```clojure
  user=> (pp/pprint {:a 1 :b 2} {:map-entry-separator ""})
  {:a 1 :b 2}
  nil
  ```

## 2023-10-05.5

- Initial release
