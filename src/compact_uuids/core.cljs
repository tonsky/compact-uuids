(ns compact-uuids.core
  "Compact 26-char URL-safe representation of UUIDs"
  (:refer-clojure :exclude [str read])
  (:require-macros
    [compact-uuids.core :as uuid]))


(defn char-of-alphabet [i]
  ;; http://www.crockford.com/wrmg/base32.html
  (.charAt "0123456789abcdefghjkmnpqrstvwxyz" i))


(defn char-of-hex [i]
  (.charAt "0123456789abcdef" i))


(defn parse-char* [c]
  (uuid/parse-char c))


(defn- parse-hex [c]
  (cond
    (<= 48 c 57)  (- c 48)   ;; 0..9
    (<= 97 c 102) (- c 87)   ;; a-f
    (<= 65 c 70)  (- c 55))) ;; A-F


(defn- convert [s bits-from bits-to mask from->code code->to]
  (loop [pos     (dec (count s)) ;; moving right-to-left, one char at a time
         acc     0
         acc-len 0
         res     ""]
    (cond
      ;; accumulated enough bits to generate char from lowest `bits-to` bits
      (>= acc-len bits-to)
        (recur
          pos
          (unsigned-bit-shift-right acc bits-to)   ;; drop lowest `bits-to` bits
          (- acc-len bits-to)
          (clojure.core/str (code->to (bit-and acc mask)) res)) ;; prepend char corresponding to lowest `bits-to` bits

      ;; end of string
      (neg? pos)
        (clojure.core/str (code->to (bit-and acc mask)) res)    ;; prepend rest of bits

      ;; read next `bits-from` bits from the string
      :else
        (recur
          (dec pos)
          ;; put next `bits-from` bits in front of `acc`
          (-> (from->code (.charCodeAt s pos))
              (bit-shift-left acc-len)
              (bit-or acc))
          (+ acc-len bits-from)
          res))))


(defn- base32->hex [s]
  ;; base32 is 65 bits from which we only use 64
  ;; when converted to hex, we pad it to 68 bits (17 chars)
  ;; drop first one as known to always be 0000
  (-> (convert s 5 4 0x0F parse-char* char-of-hex)
      (.substring 1)))


(defn read [s]
  (let [h (base32->hex (subs s 0 13))
        l (base32->hex (subs s 13 26))]
    (uuid (clojure.core/str (subs h 0 8) "-" (subs h 8 12) "-" (subs h 12 16) "-" (subs l 0 4) "-" (subs l 4 16)))))


(defn read-both [s]
  (case (count s)
    26 (read s)
    36 (uuid s)))


(defn- hex->base32 [s]
  (convert s 4 5 0x1F parse-hex char-of-alphabet))


(defn str [uuid]
  (let [s (clojure.core/str uuid)]
    (clojure.core/str
      (hex->base32 (clojure.core/str (subs s 0 8) (subs s 9 13) (subs s 14 18)))
      (hex->base32 (clojure.core/str (subs s 19 23) (subs s 24 36))))))