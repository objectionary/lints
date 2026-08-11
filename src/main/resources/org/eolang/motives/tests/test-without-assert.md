# Test without assert

A unit test that does not assert anything tests nothing. Such a test is
useless: it will pass no matter what, so it cannot catch a regression.

Incorrect:

```eo
+tests

# Test.
[] > checks-something
  52 > @
```

Here the test object `checks-something` is just a number literal — there
is no comparison, no assertion, nothing to verify.

Correct:

```eo
+tests

# Test.
[] > checks-something
  eq. > @
    42
    42
```

Now the test asserts that `eq 42 42` is true.
