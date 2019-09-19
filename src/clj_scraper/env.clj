(ns clj-scraper.env)

(require '[clj-common.path :as path])

(def ^:dynamic *cache-path*
  (path/string->path
    "/tmp/clj-scraper/cache"))
