(ns compact-uuids.core
  "Compact 22-char URL-safe representation of UUID"
  (:refer-clojure :exclude [str read])
  (:import
    [java.util UUID]))


(def ^:const ^:private alphabet "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz~")


(defn- write-long! [^StringBuilder sb ^long n]
  ;; write top 4 bits
  (.append sb (.charAt alphabet (unsigned-bit-shift-right n 60)))
  (loop [x 10
         n (bit-shift-left n 4)]
    (when (pos? x)
      ;; write top 6 bits
      (.append sb (.charAt alphabet (unsigned-bit-shift-right n 58)))
      (recur (dec x) (bit-shift-left n 6)))))


(defn str [^UUID uuid]
  (let [sb (StringBuilder. 22)]
    (write-long! sb (.getMostSignificantBits uuid))
    (write-long! sb (.getLeastSignificantBits uuid))
    (.toString sb)))


(defn- parse-long [^String s offset]
  (let [offset (unchecked-int offset)
        max    (unchecked-int (unchecked-add-int offset 11))]
    (loop [n   (unchecked-int 0)
           pos (unchecked-int offset)]
      (if (< pos max)
        (let [c (unchecked-int (.charAt s pos))
              v (cond
                  (<= 48 c 57)  (- c 48)   ;; 0..9
                  (<= 65 c 90)  (- c 55)   ;; A-Z
                  (== c 95)     36         ;; _
                  (<= 97 c 122) (- c 60)   ;; a-z
                  (== c 126)     63)]      ;; ~
          (recur (bit-or (bit-shift-left n 6) v) (unchecked-inc pos)))
        n))))


(defn read [s]
  (UUID. (parse-long s 0) (parse-long s 11)))


(defn read-both [s]
  (case (count s)
    22 (read s)
    36 (UUID/fromString s)))