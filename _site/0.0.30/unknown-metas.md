# Unknown Metas

The following metas are supported:

* `+package`
* `+alias`
* `+version`
* `+tests`
* `+rt`
* `+architect`
* `+home`
* `+unlint`
* `+probe`

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
+unlint unsorted-metas

[] > foo
```
