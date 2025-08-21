# Alias too long

Object aliases must have **at most 2 parts**.

Incorrect:

```eo
+alias a b c

# Foo.
[] > foo
```

Correct:

```eo
+alias a
+alias a b
+alias a b.c.d.e

# Foo.
[] > foo
```
