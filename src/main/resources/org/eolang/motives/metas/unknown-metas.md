# Unknown metas

The following metas are supported:

* `+package`
* `+alias`
* `+version`
* `+rt`
* `+architect`
* `+home`
* `+unlint`
* `+probe`
* `+syntax`

Incorrect:

```eo
+foo
+bar

[] > foo
```

Correct:

```eo
+package com.test
+alias foo
+version 0.0.0
+architect yegor256@gmail.com
+rt jvm
+home https://earth.com
+syntax 0.59.0
+unlint unsorted-metas

[] > foo
```
