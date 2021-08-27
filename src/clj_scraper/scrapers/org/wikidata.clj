(ns clj-scraper.scrapers.org.wikidata
  (:use
   clj-common.clojure)
  (:require
   [clj-common.json :as json]
   [clj-scraper.scrapers.retrieve :as retrieve]))

(defn entity [id]
  (second
   (first
    (:entities
     (json/read-keyworded
      (retrieve/retrieve
       (assoc
        retrieve/*configuration*
        :url
        (str "https://www.wikidata.org/wiki/Special:EntityData/" id ".json"))))))))

(defn wikipedia-title [language title]
  (second
   (first
    (:entities
     (json/read-keyworded
      (retrieve/retrieve
       (assoc
        retrieve/*configuration*
        :url
        (str
         "https://www.wikidata.org/w/api.php?action=wbgetentities&sites="
         language "wiki&format=json&normalize=yes&titles=" (url-encode title)))))))))
