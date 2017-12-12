(ns clj-quiqup-test.check-location-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-quiqup.core :as quiqup]
    [clj-quiqup-test.fake-http :as fake-http]))


(def quiqup-host "https://api-staging.quiqup.localhost")

(def check-location (partial quiqup/check-location quiqup-host))

(def json-handler (partial fake-http/json-handler quiqup-host))


(def body-422
  {:api_error {:attribute_errors [{:detail "The postcode provided is not valid."}]
               :code "invalid_postcode"
               :description (str "The postcode you provided does not match the format for a valid UK postcode."
                                 " Generally a valid postcode looks like \"NW10 6RB\"\n")
               :http_status_code 422
               :human "The postcode provided is not valid."
               :id 5003
               :message "The postcode provided is not valid."}
   :error "The postcode provided is not valid."
   :error_detail "The postcode provided is not valid."
   :error_details [{:detail "The postcode provided is not valid."}]
   :errors [{:detail "The postcode provided is not valid."}]})


(deftest check-location-test
  (testing "should check location by postcode"
    (with-fake-routes (json-handler "/active_locations?postcode=SW114BG"
                                    {:location_supported true, :earliest_collection_at "2017-12-11T11:23:57Z"})
      (let [response (check-location "SW114BG")]
        (is (= 200 (:status response)))
        (is (= {:earliest_collection_at "2017-12-11T11:23:57Z", :location_supported true} (:body response))))))

  (testing "should check location by coordinates"
    (with-fake-routes (json-handler "/active_locations?lon=-0.149872037904425&lat=51.4771750651945"
                                    {:location_supported true, :earliest_collection_at "2017-12-11T11:23:57Z"})
      (let [response (check-location [-0.149872037904425 51.4771750651945])]
        (is (= 200 (:status response)))
        (is (= {:earliest_collection_at "2017-12-11T11:23:57Z", :location_supported true} (:body response))))))

  (testing "should return 422 for invalid postcode"
    (with-fake-routes (json-handler "/active_locations?postcode=asdf" body-422 422)
      (let [response (check-location "asdf")]
        (is (= 422 (:status response)))
        (is (-> response :body map?)))))

  (testing "should return 200, unsupported location"
    (let [expected-response  {:location_supported false, :earliest_collection_at nil}]
      (with-fake-routes (json-handler "/active_locations?postcode=SW114BA" expected-response)
        (let [response (check-location "SW114BA")]
          (is (= 200 (:status response)))
          (is (= expected-response (:body response)))))))

  (testing "should return 499, with unparsed body"
    (with-fake-routes {(str quiqup-host "/active_locations?postcode=SW114BB")
                       (fn [_] {:status 499, :body "---"})}
      (let [response (check-location "SW114BB")]
        (is (= 499 (:status response)))
        (is (-> response :body nil?))
        (is ( = "---" (:body-unparsed response)))))))
