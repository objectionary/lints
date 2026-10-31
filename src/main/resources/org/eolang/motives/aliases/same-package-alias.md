# Same-package alias

An `+alias` meta that points to an object in the same package as the
current file's `+package` meta is redundant. The compiler resolves a
bare reference to an object of the same package on its own, so such
an alias adds nothing but noise.

Incorrect:

```eo
+package org.eolang.txt
+alias org.eolang.txt.sprintf

# Foo.
[x] > foo
  sprintf x > @
```

Correct:

```eo
+package org.eolang.txt

# Foo.
[x] > foo
  sprintf x > @
```

An alias that points to a different package, or one that renames the
object to a new local name, is not redundant and is left alone:

```eo
+package org.eolang.txt
+alias sp org.eolang.txt.sprintf

# Foo.
[x] > foo
  sp x > @
```
