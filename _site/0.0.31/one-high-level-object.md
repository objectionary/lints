# One High-Level Object

Every `.eo` file must have only one high-level object inside.

Incorrect:

`app.eo`:

```eo
# Foo.
[] > foo
  42 > @

# Bar.
[] > bar
  42 > @
```

Correct:

`app.eo`:

```eo
# Foo.
[] > foo
  42 > @
```
