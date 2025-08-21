# Mandatory `+spdx`

The special meta `+spdx` must be present in each `.eo` file.

Incorrect:

```eo
# Foo.
[] > foo
```

Correct:

```eo
+spdx SPDX-License-Identifier: MIT

# Foo.
[] > foo
```

In [XMIR], `spdx` meta should look like this:

```xml
<meta>
  <head>spdx</head>
  <tail>SPDX-License-Identifier: MIT</tail>
  <part>SPDX-License-Identifier:</part>
  <part>MIT</part>
</meta>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
