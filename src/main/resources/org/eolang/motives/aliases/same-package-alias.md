# Same-package alias

An `+alias` meta that points to an object in the same package as the
current file is redundant. Since the compiler auto-homes a bare
reference to an object of the same package, the alias adds nothing and
should be removed.

Incorrect (the file's package is `org.eolang.txt`):

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

A cross-package alias, or an alias that renames the object to a new
local name, is not redundant and stays untouched.
