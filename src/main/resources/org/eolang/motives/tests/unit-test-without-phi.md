# Unit Test Without Phi

Unit test must have `@` attribute.

Incorrect:

```eo
+tests

# Test suite for `foo`.
[] > foo-tests
  # Unit test.
  [] > works-dummy
    true > i
```

Correct:

```eo
+tests

# Test suite for `foo`.
[] > foo-tests
  # Unit test.
  [] > works-dummy
    true > @
```
