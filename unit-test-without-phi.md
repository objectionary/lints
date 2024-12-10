# Unit Test Without Phi

Unit test must have `@` attribute.

Incorrect:

```eo
+tests

[] > test
  true
```

Correct:

```eo
+tests

[] > test
  true > @
```
