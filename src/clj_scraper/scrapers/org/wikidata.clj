(ns clj-scraper.scrapers.org.wikidata
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
        (str " http://www.wikidata.org/wiki/Special:EntityData/" id ".json"))))))))
