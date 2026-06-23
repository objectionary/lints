# Invalid Name Notation

**Lint ID:** `invalid-name-notation`

**Rule:** All object names should follow kebab-case convention.

## What

Names like `my-object` are valid. Names like `MyObject`, `my_object`, or `123test` are not.

## Why

Consistent naming conventions reduce cognitive load when reading EO code. Kebab-case is the [official EO naming convention](https://www.eolang.org/convention.html).

## Examples

```eo
[] > my-object    ✓ valid
[] > MyObject     ✗ invalid (camelCase)
[] > my_object    ✗ invalid (snake_case)
[] > 123test      ✗ invalid (starts with digit)
```

## Fix

Rename the object to follow kebab-case: use only lowercase letters, digits, and hyphens, starting with a letter.
