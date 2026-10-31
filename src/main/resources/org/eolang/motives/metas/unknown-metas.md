# Unknown metas

The following metas are supported:

* `+package`
* `+alias`
* `+also`
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
+also foo
+version 0.0.0
+architect yegor256@gmail.com
+rt jvm
+home https://earth.com
+unlint unsorted-metas
+syntax 0.1.0

[] > foo
```
