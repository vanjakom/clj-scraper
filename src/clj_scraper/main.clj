(ns clj-scraper.main)

(require '[clj-scraper.server :as server])

; todo currently all available scrapers need to be required

(require 'clj-scraper.scrapers.de.bike-components)
(require 'clj-scraper.scrapers.rs.vipsistem)

(defn start []
  (server/start-server))

(defn -main [& args]
  (start))
