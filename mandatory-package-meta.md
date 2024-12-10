# Mandatory `+package` Meta

Program should have only one `+package` special meta.

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
