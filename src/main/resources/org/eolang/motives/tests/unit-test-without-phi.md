# Unit test without phi

Unit tests must have a `@` attribute.

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
