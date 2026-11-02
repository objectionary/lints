# Empty alias

Object aliases must not be empty.

Incorrect:

```eo
# Foo.

+alias

[] > foo
```

Correct:

```eo
# Foo.

+alias a
+alias b c
+alias d e f
+alias g h i j

[] > foo
```
