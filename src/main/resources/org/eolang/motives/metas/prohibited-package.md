# Prohibited `+package`

The special package `org.eolang` is reserved for internal objects only and cannot
be used externally.

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
