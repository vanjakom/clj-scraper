(ns clj-scraper.scrapers.org.wikidata
  (:use
   clj-common.clojure)
  (:require
   [clj-common.json :as json]
   [clj-scraper.scrapers.retrieve :as retrieve]))

(def configuration
  {:keep-for (* 30 24 60 60 1000)})

(defn entity [id]
  (second
   (first
    (:entities
     (json/read-keyworded
      (retrieve/retrieve
       (assoc
        configuration
        :url
        (str "http://www.wikidata.org/wiki/Special:EntityData/" id ".json"))))))))

(defn wikipedia-title [language title]
  (second
   (first
    (:entities
     (json/read-keyworded
      (retrieve/retrieve
       (assoc
        configuration
        :url
        (str
         "https://www.wikidata.org/w/api.php?action=wbgetentities&sites="
         language "wiki&format=json&titles=" (url-encode title)))))))))
