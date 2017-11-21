(ns compact-uuids.core.bench
  (:require
    [compact-uuids.core :as uuid]
    [criterium.core :as criterium])
  (:import
    [java.util UUID]))


(defn bench-str []
  (let [buffer (repeatedly 1000 #(UUID/randomUUID))
        *uuids (atom (cycle buffer))]
    (println "\n\n>>> compact-uuids.core/-str <<<")
    (criterium/quick-bench 
      (uuid/str (first (swap! *uuids next))))))


(defn bench-toString []
  (let [buffer (repeatedly 1000 #(UUID/randomUUID))
        *uuids (atom (cycle buffer))]
    (println "\n\n>>> UUID/toString <<<")
    (criterium/quick-bench 
      (str (first (swap! *uuids next))))))


(defn bench-read []
  (let [buffer   (repeatedly 1000 #(UUID/randomUUID))
        *strings (atom (cycle (mapv uuid/str buffer)))]
    (println "\n\n>>> compact-uuids.core/read <<<")
    (criterium/quick-bench 
      (uuid/read (first (swap! *strings next))))))


(defn bench-fromString []
  (let [buffer   (repeatedly 1000 #(UUID/randomUUID))
        *strings (atom (cycle (mapv str buffer)))]
    (println "\n\n>>> UUID/fromString <<<")
    (criterium/quick-bench 
      (UUID/fromString (first (swap! *strings next))))))


(defn -main [& _]
  (bench-str)
  (bench-toString)    
  (bench-read)
  (bench-fromString))