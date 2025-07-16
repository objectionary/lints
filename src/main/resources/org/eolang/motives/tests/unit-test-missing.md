# Unit test missing

Each live object should have unit tests inside.

Incorrect:

```eo
# Foo.
[] > foo
```

Correct:

```eo
# Foo.
[] > foo
  # Test works.
  [] +> prints-hello-world
    stdout > @
      "Hello, world"
```
