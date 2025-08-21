# Decorated formation

A formation should not be used as a decoratee. This means that abstract objects
should not have `@` as their name.

Incorrect:

```eo
[] > main
  [] > @
    hello > test
```

Correct:

```eo
[] > main
  [] > foo
    hello > test
```
