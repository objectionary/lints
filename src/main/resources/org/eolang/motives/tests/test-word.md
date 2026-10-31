# Test word

Names of objects in test files must not contain the word 'test'. This
applies to a unit test itself, a helper inside one, or any object in a
file whose top object name ends with `-tests`. Its name should describe
the behavior it verifies instead of repeating the word 'test'.

Incorrect:

```eo
# Tests for foo.

[] > foo-tests

  [] +> can-test-add
    42 > @
```

Correct:

```eo
# Tests for foo.

[] > foo-tests

  [] +> can-add-two-and-two
    42 > @
```
