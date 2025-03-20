# Incorrect `+spdx`

Tail of special meta `+spdx` should be [SPDX]-compliant headers.

Incorrect:

```eo
+spdx foo bar

# Foo.
[] > foo
```

Correct:

```eo
+spdx SPDX-License-Identifier: MIT

# Foo.
[] > foo
```

In [XMIR], spdx meta should look like this:

```xml
<metas>
  <meta>
    <head>spdx</head>
    <tail>SPDX-License-Identifier: MIT</tail>
    <part>SPDX-License-Identifier:</part>
    <part>MIT</part>
  </meta>
  <meta>
    <head>spdx</head>
    <tail>SPDX-FileCopyrightText Copyright (c) 2016-2025 Objectionary.com</tail>
    <part>SPDX-FileCopyrightText</part>
    <part>Copyright</part>
    <part>(c)</part>
    <part>2016-2025</part>
    <part>Objectionary.com</part>
  </meta>
</metas>
```

Please note, that these two contain valid SPDX headers. Thus, you can use any
number of SPDX-compliant headers.

[SPDX]: https://en.wikipedia.org/wiki/Software_Package_Data_Exchange
[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
