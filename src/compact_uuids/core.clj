(ns compact-uuids.core
  "Compact 26-char URL-safe representation of UUIDs"
  (:refer-clojure :exclude [str read parse-long])
  (:import
    [java.util UUID]))


;; http://www.crockford.com/wrmg/base32.html
(def ^:const ^:private alphabet "0123456789abcdefghjkmnpqrstvwxyz")
                                

(defn- write-long! [^StringBuilder sb ^long n]
  ;; write top 4 bits
  (.append sb (.charAt alphabet (unsigned-bit-shift-right n 60)))
  (loop [x 12
         n (bit-shift-left n 4)]
    (when (pos? x)
      ;; write top 6 bits
      (.append sb (.charAt alphabet (unsigned-bit-shift-right n 59)))
      (recur (dec x) (bit-shift-left n 5)))))


(defn str [^UUID uuid]
  (let [sb (StringBuilder. 26)]
    (write-long! sb (.getMostSignificantBits uuid))
    (write-long! sb (.getLeastSignificantBits uuid))
    (.toString sb)))


(defmacro parse-char [c]
 `(cond
    (<= 48 ~c 57)   (- ~c 48) ;; 0..9
    (<= 97 ~c 104)  (- ~c 87) ;; a-h
    (== ~c 106)     18        ;; j   
    (== ~c 107)     19        ;; k
    (== ~c 109)     20        ;; m
    (== ~c 110)     21        ;; n
    (<= 112 ~c 116) (- ~c 90) ;; p-t
    (<= 118 ~c 122) (- ~c 91) ;; v-z
    
    ;; uppercase
    (<= 65 ~c 72)   (- ~c 55) ;; A-H
    (== ~c 74)      18        ;; J   
    (== ~c 75)      19        ;; K
    (== ~c 77)      20        ;; M
    (== ~c 78)      21        ;; N
    (<= 80 ~c 84)   (- ~c 58) ;; P-T
    (<= 86 ~c 90)   (- ~c 59) ;; V-Z
    
    ;; error correction
    (== ~c 105)     1         ;; i
    (== ~c 73)      1         ;; I
    (== ~c 108)     1         ;; l
    (== ~c 76)      1         ;; L
    (== ~c 111)     0         ;; o
    (== ~c 79)      0))       ;; O


(defn- parse-long [^String s offset]
  (let [offset (unchecked-int offset)
        max    (unchecked-int (unchecked-add-int offset 13))]
    (loop [n   (unchecked-int 0)
           pos (unchecked-int offset)]
      (if (< pos max)
        (let [c (unchecked-int (.charAt s pos))
              v (parse-char c)]
          (recur (bit-or (bit-shift-left n 5) v) (unchecked-inc pos)))
        n))))


(defn read [s]
  (UUID. (parse-long s 0) (parse-long s 13)))


(defn read-both [s]
  (case (count s)
    26 (read s)
    36 (UUID/fromString s)))
