(ns clj-quiqup.core
  (:require
    [clojure.string :refer [blank?]]
    [cemerick.url :as url]
    [clj-http.client :as http]))


(def http-opts
  {:as :json
   :accept :json
   :content-type :json
   :throw-exceptions false})


(defn- auth-header
  [token]
  {"Authorization" (format "Bearer %s" token)})


(defn- parse-4xx-response
  "Parses a :body for all 4xx responses as JSON (with keywords). If a parser throw an exception it sets :body to `nil`
   and unparsed body is `assoc`ed into :body-unparsed.
   Thus calling any collection modifier on :body will not throw an exception."
  [response]
  (if (and (<= 400 (:status response))
           (< (:status response) 500))
    (try
      (update response :body http/json-decode true)
      (catch Exception e (-> response
                             (assoc :body-unparsed (:body response))
                             (assoc :body nil))))
    response))


(defn check-location
  "Checks if a given `location` is supported by Quiqup. The `location` could be simple postcode as `string` or
   coordinates as a tuple `[longitude latitude]`."
  [host location]
  {:pre [(not (blank? host))]}
  (let [assoc-query #(assoc % :query (if (and (vector? location)
                                              (= 2 (count location)))
                                       (zipmap [:lon :lat] location)
                                       {:postcode location}))]
    (-> host
        str
        url/url
        (assoc :path "/active_locations")
        assoc-query
        str
        (http/get http-opts)
        parse-4xx-response)))


(defn login
  "Returns a response with a token if it succeeded."
  [host client-id client-secret]
  {:pre [(not (blank? host))]}
  (-> host
      str
      url/url
      (assoc :path "/oauth/token")
      str
      (http/post (merge http-opts {:form-params {:grant_type "client_credentials"
                                                 :client_id client-id
                                                 :client_secret client-secret}}))
      parse-4xx-response))


(defn create-job
  "Creates a job from a given `job-req` object"
  [host token job-req]
  {:pre [(not (blank? host))]}
  (-> host
      str
      url/url
      (assoc :path "/partner/jobs")
      str
      (http/post (merge http-opts {:form-params job-req} {:headers (auth-header token)}))
      parse-4xx-response))


(defn submit-job
  "Submits a job by a given `id`"
  [host token id submission-req]
  {:pre [(not (blank? host))]}
  (-> host
      str
      url/url
      (assoc :path (format "/partner/jobs/%s/submissions" id))
      str
      (http/post (merge http-opts {:form-params submission-req} {:headers (auth-header token)}))
      parse-4xx-response))
