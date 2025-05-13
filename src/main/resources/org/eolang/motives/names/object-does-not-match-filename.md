# Object doesn't match filename

Every `.eo` file shouldn't have different name than object, which may confuse
the readers.

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
