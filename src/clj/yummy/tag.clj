(ns yummy.tag
  "A dispatcher for custom tag decoders"
  (:require yummy.tag.envdir
            yummy.tag.envvar
            [clojure.string :refer [trim-newline]])
  (:import java.util.UUID))

(defmulti decode
  "Transform a tagged bean into the wanted representation"
  :tag)

(defmethod decode :envvar
  [opts]
  (yummy.tag.envvar/get-envvar opts))

(defmethod decode :envfmt
  [{:keys [args] :as opts}]
  (yummy.tag.envvar/get-envfmt args))

(defmethod decode :envdir
  [{:keys [args]}]
  (yummy.tag.envdir/get-envdir args))

(defmethod decode :keyword
  [{:keys [args]}]
  (keyword args))

(defmethod decode :slurp
  [{:keys [args]}]
  (trim-newline (slurp args)))

(defmethod decode :uuid
  [{:keys [args]}]
  (UUID/fromString args))


(defmethod decode :default
  [bean]
  bean)
