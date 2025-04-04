# Sparse Decoration

Sparse decoration of the base object is prohibited.

Incorrect:

```eo
[] > decorates-five
  five > @
```

Correct:

```eo
[] > decorates-application
  if > @
    true
    5
    five
```

```eo
[free] > decorates-with-free-args
  five > @
```

Also, it is possible to have sparse decoration in tests:

```eo
# This is my unit test.
[] > runs-analysis
  assert > @
    foo.eq 42
```
