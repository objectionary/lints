# Unit test is not a verb

Every unit test object, declared with `+>`, must be named after the behavior
it verifies. The name must start with a verb in singular form, so that it
reads as a statement about the object under test: it `runs`, it `parses-dom`,
it `generates-report`.

A name that starts with a noun, a pronoun, or a verb in plural or gerund
form says nothing about what the test asserts. The reader has to open the
body of the test to learn its purpose.

Incorrect:

```eo
# Foo.
[] > foo
  # Test.
  [] +> it-works
    42 > @

  # Test.
  [] +> testing
    42 > @

  # Test.
  [] +> should-not-pass
    42 > @
```

Correct:

```eo
# Foo.
[] > foo
  # Test.
  [] +> runs
    42 > @

  # Test.
  [] +> parses-dom
    42 > @

  # Test.
  [] +> generates-report
    42 > @
```
