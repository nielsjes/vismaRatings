(ns clojurescript-todo.prod
  (:require [clojurescript-todo.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
