# Sparse decoration

Sparse decoration of the base object is prohibited.

Incorrect:

```eo
[] > decorates-five
  five > @
```

Correct:

```eo
[] > decorates-app
  if > @
    true
    5
    five
```

```eo
[free] > decorates-with-free-args
  five > @
```

Also, it's possible to have sparse decoration in tests:

```eo
# This is my unit test.
[] > runs-analysis
  assert > @
    foo.eq 42
```
