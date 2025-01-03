# Atom With `@`

Atoms must not have `@` attribute.

Incorrect:

```eo
# Foo.
[@] > foo /number
```

Correct:

```eo
# Foo.
[] > foo /number
```
