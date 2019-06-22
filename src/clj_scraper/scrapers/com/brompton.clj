(ns clj-scraper.scrapers.com.brompton
  (:require [clj-common.http :as http])
  (:require [clj-scraper.scrapers.retrieve :as retrieve])
  (:require [net.cgrand.enlive-html :as html]))

(def configuration
  {:keep-for (* 30 24 60 60 1000)})

(defn dealers-a-z-page [configuration]
  (let [response (html/html-resource
                   (retrieve/retrieve
                     (assoc
                       configuration
                       :url
                       "https://www.brompton.com/Find-a-Store/A-Z")))]
    (map
      (fn [entry]
        (let [path (:href (:attrs entry))
              params (http/parse-query-string path)]
          {
            :url (str "https://www.brompton.com" path)
            :sid (Long/parseLong (:sid params))
            :aid (Long/parseLong (:aid params))
            :name (first (:content entry))}))
      (html/select
        response
        [:article :div :a]))))

(defn dealer-page [{sid :sid aid :aid :as configuration}]
  (let [response (html/html-resource
                   (retrieve/retrieve
                     (assoc
                       configuration
                       :url
                       (str "https://www.brompton.com/Find-a-Store/Store?sid=" sid "&aid=" aid))))
        raw-type (html/text
               (first
                 (html/select
                   response
                   [:.content :#main_0_salesType])))]
    {
      :name    (clojure.string/trim
                 (html/text
                   (first
                     (html/select
                       response
                       [:.content :h2]))))
      :url (:href (:attrs (first (html/select
                                   response
                                   [:.store-data :.address :#main_0_websiteLink]))))
      :longitude (let [longitude-s (:data-lng
                                     (:attrs
                                       (first (html/select
                                                response
                                                [:.media :#gmap]))))]
                   (if (empty? longitude-s)
                     nil
                     (Double/parseDouble longitude-s)))
      :latitude (let [latitude-s (:data-lat
                                   (:attrs
                                     (first (html/select
                                              response
                                              [:.media :#gmap]))))]
                  (if (empty? latitude-s)
                    nil
                    (Double/parseDouble latitude-s)))
      :raw-type raw-type
      :type
              (condp = raw-type
                "Premier Store" :premier-store
                "Brompton Junction Store" :brompton-junction
                :unknown)}))

(defn obtain-dealers []
  (map
    (fn [dealer]
      (dealer-page (merge configuration dealer)))
    (dealers-a-z-page configuration)))
