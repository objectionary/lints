# Object does not match Filename

Every `.eo` file should not have different name than object, which may confuse
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
