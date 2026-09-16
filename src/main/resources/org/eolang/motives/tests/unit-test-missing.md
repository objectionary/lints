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

  [] +> prints-hello-world
    stdout > @
      "Hello, world"
```
