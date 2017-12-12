(defproject clj-quiqup "0.0.0"
  :description "A client for Quiqup API based on clj-http.client"
  :url "https://github.com/flexiana/clj-quiqup"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "3.7.0"]
                 [com.cemerick/url "0.1.1"]
                 [cheshire "5.8.0"]]

  :profiles {:dev {:plugins [[lein-cloverage "1.0.10"]
                             [lein-kibit "0.1.6"]]
                   :dependencies [[clj-http-fake "1.0.3"]]}})
