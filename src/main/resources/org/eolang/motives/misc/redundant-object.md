# Redundant object

If a named object is used only once, it is considered "redundant" and should be
inlined.

Incorrect:

```eo
# Foo.
[] > foo
  52 > spb
  spb.plus 2
```

Correct:

```eo
# Foo.
[] > foo
  52.plus 2
```

An object referenced only once is *not* redundant when that single reference
sits inside a recursive formation. Syntactically the name is mentioned once, but
at runtime the recursion evaluates it many times, and the named attribute lets
all those evaluations share a single node. Inlining it would give every level
of the recursion its own copy of the subgraph, so the lint skips such objects:

```eo
# Series.
[point] > series
  point.times point > squared
  poly 30 > @
  [n] > poly
    if. > @
      n.eq 0
      0
      squared.plus (poly (n.minus 1))
```

A `$`-alias is also *not* redundant when its only reference reaches it from
inside a nested formation. There, `$` refers to the nested formation itself,
not to the object where the alias was declared. Inlining the alias into that
reference would change what it points to:

```eo
# Jeff.
[] > jeff
  $ > self
  [] > say-hello
    stdout self > @
```
