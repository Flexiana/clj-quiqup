(ns clj-quiqup-test.create-job-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [clj-http.fake :refer [with-fake-routes]]
    [clj-quiqup.core :as quiqup]
    [clj-quiqup-test.fake-http :as fake-http]))


(def quiqup-host "https://api-staging.quiqup.localhost")

(def create-job (partial quiqup/create-job quiqup-host "token"))

(def json-handler (partial fake-http/json-handler quiqup-host))


(def job-req
  {:transport_mode "scooter"
   :pickups [{:contact_name "Danny Hawkins"
              :contact_phone "07447111111"
              :location {:address1 "12 Prince of wales drive"
                         :postcode "SW11 4BG"
                         :coords [-0.149872037904425, 51.4771750651945]}
              :items [{:name "pickup", :quantity 1}]}]
   :dropoffs [{:contact_name "Dim Rinssen"
               :contact_phone "07447111111"
               :location {:address1 "8 Scrubs Lane", :postcode "NW10 6RB"}}]})


(def job-response
  {:billing_identifier nil
   :courier nil
   :created_at "2017-12-14T13:28:43.749+00:00"
   :delivery_window_minutes nil
   :earliest_collection_at "2017-12-14T13:49:44Z"
   :estimated_costs {:adjustment_cost "0.0"
                     :fixed_cost "5.2"
                     :kilometer_cost "11.34"
                     :period_cost ""
                     :total_vat_exclusive_cost "16.54"
                     :total_vat_inclusive_cost "19.85"
                     :vat_cost "3.31"
                     :waypoint_cost "0.0"}
   :id "20171214-b180664f"
   :kind "partner_on_demand"
   :known_costs {:adjustment_cost "0.0"
                 :delivery_cost "19.85"
                 :pickups_cost "0"
                 :service_fee "0.0"
                 :service_fee_percent "0"
                 :subtotal_cost "19.85"
                 :surcharge_cost "0"
                 :total_cost "19.85"}
   :metadata {}
   :orders [{:dropoff {:contact_name "Dim Rinssen"
                       :contact_phone "07447111111"
                       :id "o-9289362"
                       :item_quantity_count 0
                       :location {:address1 "8 Scrubs Lane"
                                  :address2 nil
                                  :apartment_number nil
                                  :building_name nil
                                  :coords [-0.237550370032044 51.5315001341995]
                                  :county nil
                                  :notes nil
                                  :partner_location_id nil
                                  :postcode "NW10 6RB"
                                  :town nil}
                       :notes ""
                       :tracking_token ""
                       :waypoint_type "dropoff"}
             :items [{:allowed_eans []
                      :children []
                      :dimensions nil
                      :ean nil
                      :gid "gid://quiqup/Job::Order::Item/2669831"
                      :id 2669831
                      :image nil
                      :metadata {}
                      :name "pickup"
                      :notes nil
                      :price "0.0"
                      :product_image_url nil
                      :quantity 1
                      :quiqee_picking_order nil
                      :section []
                      :source_gid nil
                      :state nil
                      :substitutions []
                      :total_cost -1
                      :weight nil}]
             :partner_order_id nil
             :payment_amount "0.0"
             :payment_mode nil
             :pickup {:contact_name "Danny Hawkins"
                      :contact_phone "07447111111"
                      :id "o-3570123"
                      :item_quantity_count 0
                      :location {:address1 "12 Prince of wales drive"
                                 :address2 nil
                                 :apartment_number nil
                                 :building_name nil
                                 :coords [-0.149872037904425 51.4771750651945]
                                 :county nil
                                 :notes nil
                                 :partner_location_id nil
                                 :postcode "SW11 4BG"
                                 :town nil}
                      :notes ""
                      :tracking_token ""
                      :waypoint_type "pickup"}}]
   :quiqee_accepted_at nil
   :scheduled_for nil
   :state "pending"
   :state_updated_at "2017-12-14T13:28:43.749+00:00"
   :submitted_at nil
   :transport_mode "scooter"})


(deftest create-job-test
  (testing "should create job"
    (with-fake-routes (json-handler "/partner/jobs" job-response 200 :post)
      (let [response (create-job job-req)]
        (is (= 200 (:status response)))
        (is (= job-response (:body response))))))

  (testing "should return bad request"
    (with-fake-routes (json-handler "/partner/jobs" {} 400 :post)
      (let [response (create-job {})]
        (is (= 400 (:status response)))))))

