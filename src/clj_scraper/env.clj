(ns clj-scraper.env)

(require '[clj-common.path :as path])

(def ^:dynamic *cache-path*
  (path/string->path
    "/Users/vanja/projects/clj-scraper/data/cache"))
