# Identity object

The language has a symbol for the identity object, `I`, which is
`[x] > (φ ↦ x)`. Writing that formation out by hand instead of using
the symbol is discouraged.

Incorrect:

```eo
[] > jeff
  [value] > pass
    value > @
  foo pass > @
```

Correct:

```eo
[] > jeff
  foo I > @
```
