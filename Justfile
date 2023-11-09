test-bb:
  bb test/run.bb

npm:
  npm install

test-cljs: npm
  npx shadow-cljs compile test && node out/test.js

test-clj:
  clojure -A:dev -X cognitect.test-runner.api/test
  clojure -X:dev user/xr! '{:path "check"}'

test: test-bb test-cljs test-clj

bench:
  clojure -X:dev user/xr! '{:path "bench"}'

release:
  clojure -T:build release
  mvn deploy
