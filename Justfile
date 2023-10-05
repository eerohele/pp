test:
  clojure -A:dev -X cognitect.test-runner.api/test
  clojure -X:dev user/xr! '{:path "check"}'

bench:
  clojure -X:dev user/xr! '{:path "bench"}'

release:
  clojure -T:build release
