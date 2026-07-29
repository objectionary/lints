# Bad test name

Every unit test object must be named to describe the behavior it verifies,
starting with one of these prefixes: `can-`, `cannot-`, `accepts-`,
`rejects-`, or `stops-on-`.

Incorrect:

```eo
[] > foo
  [] +> it-works
    42 > @
```

Correct:

```eo
[] > foo
  [] +> can-add-two-numbers
    42 > @
```
