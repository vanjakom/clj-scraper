(ns clj-scraper.scrapers.rs.vipsistem)

(require '[clj-scraper.scrapers.retrieve :as retrieve])
(require '[net.cgrand.enlive-html :as html])

(defn exchange-list [configuration]
  (let [response (html/html-resource (retrieve/retrieve configuration))]
    (reduce
      (fn [response exchange]
        (let [divs (html/select (:content exchange) [[:div]])
              description (html/text (nth divs 1))
              currency (html/text (nth divs 2))
              selling (html/text (nth divs 3))
              buying (html/text (nth divs 5))]
          (assoc
            response
            (keyword currency)
            {
              :desciption description
                          :selling selling
            :buying buying})))
      {
        :date (->
                (html/text
                  (first
                    (html/select
                      response
                      [[:.page-header] [:.subtitle]])))
                (.replace "Na dan " "")
                (.trim))
        }
      (html/select response [[:.rates-table-row]]))))

(defn view-currency [configuration result]
  (let [currency (:currency configuration)
        exchange (get result currency)]
    {
      :name (str "exchange on vipsistem.rs for " (name currency))
      :monitor :ok
      :description [(:selling exchange) " / " (:buying exchange)]}))

(comment
  (view-currency
    {:currency :EUR}
    (exchange-list {:url "http://www.vipsistem.rs/kursna-lista.php"}) )

  (def response (html/html-resource (retrieve/retrieve {:url "http://www.vipsistem.rs/kursna-lista.php"})))


  (html/text (second (rest (html/select response [[:.rates-table-row] [:div]]))))

  (first (map
    (fn [exchange]
      (println exchange)
      (let [divs (html/select (:content exchange) [[:div]])
            description (html/text (nth divs 1))
            currency (html/text (nth divs 2))
            selling (html/text (nth divs 3))
            buying (html/text (nth divs 5))]
      {
        :currency currency
        :desciption description
        :selling selling
        :buying buying}))
    (rest (html/select response [[:.rates-table-row]]))))

)
