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

  [] +> it-works
    42 > @

  [] +> testing
    42 > @

  [] +> should-not-pass
    42 > @
```

Correct:

```eo
# Foo.

[] > foo

  [] +> runs
    42 > @

  [] +> parses-dom
    42 > @

  [] +> generates-report
    42 > @
```
