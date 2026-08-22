# Test word

Names of objects in test files must not contain the word 'test'. If an object
is a unit test, or a helper inside a unit test, or lives in a file whose top
object name ends with `-tests`, its name should describe the behavior it
verifies instead of repeating the word 'test'.

Incorrect:

```eo
# Tests for foo.
[] > foo-tests
  # Test.
  [] +> can-test-add
    42 > @
```

Correct:

```eo
# Tests for foo.
[] > foo-tests
  # Test.
  [] +> can-add-two-and-two
    42 > @
```
