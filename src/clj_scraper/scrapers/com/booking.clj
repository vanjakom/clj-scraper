(ns clj-scraper.scrapers.com.booking
  (:require
   [clj-common.logging :as logging]
   [clj-common.http :as http]
   [clj-common.localfs :as fs]
   [clj-scraper.scrapers.retrieve :as retrieve]
   [net.cgrand.enlive-html :as html]))


(def configuration
  {:keep-for -1})

(defn retrieve-hotel-from-my-bookings [url]
  (let [dom (nth
             (html/html-resource
              (retrieve/retrieve
               (assoc
                configuration
                :url url)))
             3)
        hotel-url (:href
                   (:attrs
                    (first
                     (html/select
                      dom
                      [[:h1 (html/attr= :class "mb-h1 mb-hotel-name bui_font_large ")] :a]))))
        location-data (first
                       (html/select
                        dom
                        [[:a (html/attr= :class "pb_myres_map_show_map js-pb-conf-show-map")]]))]
    {
     :name (:data-hotel-name (:attrs location-data))
     :longitude (Double/parseDouble (:data-location-long (:attrs location-data)))
     :latitude (Double/parseDouble (:data-location-lat (:attrs location-data)))
     :url (first (.split hotel-url "\\?"))}))



