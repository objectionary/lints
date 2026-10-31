# Cyclomatic complexity

EO has no loops or `goto`. The only decision points a formation can have
are a standalone `while` and an `if`, `and`, or `or` dispatched on
something. Whether the receiver of such a dispatch is a boolean, as
opposed to some other object with a method of the same name, cannot be
decided without type inference. So this lint counts any dispatch named
`if`, `and`, or `or`, together with a standalone `while`, as a decision
point. This is a heuristic, and the defect is marked `experimental`.

The cyclomatic complexity of a formation is one plus the number of its
own decision points. A decision point that belongs to a nested formation
counts for that formation, not for the one around it. When the
complexity exceeds ten, a warning is raised.

Incorrect:

```eo
# Foo.
[x] > foo
  x.eq 1 > c1
  x.eq 2 > c2
  x.eq 3 > c3
  x.eq 4 > c4
  x.eq 5 > c5
  x.eq 6 > c6
  x.eq 7 > c7
  x.eq 8 > c8
  x.eq 9 > c9
  x.eq 10 > c10
  c1.if > @
    1
    c2.if
      2
      c3.if
        3
        c4.if
          4
          c5.if
            5
            c6.if
              6
              c7.if
                7
                c8.if
                  8
                  c9.if
                    9
                    c10.if
                      10
                      0
```

Here, `foo` has eleven `if` dispatches, a cyclomatic complexity of
twelve. It should be refactored, for example by extracting some branches
into their own objects.
