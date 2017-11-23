(ns widgetshop.app.products
  "Controls product listing information."
  (:require [widgetshop.app.state :as state]
            [widgetshop.server :as server]))

(defn- products-by-category [app category products]
  (assoc-in app [:products-by-category category] products))

(defn- set-categories [app categories]
  (assoc-in app [:categories] categories))

(defn- product-to-cart [app product]
  (update app :cart conj product))

(defn- set-rating [app product-id rating]
  (assoc-in app [:ratings-by-product product-id] rating))

(defn- load-products-by-category! [{:keys [categories] :as app} server-get-fn! category-id]
  (let [category (some #(when (= (:id %) category-id) %) categories)]
    (server-get-fn! category)
    (-> app
        (assoc :category category)
        (assoc-in [:products-by-category category] :loading))))

(defn select-category-by-id! [category-id]
  (state/update-state!
    load-products-by-category!
    (fn [category]
      (server/get! (str "/products/" (:id category))
                   {:on-success #(state/update-state! products-by-category category %)}))
    category-id))

(defn load-product-categories! []
  (server/get! "/categories" {:on-success #(state/update-state! set-categories %)}))

(defn add-to-cart! [product]
  (state/update-state! product-to-cart product))

(defn- select-product [app product]
  (assoc app :selected-product product))

(defn select-product! [products row-index]
  (if-let [row-index (first (js->clj row-index))]
    (do (println (str "Selected row " row-index))
        (state/update-state! select-product (get products row-index)))))

(defn post-a-new-rating! [{id :id :as product} rating]
  (server/post! "/ratings/" {:params {:id id :my-review {:rating rating}}
                             :on-success #(do
                                           (select-category-by-id! (-> @state/app :category :id))
                                           (state/update-state! select-product nil))}))