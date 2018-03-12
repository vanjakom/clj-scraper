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





(comment

  (obtain-dealers)

  (def response (html/html-resource
                  (retrieve/retrieve
                    (assoc
                      {}
                      :url
                      "https://www.brompton.com/Find-a-Store/A-Z"
                      :keep-for
                      10000))))

  (dealer-page {
                 :keep-for 10000
                 :sid 5787
                 :aid 1762})
  ; {:name "2radtreff Huber e.U.", :url "http://www.2radtreff.at", :longitude 16.2932234, :latitude 48.144449, :type :unknown}


  (dealer-page {:sid 4352 :aid 243})
  ; {:name "*", :url "http://www.rockymountainrecumbents.com", :longitude nil, :latitude nil, :type :unknown}

  (def dealers (take 100 (obtain-dealers)))

  (count dealers)

  (count (dealers-a-z-page {}))
  (take 10 (dealers-a-z-page {}))

  (dealer-page {:sid 6109 :aid 2489})


  (def response (html/html-resource
                  (retrieve/retrieve
                    (assoc
                      {}
                      :url
                      "https://www.brompton.com/Find-a-Store/A-Z"))))

  (first response)

  ; {
  ; :tag :a,
  ; :attrs {
  ;    :id "main_2_letterRepeater_storeList_0_storeLink_0",
  ;    :href "/Find-a-Store/Store?sid=4506&aid=397"},
  ; :content ("-")}


  (map
    (fn [param]
      (let [pair (.split param "=")]
        [(first pair) (second pair)]))
    (.split "sid=4506&aid=397" "&"))



  (parse-query-string "Store?sid=4506&aid=397")

  (let [entry (first
                (html/select
                  response
                  [:article :div :a]))
        path (:href (:attrs entry))
        params (http/parse-query-string path)]
    {
      :url (str "https://www.brompton.com" path)
      :sid (:sid params)
      :aid (:aid params)
      :name (first (:content entry))})





  (def response (html/html-resource
                  (retrieve/retrieve
                    (assoc
                      {}
                      :url
                      (str "https://www.brompton.com/Find-a-Store/Store?sid=" 6109 "&aid=" 2489)))))


  (html/select
    response
    [:.content :#main_0_salesType]))


    ;
    ;<div class="content">
    ;    <h2>
    ;        Velofixer Flagey
    ;    </h2>
    ;    <div class="store-data">
    ;        <div class="address">
    ;            Place Eugene Flagey 32<br />Ixelle, Ixelle, 1050<br />Belgium
    ;            <br />
    ;                <abbr id="main_0_phoneWrapper" title="Phone">
    ;                <span class="address-icon icon-phone"></span>
    ;
    ;                  +32493 33 44 43
    ;                    <br />
    ;            </abbr>
    ;                <a id="main_0_websiteLink" href="http://www.velofixer.com/nl.html" target="_blank">http://www.velofixer.com/nl.html</a>
    ;            <br />
    ;            <a id="main_0_storeEmail" href="mailto:flagey@velofixer.com">flagey@velofixer.com</a>
    ;            </div>
    ;        </div>
    ; </div>
    ;<div class="media">
    ;    <script type="text/javascript" src="//maps.googleapis.com/maps/api/js"></script>
    ;    <a id="main_0_googleLink" class="label" href="https://www.google.com/maps/search/50.8286245,4.37251190000006" target="_blank">Open in Google Maps</a>
    ;        <div id="gmap" class="google-map" data-lng="4.37251190000006" data-lat="50.8286245" data-address="Place Eugene Flagey 32,Ixelle, 1050">





