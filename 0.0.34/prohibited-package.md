# Prohibited `+package`

Special package `org.eolang` is reserved for internal object only, and can not
be used outside.

Incorrect:

```eo
+package org.eolang

[] > foo
```

Correct:

```eo
+package my.awesome.package

[] > foo
```
