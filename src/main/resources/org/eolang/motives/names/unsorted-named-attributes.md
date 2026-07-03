# Unsorted named attributes

Named attributes inside a formation should be listed in alphabetical
order. A stable order makes code easier to read, review, and maintain.

The `@` attribute is allowed to stay first, and test objects prefixed
with `+` keep their own order handled by another lint.

Incorrect:

```eo
[] > app
  x.plus 1 > b
  x.mul 2 > a
```

Correct:

```eo
[] > app
  x.mul 2 > a
  x.plus 1 > b
```
