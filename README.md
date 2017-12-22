clj-quiqup
==========

A client for [Quiqup API based](https://api-docs.quiqup.com) on [clj-http.client](https://clojars.org/clj-http).

[![CircleCI](https://circleci.com/gh/Flexiana/clj-quiqup.svg?style=svg)](https://circleci.com/gh/Flexiana/clj-quiqup)
[![Dependencies Status](https://jarkeeper.com/flexiana/clj-quiqup/status.png)](https://jarkeeper.com/flexiana/clj-quiqup)
[![License](https://img.shields.io/badge/MIT-Clause-blue.svg)](https://opensource.org/licenses/MIT)


Leiningen/Boot
--------------

```clojure
[clj-quiqup "0.1.0"]
```


Documentation
-------------

All functions are designed to return errors instead of throwing exceptions (except `:pre` in a function).

To be able to run examples these lines are needed:

```clojure
(require '[clj-quiqup.core :as clj-quiqup])

(def host "https://api-staging.quiqup.com")
```

### check-location
Checks if a given `location` is supported by Quiqup. The `location` could be simple postcode as `string` or
 coordinates as a tuple `[longitude latitude]`.

It returns a response from `clj-http.client`. Thus response JSON is under `:body` key.

```clojure
(def response (clj-quiqup/check-location host "SW114BG"))
{:request-time 600, :repeatable? false, :protocol-version {:name "HTTP", :major 1, :minor 1}, :streaming? true, :chunked? true, :reason-phrase "OK", :headers {"Content-Type" "application/json", "X-Runtime" "0.125058", "X-Rack-Cache" "miss", "X-QueueTimeSeconds" "0", "Connection" "close", "Transfer-Encoding" "chunked", "ETag" "W/\"9d4e0be7a9dabc2480d355d7cea848d0\"", "Date" "Mon, 18 Dec 2017 14:11:01 GMT", "Vary" "Accept-Encoding", "X-Request-Id" "", "Cache-Control" "max-age=0, private, must-revalidate"}, :orig-content-encoding nil, :status 200, :length -1, :body {:location_supported true, :earliest_collection_at "2017-12-18T14:35:01Z"}, :trace-redirects []}

(:body response)
{:location_supported true, :earliest_collection_at "2017-12-18T14:36:44Z"}

(:status response)
200
```

In case of any error while parsing a body, `:body` attribute is set to `nil` and the body is associates
 to `:body-unparsed` as a `string`.
```clojure
(:body response)
nil

(:body-unparsed response)
"{\"I'm not a JSON\"}"
```

### login

```clojure
(:body (clj-quiqup/login host "client-id" "client-secret"))
{:access_token "token", :token_type "bearer", :expires_in 3600}
```

### create-job
Creates a job from a given job request.

```clojure
(:body (quiqup/create-job host token {:pickups [{:contact_name "Danny Hawkins" ...}]}
{:id "20171216-01bda77f", {:orders [{:pickup {:id "o-9613881", :contact_name ...}}]}}
```

### submit-job
Submits a job by a given id.

```clojure
(:body (quiqup/submit-job host token "20171216-01bda77f" {:earliest_collection_at "2017-12-14T13:49:44Z"}))
{:id "20171216-01bda77f", :state "pending_assignment", :earliest_collection_at "2017-12-14T15:49:44Z", ...}
```

### get-job
Gets a job by a given id.

```clojure
(:body (quiqup/get-job host token "20171216-01bda77f"))
{:id "20171216-01bda77f", :state "pending_assignment", :earliest_collection_at "2017-12-14T15:49:44Z", ...}
```
