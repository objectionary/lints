# Alias Too Long

Object's alias must have **2 parts at max**.

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
