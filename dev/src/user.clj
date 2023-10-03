(ns user)

(defn xr!
  ([]
   (xr! {:path "check"}))
  ([{:keys [path]}]
   (run! (requiring-resolve 'cognitect.transcriptor/run)
     ((requiring-resolve 'cognitect.transcriptor/repl-files) (format "repl/%s" path)))))

(comment
  (xr!)
  ,,,)
