(ns clojurescript-todo.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [reitit.frontend :as reitit]
              [clerk.core :as clerk]
              [accountant.core :as accountant]
              ))

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]
    ["/items"
     ["" :items]
     ["/:item-id" :item]]
    ["/about" :about]
    ["/search-estate" :search-estate]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

(path-for :about)
(path-for :search-estate)

;; -------------------------
;; Page components

(defn home-page []
  (fn []
    [:span.main
     [:h1 "Ejendomme"]
     [:ul
      [:li [:a {:href (path-for :search-estate)} "Fremsøg ejendom"]]
      [:li [:a {:href (path-for :items)} "Tilgængelige boliger"]]
      [:li [:a {:href "/borken/link"} "Beregninger"]]]]))

(defn search-estate-page []
  (let [estate-number (atom {:value nil})]
  (fn []
  [:span.main
    [:div
      [:h2 "Fremsøg ejendom"]
      [:p "Udfyld ejendomsnr. eller adresse og fremsøg ejendom"]
      [:div

        [:table (:style "border: 0; width: 100%;")
          [:tr
            [:td "Ejendoms nr."]
            [:td [:input {
                          :id "estate-number" 
                          :value (:value @estate-number)
                          :on-change #(swap! estate-number assoc :value (-> % .-target .-value))
                          :type "text"}]]
          ]
          [:tr
            [:td "Adresse"]
            [:td [:input {
                          :id "estate-address" 
                          :type "text"}]]
            [:td "Post nr."]
            [:td [:input {
                          :id "estate-postal-number" 
                          :type "text"}]]
          ]
        ]
      ]
        [:div
          [:button {:on-click #(handle_search estate-number)} "Fremsøg"]]
    ]
  ])))
  
  (defn handle_search [estate-number-val]
    (let [estate-number (:value @estate-number-val)]
      (println estate-number)
    )
  )

(defn items-page []
  (fn []
    [:span.main
     [:h1 "Tilgængelige Boliger"]
     [:ul (map (fn [item-id]
                 [:li {:name (str "item-" item-id) :key (str "item-" item-id)}
                  [:a {:href (path-for :item {:item-id item-id})} "Item: " item-id]])
               (range 1 5))]]))


(defn item-page []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :item-id])]
      [:span.main
       [:h1 (str "Item " item " of clojurescript-todo")]
       [:p [:a {:href (path-for :items)} "Back to the list of items"]]])))


(defn about-page []
  (fn [] [:span.main
          [:h1 "Tilgængelige ejendomme"]]))


;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'home-page
    :about #'about-page
    :items #'items-page
    :item #'item-page
    :search-estate #'search-estate-page
    ))


;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [:header
        [:p [:a {:href (path-for :index)} "Home"] " | "
         [:a {:href (path-for :about)} "Om"]]]
       [page]
       [:footer
        [:p "Created by Arnold Schwarzenegger"]]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data  match))
            route-params (:path-params match)]
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)
        ))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))
