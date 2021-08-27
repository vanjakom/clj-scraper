(ns clj-scraper.scrapers.org.wikipedia
  (:use
   clj-common.clojure)
  (:require
   [clj-common.io :as io]
   [clj-common.json :as json]
   [clj-scraper.scrapers.retrieve :as retrieve]))

(defn title-encode
  "To be used when title is passed in url"
  [title]
  (.replace
   title
   " "
   "_"))

;; alternative
;; https://sr.wikipedia.org/w/api.php?action=parse&prop=wikitext&formatversion=2&format=json&page=Списак_споменика_културе_у_Златиборском_округу
(defn title->wikitext
  "Note title needs to be encoded, space -> _"
  [language title]
  (with-open [is (retrieve/retrieve
                  ;; temorary fix
                  (assoc
                   retrieve/*configuration*
                   :url
                   (str "https://" language ".wikipedia.org/wiki/" title "?action=raw")))]
    (doall
     (io/input-stream->line-seq is))))

#_(title->wikitext "sr" "Списак_споменика_културе_у_Златиборском_округу")

(defn title->metadata
  "Note title should be raw string. Follows redirects and returns title redirect."
  [language title]
  (with-open [is (retrieve/retrieve
                  (assoc
                   retrieve/*configuration*
                   :url
                   (str
                    "https://" language ".wikipedia.org/w/api.php?action=query&prop=pageprops&ppprop=wikibase_item&redirects=1&format=json&utf8=1&titles="
                    (url-encode title))))]
    (when is
      (let [page (second (first (get-in (json/read-keyworded is) [:query :pages])))]
        {
         :title (:title page)
         :wikidata-id (:wikibase_item (:pageprops page))}))))

#_(title->metadata "sr" "Београд")
#_(title->metadata "sr" "Средњовековни град Ковин (Јеринин град)")
