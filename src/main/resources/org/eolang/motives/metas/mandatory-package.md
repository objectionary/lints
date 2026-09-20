# Mandatory `+package` Meta

The program must have a single `+package` special meta.

Incorrect:

```eo
[] > foo
```

```eo
+package org.eolang
+package org.eo

[] > foo
```

Correct:

```eo
+package org.eolang

[] > foo
```
