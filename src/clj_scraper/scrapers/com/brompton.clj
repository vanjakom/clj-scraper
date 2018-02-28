(ns clj-scraper.scrapers.com.brompton)

(require '[clj-scraper.scrapers.retrieve :as retrieve])
(require '[net.cgrand.enlive-html :as html])


(defn dealer-page [{sid :sid aid :aid :as configuration}]
  (let [response (html/html-resource
                   (retrieve/retrieve
                     (assoc
                       configuration
                       :url
                       (str "https://www.brompton.com/Find-a-Store/Store?sid=" sid "&aid=" aid))))]


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




    response))




(dealer-page {:sid 6109 :aid 2489})
