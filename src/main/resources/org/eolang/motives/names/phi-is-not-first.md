# `@` Is Not First

The `@` attribute should always go first.

Incorrect:

```eo
# Foo.
[] > foo
  x > bar
  y > @
  z > hey
```

Correct:

```eo
# Foo.
[] > foo
  y > @
  x > bar
  z > hey
```

The position of `@` after void attributes is also allowed:

```eo
# Foo.
[] > foo
  [i] >>
    boom > @
      *
        "f"
```
