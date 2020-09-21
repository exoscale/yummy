(ns yummy.tag
  "A dispatcher for custom tag decoders"
  (:require yummy.tag.envdir
            yummy.tag.envvar
            [clojure.string :as str]
            [exoscale.cloak :as cloak])
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
  (str/trim-newline (slurp args)))

(defmethod decode :uuid
  [{:keys [args]}]
  (UUID/fromString args))

(defmethod decode :secret
  [{:keys [args]}]
  (cloak/mask args))

(defmethod decode :default
  [bean]
  bean)
