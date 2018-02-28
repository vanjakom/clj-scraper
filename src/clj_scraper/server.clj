(ns clj-scraper.server)

(require '[clj-common.http-server :as http])
(require '[clj-common.io :as io])
(require '[clj-common.ring-middleware :as middleware])
(require '[clj-scraper.scrapers.core :as scrapers])

(defonce server-handle (atom nil))

(defn status-handler [request]
  (let [results (scrapers/process-all
                  (clj-common.edn/read-object
                    (clj-common.jvm/resource-as-stream ["configuration.edn"])))]
    {
      :status 200
      :headers {
                 "Content-Type" "text/html"}
      :body (io/seq->input-stream
              [
                "<html>"
                "<head><meta charset=\"UTF-8\"></head>"
                "<body>"
                "<table width=\"100%\" cellpadding="5">"
                (map
                  (fn [result]
                    (let [color (if (= (:monitor result) :alarm) "#FF0000" "#008000")]
                      [
                        "<tr bgcolor=\"" color "\">"
                        "<td width=\"40%\" padding=\"10px\">"
                        "<b>" (:name result) "</b>"
                        "</td>"
                        "<td width=\"60%\" align=\"center\">"
                        (:description result)
                        "</td>"
                        "</tr>"]))
                  results)
                "</table>"
                "</body></html>"])}))


(comment
  (io/input-stream->string
    (:body (status-handler {})))

  (require 'clojure.java.io)
  (clojure.java.io/copy
    (:body (status-handler {}))
    System/out)

  )


(def handler
  (compojure.core/routes
    (compojure.core/GET
      "/status"
      _
      (middleware/wrap-exception-to-logging
        status-handler))))

(defn stop-server []
  (if-let [server @server-handle]
    (.stop server)))

(defn start-server []
  (swap!
    server-handle
    (fn [_]
      (http/create-server 7777 #'handler))))
