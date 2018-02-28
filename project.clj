(defproject com.mungolab/clj-scraper "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                  [org.clojure/clojure "1.8.0"]
                  [lein-light-nrepl "0.3.2"]

                  [com.mungolab/clj-common "0.2.0-SNAPSHOT"]
                  [enlive "1.1.6"]]
  :repl-options {
                  :nrepl-middleware [lighttable.nrepl.handler/lighttable-ops]}
  :main clj-scraper.main)
