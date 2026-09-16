# Same-package alias

An `+alias` meta that points to an object in the same package as the
current file's `+package` meta is redundant. The compiler resolves a
bare reference to an object of the same package on its own, so such
an alias only adds noise.

Incorrect:

```eo
# Foo.

+package org.eolang.txt
+alias org.eolang.txt.sprintf

[x] > foo
  sprintf x > @
```

Correct:

```eo
# Foo.

+package org.eolang.txt

[x] > foo
  sprintf x > @
```

An alias that points to a different package, or one that renames the
object to a new local name, is not redundant and is left alone:

```eo
# Foo.

+package org.eolang.txt
+alias sp org.eolang.txt.sprintf

[x] > foo
  sp x > @
```
