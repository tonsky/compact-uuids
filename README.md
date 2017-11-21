# Compact 22-char URL-safe representation of UUIDs

## Highlights

- Produces strings that are 40% smaller (22 chars vs traditional 36 chars)
- Supports full UUID range (128 bits)
- Encoding-safe (uses only readable characters from ASCII)
- URL/file-name safe
- Alphabetical sort on 22-char strings matches default UUID sort order
- Both Clojure and ClojureScript are supported

## Usage

Include:

```clojure
[compact-uuids "0.1.0"]
```

Require:

```clojure
(require [compact-uuids.core :as uuid])
```

Use:

```clojure
(uuid/str #uuid "3867b6f3-dbb0-4ef5-8078-364897154fd9")
           ; => "3XcikFRh4wq81tD_YN5K~P"

(uuid/read "3XcikFRh4wq81tD_YN5K~P")
; => #uuid "3867b6f3-dbb0-4ef5-8078-364897154fd9"
```

## Why?

What’s wrong with the default UUID encoding?

Nothing, except for how wasteful it is. UUIDs are not meant for human consumption, so why waste precious characters (UUID encoding wastes 4 chars on dashes alone!) on something that will be an opaque token for people anyway?

Unlike default UUID encoding, Compact UUIDs encoding uses as little space as possible while still being encoding-safe (only readable chars from ASCII) and URL-/filename-safe.

Does it matter? Well, not much, but it does a little.

### For people

If you produce an URL that has at least one UUID in it, it becomes unbearably long:

```
https://domain.com/chat/3867b6f3-dbb0-4ef5-8078-364897154fd9
```

Compare it to much more compact variant:

```
https://domain.com/chat/3XcikFRh4wq81tD_YN5K~P
```

### For performance

If you’re serializing lots of UUIDs (to JSON, for example), or you store lots of UUIDs as strings in memory (since there’s no compact UUID type in JS) or in DB (bad practice, don’t do that), then Compact UUIDs will save you up to 40% for free by essentially doing exactly the same. Just more efficient. It means faster transfers, less memory consumption, and less disk usage.

It probably wouldn’t make all the difference in the world, but it never hurts being a little more performant.

## Encoding algorithm

Compact-UUIDs encoding uses 6-bit similar to [base64url without padding](https://tools.ietf.org/html/rfc7515#appendix-C). This is the alphabet used for encoding:

| values | chars |
| ------ | ----- |
| 0-9    | `0-9` |
| 10-35  | `A-Z` |
| 36     | `_`   |
| 37-62  | `a-z` |
| 63     | `~`   |

One important property of this alphabet is that it retains sort order. I.e. if `1` < `2` < `3` then `(uuid/str 1)` < `(uuid/str 2)` < `(uuid/str 3)` (lexicographically) for any 1, 2 and 3.

It’s also URL-safe, filename-safe, zero maps to ASCII 0 and small numbers (0-15) map to expected ASCII `0-9,A-F` chars, making reading encoded small numbers easier.

N.B. Because 22 6-bit chars give you 132 bits and UUIDs are just 128 bit we limit chars at positions 0 and 11 to 4 bits (`0123456789ABCDEF`)

## Developing

To run tests:

```
lein test
lein cljsbuild test
```

To run benchmarks (JVM):

```
lein bench
```

## License

Copyright © 2017 Nikita Prokopov

Licensed under Eclipse Public License (see [LICENSE](LICENSE)).