# Mandatory `+package` Meta

The program must have exactly one `+package` special meta.

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
