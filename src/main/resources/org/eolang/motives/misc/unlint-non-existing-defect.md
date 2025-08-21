# `+unlint` of non-existing defect

The special `+unlint` meta should be used only to suppress existing defects.

Incorrect (since there are no duplicate metas):

```eo
+unlint duplicate-metas

[] > foo
  42 > @
```

Correct:

```eo
+unlint duplicate-metas
+architect jeff
+architect foo

[] > foo
  42 > @
```
