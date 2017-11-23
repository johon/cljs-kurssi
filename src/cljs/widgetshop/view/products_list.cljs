(ns widgetshop.view.products-list
  ""
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [widgetshop.app.state :as state]
            [widgetshop.app.products :as products]))

(defn- star [state]
  [:span {:style {:width "20px"
                  :height "20px"
                  :margin "2px"
                  :border-color "black"
                  :border-style "solid"
                  :display "inline-block"}}
   [:span {:style (cond->
                    {:width "10px"
                     :height "20px"
                     :display "inline-block"}

                    (#{:full :half} state)
                    (assoc :background-color "yellow"))}]
   [:span {:style (cond->
                    {:width "10px"
                     :height "20px"
                     :display "inline-block"}

                    (= :full state)
                    (assoc :background-color "yellow"))}]])

(defn star-rating [rating ratings_count]
  (let [states (cond
                 (< rating 1.0) [:half nil nil nil nil]
                 (< rating 1.5) [:full nil nil nil nil]
                 (< rating 2.0) [:full :half nil nil nil]
                 (< rating 2.5) [:full :full nil nil nil]
                 (< rating 3.0) [:full :full :half nil nil]
                 (< rating 3.5) [:full :full :full nil nil]
                 (< rating 4.0) [:full :full :full :half nil]
                 (< rating 4.5) [:full :full :full :full nil]
                 (< rating 4.9) [:full :full :full :full :half]
                 :default       [:full :full :full :full :full])]
    [:div {:style {:display "inline-block"}}
     (doall (map-indexed
              (fn [i state] ^{:key (str "star_" i)}
                [star state])
              states))
     (when ratings_count
       (str ratings_count " ratings"))]))

(defn product-view [{:keys [id name description price rating ratings_count] :as product}]
  (when product
   [:div
    [:hr]
    [ui/card
     {:initially-expanded true}
     [ui/card-header {:title name
                      :subtitle description}]
     [ui/card-text
      [:div
       {:style {:float "right"}}
       [star-rating rating ratings_count]]
      [:div
       (str price " €")]]

     [ui/card
      {:initially-expanded true}
      [ui/card-header {:title "Rate this product"}]
      [ui/card-text
       [:div
        (for [rating (range 1 5)]
          ^{:key (str "rate-button-" rating)}
          [ui/raised-button {:label rating
                             :on-click #(products/post-a-new-rating! product rating)}])]]]]

    ]))

(defn listing [products]
  (if (= :loading products)
    [ui/refresh-indicator {:status "loading" :size 40 :left 10 :top 10}]

    [ui/table {:on-row-selection (partial products/select-product! products)}
     [ui/table-header {:display-select-all false :adjust-for-checkbox false}
      [ui/table-row
       [ui/table-header-column "Name"]
       [ui/table-header-column "Description"]
       [ui/table-header-column "Price (€)"]
       [ui/table-header-column "Rating"]
       [ui/table-header-column "Add to cart"]]]
     [ui/table-body {:display-row-checkbox false}
      (for [{:keys [id name description price rating ratings_count] :as product} products]
        ^{:key id}
        [ui/table-row
         [ui/table-row-column name]
         [ui/table-row-column description]
         [ui/table-row-column price]
         [ui/table-row-column [star-rating rating ratings_count]]
         [ui/table-row-column
          [ui/flat-button {:primary true :on-click #(products/add-to-cart! product)}
           "Add to cart"]]])]]))

