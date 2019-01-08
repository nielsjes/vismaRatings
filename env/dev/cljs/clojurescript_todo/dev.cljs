(ns ^:figwheel-no-load clojurescript-todo.dev
  (:require
    [clojurescript-todo.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
