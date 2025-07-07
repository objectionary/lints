# Unit Test Without Phi

Unit test must have `@` attribute.

Incorrect:

```eo
# Foo.
[] > foo
  # Unit test.
  [] +> works-dummy
    true > i
```

Correct:

```eo
# Foo.
[] > foo
  # Unit test.
  [] +> works-dummy
    true > @
```
