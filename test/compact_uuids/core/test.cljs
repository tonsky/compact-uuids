(ns compact-uuids.core.test
  (:require
    [cljs.test :refer [deftest is are]]
    [compact-uuids.core :as uuid]))


(enable-console-print!)


(deftest test-random
  (dotimes [_ 10000]
    (let [u (random-uuid)]
      (is (= u (uuid/read (uuid/str u)))))))


(deftest test-manual
  (are [uuid str] (and (= (uuid/str uuid) str)
                       (= uuid (uuid/read str)))
    #uuid "00000000-0000-0000-0000-000000000000" "0000000000000000000000"
    #uuid "00000000-0000-0001-0000-000000000001" "0000000000100000000001"
    #uuid "00000000-0000-003f-0000-000000000040" "0000000000z00000000010"
    #uuid "00000000-0000-0fff-0000-000000001000" "000000000zz00000000100"
    #uuid "ffffffff-ffff-ffff-ffff-ffffffffffff" "EzzzzzzzzzzEzzzzzzzzzz"))


(deftest test-read-both
  (let [u (random-uuid)]
    (is (= u (uuid/read-both (uuid/str u))))
    (is (= u (uuid/read-both (str u))))))


(defn ^:export test-all []
  (cljs.test/run-all-tests #"compact-uuids\..*"))