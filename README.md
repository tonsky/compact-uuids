# Compact 26-char URL-safe representation of UUIDs

## Highlights

- Produces strings that are 30% smaller (26 chars vs traditional 36 chars)
- Supports full UUID range (128 bits)
- Encoding-safe (uses only readable characters from ASCII)
- URL/file-name safe
- Lowercase/uppercase safe
- Avoids ambiguous characters (i/I/l/L/1/O/o/0)
- Alphabetical sort on encoded 26-char strings matches default UUID sort order
- Both Clojure and ClojureScript are supported

## Usage

Include:

```clojure
[compact-uuids "0.2.0"]
```

Require:

```clojure
(require [compact-uuids.core :as uuid])
```

Use:

```clojure
(uuid/str #uuid "3867b6f3-dbb0-4ef5-8078-364897154fd9")
           ; => "3gsxpyfdv0kqn80y1p92bhakys"

(uuid/read "3gsxpyfdv0kqn80y1p92bhakys")
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

Compare it to more compact and easier-on-the-eyes variant:

```
https://domain.com/chat/3gsxpyfdv0kqn80y1p92bhakys
```

### For performance

If you’re serializing lots of UUIDs (to JSON, for example), or you store lots of UUIDs as strings in memory (since there’s no compact UUID type in JS) or in DB (bad practice, don’t do that), then Compact UUIDs will save you up to 30% for free by essentially doing exactly the same. Just more efficient. It means faster transfers, less memory consumption, and less disk usage.

It probably wouldn’t make all the difference in the world, but it never hurts being a little more performant.

## Encoding algorithm

Compact-UUIDs encoding is based on [Crockford’s base32](http://www.crockford.com/wrmg/base32.html) encoding
. This is the alphabet used:

| values | chars |
| ------ | ----- |
| 0-9    | `0-9` |
| 10-17  | `a-h` |
| 18-19  | `jk`  |
| 20-21  | `mn`  |
| 22-26  | `p-t` |
| 27-31  | `v-z` |

One important property of this alphabet is that it retains sort order. I.e. if `1` < `2` < `3` then `(uuid/str 1)` < `(uuid/str 2)` < `(uuid/str 3)` (lexicographically) for any 1, 2 and 3.

It’s also URL-safe, filename-safe, zero maps to ASCII 0 and small numbers (0-15) map to expected ASCII `0-9a-f` chars, making reading encoded small numbers easier.

N.B. Because 26 5-bit chars give you 130 bits and UUIDs are just 128 bit we limit chars at positions 0 and 13 to 4 bits (`0123456789abcdef`)

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