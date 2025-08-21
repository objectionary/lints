# Empty alias

Object aliases must not be empty.

Incorrect:

```eo
+alias

# Foo.
[] > foo
```

Correct:

```eo
+alias a
+alias a b
+alias a b c
+alias a b c d

# Foo.
[] > foo
```
