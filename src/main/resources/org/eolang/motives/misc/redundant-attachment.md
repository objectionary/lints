# Redundant attachment

The `>>` suffix gives an anonymous formation a name generated for it, so
that it can be referenced from elsewhere: by itself recursively (via `%`),
or by another object. If nothing ever refers to that generated
name, the `>>` is redundant and should be removed.

Incorrect:

```eo
[] > foo
  test > @
    [] >>
      2.plus 2 > @
```

Correct:

```eo
[] > foo
  test > @
    []
      2.plus 2 > @
```

A generated name is *not* redundant when it is actually used,
either by a recursive self-reference:

```eo
[] > foo
  test > @
    [n] >>
      if. > @
        n.eq 0
        0
        % (n.minus 1)
```

or by being called from another part of the same object:

```eo
[] > foo
  [n] >> helper
    n.plus 1 > @
  helper 5 > @
```
