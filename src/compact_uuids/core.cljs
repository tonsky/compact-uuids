(ns compact-uuids.core
  "Compact 22-char URL-safe representation of UUID"
  (:refer-clojure :exclude [str read]))


(def ^:const ^:private alphabet "0123456789=ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz")


(def ^:const ^:private hex "0123456789abcdef")


(defn- parse-char [c]
  (cond
    (<= 48 c 57)  (- c 48)   ;; 0..9
    (== c 61)     10         ;; =
    (<= 65 c 90)  (- c 54)   ;; A-Z
    (== c 95)     37         ;; _
    (<= 97 c 122) (- c 59))) ;; a-z


(defn- parse-hex [c]
  (cond
    (<= 48 c 57)  (- c 48)   ;; 0..9
    (<= 97 c 102) (- c 87))) ;; a-f


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


(defn- base64->hex [s]
  ;; base64 is 66 bits from which we only use 64
  ;; when converted to hex, we pad it to 68 bits (17 chars)
  ;; drop first one as known to always be 0000
  (.substring (convert s 6 4 0x0F parse-char #(.charAt hex %)) 1))


(defn read [s]
  (let [h (base64->hex (subs s 0 11))
        l (base64->hex (subs s 11 22))]
    (uuid (clojure.core/str (subs h 0 8) "-" (subs h 8 12) "-" (subs h 12 16) "-" (subs l 0 4) "-" (subs l 4 16)))))


(defn read-both [s]
  (case (count s)
    22 (read s)
    36 (uuid s)))


(defn- hex->base64 [s]
  (convert s 4 6 0x3F parse-hex #(.charAt alphabet %)))


(defn str [uuid]
  (let [s (clojure.core/str uuid)]
    (clojure.core/str
      (hex->base64 (clojure.core/str (subs s 0 8) (subs s 9 13) (subs s 14 18)))
      (hex->base64 (clojure.core/str (subs s 19 23) (subs s 24 36))))))