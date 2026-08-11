# Bad test name

Every unit test object must be named to describe the behavior it verifies,
with a prefix that matches the kind of the test.

A positive test, declared with `+>`, asserts that the object under test does
something. Its name must start with `can-` or `accepts-`.

A negative test, declared with `->`, asserts that the object under test
refuses to do something. Its name must start with `cannot-`, `rejects-`, or
`stops-on-`.

Incorrect:

```eo
[] > foo
  [] +> it-works
    42 > @

  [] +> stops-on-zero
    42 > @

  [] -> can-divide-by-zero
    42 > @
```

Correct:

```eo
[] > foo
  [] +> can-add-two-numbers
    42 > @

  [] +> accepts-negative-numbers
    42 > @

  [] -> stops-on-zero
    42 > @
```
