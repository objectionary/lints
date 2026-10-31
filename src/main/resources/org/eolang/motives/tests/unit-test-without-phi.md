# Unit test without phi

Unit tests must have a `@` attribute.

Incorrect:

```eo
# Foo.

[] > foo

  [] +> works-dummy
    true > i
```

Correct:

```eo
# Foo.

[] > foo

  [] +> works-dummy
    true > @
```
