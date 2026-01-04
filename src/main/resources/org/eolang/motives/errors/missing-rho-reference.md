# Missing `^.` Reference

Each [rho][EO-specials] reference (`^.`) should point only to an existing
objects.

Incorrect:

```eo
# Foo.
[] > foo
  [] > message
  [] > bar
    ^.boom > @
```

Correct:

```eo
# Foo.
[] > foo
  [] > message
  [] > bar
    ^.message > @
```

[EO-specials]: https://news.eolang.org/2024-05-14-rho-sigma-delta-lambda.html
