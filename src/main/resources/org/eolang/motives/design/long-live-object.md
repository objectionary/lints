# Long live object

An object located too far from where it's used is hard to keep in mind
while reading the code between. If more than five lines separate an
attribute's declaration from the last line, in the same formation, that
uses it, a warning is raised.

Incorrect:

```eo
# Foo.
[] > app
  foo 42 > f
  1 > a
  2 > b
  3 > c
  4 > d
  5 > e
  6 > g
  52.plus f > r
```

Here, `f` is declared on the first line but not used until the eighth,
seven lines away. It should be moved closer to `52.plus f > r`, or
extracted into its own object.

Correct:

```eo
# Foo.
[] > app
  1 > a
  2 > b
  3 > c
  4 > d
  5 > e
  6 > g
  foo 42 > f
  52.plus f > r
```
