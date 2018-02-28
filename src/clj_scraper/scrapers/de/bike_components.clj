(ns clj-scraper.scrapers.de.bike-components)

(require '[clj-scraper.scrapers.retrieve :as retrieve])
(require '[net.cgrand.enlive-html :as html])

(defn details-page [configuration]
  (let [response (html/html-resource (retrieve/retrieve configuration))]
    {
      :title (.trim
               (html/text
                 (first
                   (html/select
                     response
                     [[:#module-product-detail] [:.site-headline-special]]))))
      :price (Double/parseDouble
               (->
                 (html/text
                   (first (html/select response [:#module-product-detail-price])))
                 (.trim)
                 (.replace "€" "")
                 (.replace "," ".")))
      :availability (.trim
                      (html/text
                        (first (html/select response [:#module-product-detail-stock]))))}))

(defn monitor-price [configuration result]
  (let [price (:price result)
        wanted-price (:wanted-price configuration)]
    (if (= price wanted-price)
      (assoc
        result
        :monitor-price-state :ok
        :monitor-price-description (str price))
      (assoc
        result
        :monitor-price-state :alarm
        :monitor-price-description (str price "/" wanted-price)))))

(defn monitor-availability [configuration result]
  (if (= (:availability result) "in stock")
    (assoc
      result
      :monitor-availability-state :ok
      :monitor-availability-description (:availability result))
    (assoc
      result
      :monitor-availability-state :ok
      :monitor-availability-description (:availability result))))


(defn view-price-availability [configuration result]
  {
    :name (:title result)
    :monitor (if (or
                 (= :alarm (:monitor-price-state result))
                 (= :alarm (:monitor-availability-state result)))
             :alarm
             :ok)
    :description [(:monitor-price-description result)
                  "</br>"
                  (:monitor-availability-description result)]})


