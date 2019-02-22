(ns yummy.config-test
  (:require [clojure.string     :as string]
            [clojure.spec.alpha :as spec]
            [clojure.java.io    :as io]
            [yummy.config       :refer :all]
            [clojure.test       :refer :all])
  (:import java.util.UUID))

(defn return-exception
  [e msg]
  (ex-info msg {:cause e}))

(def load-opts
  {:die-fn return-exception})

(defn path-for
  [resource]
  (-> resource name io/resource .getPath))

(defn make-input
  "Generate a valid YAML input string from a collection of strings"
  [vs]
  (string/join "\n" vs))

(def keyword-input
  "Keyword input test values"
  ["a: !keyword bar"
   "b: !keyword boo/bim"
   "c: !keyword yummy.config-test/bar"
   "d: !keyword {}"
   "e: !keyword []"])

(def envvar-input
  "Input for envvar lookups"
  ["a: !envvar USER"
   "b: !envvar NOPE"
   "c: !envvar {envVar: HOME, defaultValue: hello}"
   "d: !envvar {envVar: NOPE, defaultValue: hello}"
   "e: !envvar [NOPE, hello]"
   "f: !envvar [HOME, hello]"
   "g: !envvar [NOPE]"
   "h: !envvar [HOME]"])

(def uuid-input
  "Input for UUID lookups"
  ["a: !uuid fc716a9b-fb1e-4ebd-b781-5ca13039aa55"
   "b: !uuid bad50b96-ed73-11e8-869d-9cb6d0e6ac17"
   "c: !uuid f3548089-8c02-3383-a02f-a54826cfa006"
   "d: !uuid 842e31d4-e8d5-52ee-aa36-10e0428eba31"])

(def spec-input
  "Input for spec checks"
  ["a: !keyword b"
   "b: foo"
   "c: {b: there}"
   "d: [!keyword a, !keyword b, !keyword c]"])

(spec/def ::a #{:a :b :c})
(spec/def ::b string?)
(spec/def ::c (spec/keys :req-un [::b]))
(spec/def ::d (spec/coll-of ::a))
(spec/def ::e string?)
(spec/def ::good (spec/keys :req-un [::a ::b ::c ::d]))
(spec/def ::bad  (spec/keys :req-un [::e]))

(def good-spec
  "A valid spec for our spec tests"
  (spec/keys :req-un [:good/a :good/b :good/c :good/d]))

(def bad-spec
  "A valid spec for our spec tests"
  (spec/keys :req-un [:bad/e]))

(deftest keyword-test
  (let [cfg  (load-config-string (make-input keyword-input) load-opts)]
    (testing "keyword deserialization"
      (is (= :bar (:a cfg)))
      (is (= :boo/bim (:b cfg)))
      (is (= ::bar (:c cfg)))

      (is (nil? (:d cfg)))
      (is (nil? (:e cfg))))))

(deftest envvar-test
  (let [home (System/getenv "HOME")
        user (System/getenv "USER")
        cfg  (load-config-string (make-input envvar-input) load-opts)]

    (testing "environment lookups"
      (is (= user (:a cfg)))
      (is (nil? (:b cfg)))
      (is (= home (:c cfg)))
      (is (= "hello" (:d cfg)))
      (is (= "hello" (:e cfg)))
      (is (= home (:f cfg)))
      (is (nil? (:g cfg)))
      (is (= home (:h cfg))))))

(deftest uuid-test
  (let [cfg (load-config-string (make-input uuid-input) load-opts)]
    (testing "UUIDs"
      (is 4
          (.version ^UUID (:a cfg)))
      (is 1
          (.version ^UUID (:b cfg)))
      (is 3
          (.version ^UUID (:c cfg)))
      (is 5
          (.version ^UUID (:d cfg))))))

(deftest envfmt-test
  (let [cfg (load-config-string "a: !envfmt ['user=%s, home=%s', USER, HOME]")]
    (testing "environment variable formats"
      (is (= (format "user=%s, home=%s"
                     (System/getenv "USER")
                     (System/getenv "HOME"))
             (:a cfg))))))

(deftest envdir-test
  (let [cfg (load-config-string
             (format "a: !envdir %s" (path-for :envdir))
             load-opts)]
    (testing "envdir loading"
      (is (= {:a "b" :c "d"}
             (:a cfg))))))

(deftest spec-test
  (let [input (make-input spec-input)
        cfg1  (load-config-string input (assoc load-opts :spec ::good))
        cfg2  (load-config-string input (assoc load-opts :spec ::bad))]

    (testing "honoring specs"
      (is (= cfg1 {:a :b :b "foo" :c {:b "there"} :d [:a :b :c]})))
    (testing "failing on a bad spec"
      (is (instance? Exception cfg2)))))

(deftest file-test
  (let [cfg (load-config {:path (path-for "test.yml")
                          :spec ::good
                          :die-fn return-exception})]
    (testing "loading configuration from a file"
      (is (= cfg {:a :b :b "foo" :c {:b "there"} :d [:a :b :c]})))))

(deftest slurp-test
  (let [cfg (load-config-string (format "a: !slurp %s" (path-for :token))
                                load-opts)]
    (testing "slurp loading"
             (is (= {:a "hello"}
                    cfg)))))


(def conform-input-ok
  ["date-year: '2019'"
   "date-date: '2019-12-31'"
   "date-full: '2019-12-31T11:59:59'"])

(def conform-input-err
  ["date-year: 'cannot parse this'"
   "date-date: '2019-12-31'"
   "date-full: '2019-12-31T11:59:59'"])


(defn wrap-invalid
  [f]
  (fn [& args]
    (try
      (apply f args)
      (catch Exception e
        ::spec/invalid))))


(spec/def ::inst
  (-> clojure.instant/read-instant-date
      wrap-invalid
      spec/conformer))


(spec/def ::date-year ::inst)
(spec/def ::date-date ::inst)
(spec/def ::date-full ::inst)

(spec/def ::spec-conform
  (spec/keys :req-un [::date-year
                      ::date-date
                      ::date-full]))


(deftest conform-test-ok
  (let [cfg (load-config-string
             (make-input conform-input-ok)
             (merge
              load-opts
              {:spec ::spec-conform}))]

    (is (= cfg
           {:date-year #inst "2019-01-01T00:00:00",
            :date-date #inst "2019-12-31T00:00:00",
            :date-full #inst "2019-12-31T11:59:59"}))))


(deftest conform-test-err

  (let [e (load-config-string
           (make-input conform-input-err)
           (merge
            load-opts
            {:spec ::spec-conform}))

        {:keys [cause]} (ex-data e)
        data (ex-data cause)

        field (-> data ::spec/problems first :path first)]

    (is (= field :date-year))))
