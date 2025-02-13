# `+unlint` Of Non-Existing Defect

Special `+unlint` meta should be used only to suppress an existing defects.

Incorrect (since there is no duplicate metas):

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
