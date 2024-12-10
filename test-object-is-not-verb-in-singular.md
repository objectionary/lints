# Test Object is not Verb in Singular

Test object name should start with verb in singular form.

Incorrect:

```eo
+tests

# This is test.
[] > this-is-test
  42 > foo

# This is test.
[] > testing
  42 > foo

# This is test.
[] > itIsTrue
  42 > foo
```

Correct:

```eo
+tests

# This is test.
[] > prints-data
  42 > foo

# This is test.
[] > works-as-expected
  42 > foo

# This is test.
[] > parses-dom
  42 > foo
```
