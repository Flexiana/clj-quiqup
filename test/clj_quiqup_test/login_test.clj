(ns clj-quiqup-test.login-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-quiqup.core :as quiqup]
    [clj-quiqup-test.fake-http :as fake-http]))


(def quiqup-host "https://api-staging.quiqup.localhost")

(def login (partial quiqup/login quiqup-host))

(def json-handler (partial fake-http/json-handler quiqup-host))

(def login-response
    {:access_token "0ffe15dd8d21752d5d2731cef80dae1380788c9c4282a82bca48d259459caa6f"
     :token_type "bearer"
     :expires_in 3600})

(def login-response-error
    {:error "invalid_client"
     :error_description "Client authentication failed due to unknown client"})


(deftest login-test
  (testing "should return a token"
    (with-fake-routes (json-handler "/oauth/token" login-response 200 :post)
      (let [response (login "client-id" "client-secret")]
        (is (= 200 (:status response)))
        (is (= login-response (:body response))))))

  (testing "should return 400, due invalid credentials"
    (with-fake-routes (json-handler "/oauth/token" login-response-error 401 :post)
      (let [response (login "client-id" "client-secret")]
        (is (= 401 (:status response)))
        (is (= login-response-error (:body response)))))))

