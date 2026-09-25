# Sparse decoration

Sparse decoration of the base object is prohibited.

Incorrect:

```eo
[] > decorates-five
  five > @
```

Correct:

```eo
[free] > decorates-app
  if > @
    true
    5
    five
```

Also, it's possible to have sparse decoration in tests:

```eo
# This is my unit test.
[] > runs-analysis
  assert > @
    foo.eq 42
```
