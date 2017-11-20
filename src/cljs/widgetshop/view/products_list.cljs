(ns widgetshop.view.products-list
  ""
  (:require [reagent.core :as r]
            [cljsjs.material-ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.icons :as ic]
            [widgetshop.app.state :as state]
            [widgetshop.app.products :as products]))

(defn- select-product [app product]
  (assoc app :selected-product product))

(defn select-product! [products row-index]
  (if-let [row-index (first (js->clj row-index))]
    (do (println (str "Selected row " row-index))
        (state/update-state! select-product (get products row-index)))))

(defn product-view [{:keys [id name description price] :as product}]
   (when product
     [ui/card
      {:initially-expanded true}
      [ui/card-header {:title name
                       :subtitle description}]
      [ui/card-text (str price " €")]]))

(defn listing [products]
  (if (= :loading products)
    [ui/refresh-indicator {:status "loading" :size 40 :left 10 :top 10}]

    [ui/table {:on-row-selection (partial select-product! products)}
     [ui/table-header {:display-select-all false :adjust-for-checkbox false}
      [ui/table-row
       [ui/table-header-column "Name"]
       [ui/table-header-column "Description"]
       [ui/table-header-column "Price (€)"]
       [ui/table-header-column "Add to cart"]]]
     [ui/table-body {:display-row-checkbox false}
      (for [{:keys [id name description price] :as product} products]
        ^{:key id}
        [ui/table-row
         [ui/table-row-column name]
         [ui/table-row-column description]
         [ui/table-row-column price]
         [ui/table-row-column
          [ui/flat-button {:primary true :on-click #(products/add-to-cart! product)}
           "Add to cart"]]])]]))

