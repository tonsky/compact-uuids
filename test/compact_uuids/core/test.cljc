(ns compact-uuids.core.test
  (:require
    [clojure.string :as str]
    #?(:clj [clojure.test :refer [deftest is are testing]])
    #?(:cljs [cljs.test :refer [deftest is are testing]])
    [compact-uuids.core :as uuid]))


#?(:cljs (enable-console-print!))


(defn gen-uuid []
  #?(:clj  (java.util.UUID/randomUUID)
     :cljs (random-uuid)))


(deftest test-random
  (dotimes [_ 10000]
    (let [u (gen-uuid)]
      (is (= u (uuid/read (uuid/str u))))
      (is (= u (uuid/read (str/upper-case (uuid/str u))))))))


(deftest test-manual
  (are [uuid str] (and (= (uuid/str uuid) str)
                       (= uuid (uuid/read str)))
    #uuid "00000000-0000-0000-0000-000000000000" "00000000000000000000000000"
    #uuid "00000000-0000-0001-0000-000000000001" "00000000000010000000000001"
    #uuid "00000000-0000-001f-0000-000000000020" "000000000000z0000000000010"
    #uuid "00000000-0000-03ff-0000-000000000400" "00000000000zz0000000000100"
    #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff" "fzzzzzzzzzzzzfzzzzzzzzzzzz"))


(deftest test-error-correction
  (are [err normal] (= (uuid/str (uuid/read err)) normal)
    "000000000000000000Oo01IiLl" "00000000000000000000011111"))


(deftest test-read-both
  (let [u (gen-uuid)]
    (is (= u (uuid/read-both (uuid/str u))))
    (is (= u (uuid/read-both (str u))))))


#?(:cljs
(defn ^:export test-all []
  (cljs.test/run-all-tests #"compact-uuids\..*")))