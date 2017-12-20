(ns clj-quiqup-test.submit-job-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-quiqup.core :as quiqup]
    [clj-quiqup-test.fake-http :as fake-http]))


(def quiqup-host "https://api-staging.quiqup.localhost")

(def submit-job (partial quiqup/submit-job quiqup-host "token"))

(def json-handler (partial fake-http/json-handler quiqup-host))


(def submission-req
  {:earliest_collection_at "2017-12-14T13:49:44Z"})

(def submission-response
  {:state "pending_assignment"
   :earliest_collection_at "2017-12-14T15:49:44Z"})


(deftest submit-job-test
  (testing "should create job"
    (with-fake-routes (json-handler "/partner/jobs/1/submissions" submission-response 200 :post)
      (let [response (submit-job 1 submission-req)]
        (is (= 200 (:status response)))
        (is (= submission-response (:body response))))))

  (testing "should return bad request"
    (with-fake-routes (json-handler "/partner/jobs/asdf/submissions" "" 404 :post)
      (let [response (submit-job "asdf" submission-req)]
        (is (= 404 (:status response)))))))
