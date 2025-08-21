# Object doesn't match filename

Each `.eo` file must have the same name as its contained object to avoid
confusing readers.

Incorrect:

`bar.eo`:

```eo
# Foo.
[] > foo
```

Correct:

`foo.eo`:

```eo
# Foo.
[] > foo
```
