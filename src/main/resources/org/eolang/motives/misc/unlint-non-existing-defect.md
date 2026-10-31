# `+unlint` of non-existing defect

The special `+unlint` meta should be used only to suppress existing defects.

Incorrect, since there are no duplicate metas here:

```eo
+unlint duplicate-metas

[] > foo
  42 > @
```

Correct:

```eo
+unlint duplicate-metas
+architect jeff
+architect jeff

[] > foo
  42 > @
```
