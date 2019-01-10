(ns Utils.estate_search
  (:gen-class)
)

  (defn handle_search [estate-number-val]
    (let [estate-number (:value @estate-number-val)]
      (println estate-number)
    )
  )