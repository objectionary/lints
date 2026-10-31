# Alias too long

Object aliases must have **at most 2 parts**.

Incorrect:

```eo
# Foo.

+alias a b c

[] > foo
```

Correct:

```eo
# Foo.

+alias a
+alias b c
+alias d e.f.g.h

[] > foo
```
