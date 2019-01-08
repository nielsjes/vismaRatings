(ns visma-ratings.core
  (:require [org.httpkit.client :as http])
  (:gen-class))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


(defn -main
  [& args]
  (foo 5)
  (let [response1 (http/get "http://10.170.38.104:3000/next")]
    ;; Handle responses one-by-one, blocking as necessary
    ;; Other keys :headers :body :error :opts
    (println "response1's status: " (:body @response1)))
  )

