# Constant unit test

The `@` attribute of a unit test must not be a literal,
  such as `true`, `false`, a number, a string, or bytes.
A literal is the same no matter what the code under test does,
  so such a test always passes, or always fails,
  and checks nothing.

Incorrect:

```eo
# Foo.

[] > foo
  42 > @

  [] +> returns-forty-two
    true > @
```

Correct:

```eo
# Foo.

[] > foo
  42 > @

  [] +> returns-forty-two
    eq. > @
      foo
      42
```
