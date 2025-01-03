# Architect Duplicate

Each `.eo` file must have only one architect.

Incorrect:

```eo
+architect jeff@google.com
+architect ivan@google.com

# Foo.
[] > foo
```

Correct:

```eo
+architect jeff@google.com

# Foo.
[] > foo
```
