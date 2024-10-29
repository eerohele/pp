```
      ___           ___
     /\  \         /\  \
    /::\  \       /::\  \
   /:/\:\  \     /:/\:\  \
  /::\~\:\  \   /::\~\:\  \
 /:/\:\ \:\__\ /:/\:\ \:\__\
 \/__\:\/:/  / \/__\:\/:/  /
      \::/  /       \::/  /
       \/__/         \/__/
```

[![0 dependencies!](https://0dependencies.dev/0dependencies.svg)](https://0dependencies.dev)

A fast, single-namespace, no-dependency pretty-printer for data (not code).

Supports [Clojure](https://clojure.org), [ClojureScript](https://clojurescript.org), and [Babashka](https://babashka.org).

## Features

- [Fast](https://github.com/eerohele/pp/actions/workflows/bench.yml)[^1] (~14x-10x faster than [`fipp.edn/pprint`](https://github.com/brandonbloom/fipp) and ~85x–16x faster than [`clojure.pprint/pprint`](https://clojure.github.io/clojure/clojure.pprint-api.html#clojure.pprint/pprint) at Fipp's benchmark)
- [Small](https://github.com/eerohele/pp/blob/main/src/me/flowthing/pp.cljc) (under ~500 lines of code, not counting comments)
- Zero dependencies
- Single namespace; either use as a dependency or vendor into your codebase
- Output [similar](#differences-to-clojurepprint) to `clojure.pprint/pprint`
- Allocates conservatively (~28x fewer allocations than `clojure.pprint/pprint`, ~12x fewer than `fipp.edn/pprint`)

## Non-goals

- [Formatting code](https://journal.stuffwithstuff.com/2015/09/08/the-hardest-program-ive-ever-written/#4)
- API for large-scale [output](https://clojure.github.io/clojure/clojure.pprint-api.html#clojure.pprint/with-pprint-dispatch) [customization](https://github.com/brandonbloom/fipp#idiomatic); to customize, vendor and change whatever you want
- [`cl-format`](https://clojure.github.io/clojure/clojure.pprint-api.html#clojure.pprint/cl-format)
- Syntax coloring (á là [Puget](https://github.com/greglook/puget#syntax-coloring))

## Use

Either:

- Copy [`src/me/flowthing/pp.cljc`](https://github.com/eerohele/pp/blob/main/src/me/flowthing/pp.cljc) into your codebase and rename the namespace to avoid conflicts, or:
- Pull in via Maven or [Git](https://clojure.org/reference/deps_and_cli#_git):

    ```clojure
    ;; Maven coordinates
    me.flowthing/pp {:mvn/version "2024-09-09.69"}

    ;; Git coordinates
    io.github.eerohele/pp {:git/tag "2024-09-09.69" :git/sha "de4b20f"}
    ```

Then:

```clojure
user=> (require '[me.flowthing.pp :as pp])
nil
user=> (pp/pprint {:a 1 :b 2 :c 3 :d 4})
{:a 1, :b 2, :c 3, :d 4}
nil
user=> (pp/pprint {:a 1 :b 2 :c 3 :d 4} {:max-width 10})
{:a 1,
 :b 2,
 :c 3,
 :d 4}
nil
```

## API

```clojure
user=> (require '[me.flowthing.pp :as pp])
nil
user=> (clojure.repl/doc pp/pprint)
...
```

The API is stable and there will be no breaking changes. Everything except for `me.flowthing.pp/pprint` is internal and subject to change.

## Differences to clojure.pprint

Even though pp is not meant for formatting code, having it format the source of every var in the [`clojure.core`](https://clojure.github.io/clojure/clojure.core-api.html) namespace and comparing the output to that of `clojure.pprint/pprint` is a good exercise because `clojure.core` has a large variety of data structures and nesting levels.

In that exercise, [every difference](https://gist.github.com/eerohele/08e628ea9713c2e3e89df26f144c4edd) between the outputs of `me.flowthing.pp/pprint` and `clojure.pprint/pprint` is one where `clojure.pprint/pprint` doesn't make full use of the 72 character line width (default for both `clojure.pprint/pprint` and pp) even though it could.

Also, unlike `clojure.pprint/pprint`, pp prints records like `pr` does:

```clojure
user=> (defrecord R [x])
user.R
nil
user=> (prn (->R 1))
#user.R{:x 1}
nil
user=> (pp/pprint (->R 1))
#user.R{:x 1}
nil
user=> (clojure.pprint/pprint (->R 1))
{:x 1}
nil
```

In addition, there are one or two other minor, insignificant differences in where `clojure.pprint/pprint` and pp insert line breaks. If you spot these and they bother you, file an issue.

## Differences to Fipp

- pp uses `print-method` for pretty much everything except Clojure's built-in collection types. This means pp, unlike Fipp, prints things like [time-literals](https://github.com/henryw374/time-literals) the same way as `clojure.pprint/pprint`.
- Fipp prints `(fipp.edn/pprint '@foo)` as `(clojure.core/deref foo)`; pp, like `clojure.pprint/pprint`, prints it as `@foo`. The same with `quote`/`'`,  `var`/`#'`, and `unquote`/`~`.

## Notes on ClojureScript support

- ClojureScript doesn't support `*print-dup*`, so neither does pp.

## Applications

- [Tutkain](https://github.com/eerohele/Tutkain)
- [Tab](https://github.com/eerohele/tab)
- [Lingo](https://github.com/exoscale/lingo)
- [Bosquet](https://github.com/zmedelis/bosquet)
- [Metabase](https://github.com/metabase/metabase)
- [Penpot](https://github.com/penpot/penpot)
- [Fireworks](https://github.com/paintparty/fireworks)
- [Kushi](https://github.com/kushidesign/kushi)
- [Yetti](https://github.com/funcool/yetti)

## Acknowledgements

The algorithm pp uses is based on the ideas in [*Pretty-Printing, Converting List to Linear Structure*](https://dspace.mit.edu/handle/1721.1/5797) by Ira Goldstein (Artificial Intelligence, Memo No. 279 in Massachusetts Institute of Technology A.I. Laboratory, February 1973).

[^1]: I have only benchmarked the Clojure implementation. Cursory testing with [`simple-benchmark`](https://cljs.github.io/api/cljs.core/simple-benchmark) shows a ~6x improvement over [`cljs.pprint/pprint`](https://cljs.github.io/api/cljs.pprint/#pprint), but grain of salt and all that.
