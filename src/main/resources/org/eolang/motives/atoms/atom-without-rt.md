# Atom without `rt` meta

Defined atoms must have a `+rt` meta.

Incorrect:

```eo
+architect jeff@foo.com
+home https://github.com/objectionary/eo
+package org.eolang
+version 0.0.0

[] > bytes
  [] > eq /Q.boolean
  [] > not /Q.boolean
```

Correct:

```eo
+architect jeff@foo.com
+home https://github.com/objectionary/eo
+package org.eolang
+version 0.0.0
+rt jvm org.eolang:eo-runtime:0.43.2

[] > bytes
  [] > eq /Q.boolean
  [] > not /Q.boolean
```
