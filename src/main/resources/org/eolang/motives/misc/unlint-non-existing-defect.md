# `+unlint` of non-existing defect

The special `+unlint` meta should be used to suppress existing defects alone.

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
