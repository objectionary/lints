# Empty alias

Object aliases must not be empty.
The EO parser already rejects a bare `+alias`, so this defect shows up in [XMIR]
produced by other tools.

Incorrect:

```xml
<object>
  <metas>
    <meta>
      <head>alias</head>
      <tail/>
    </meta>
  </metas>
  <o name="foo"/>
</object>
```

Correct:

```eo
# Foo.

+alias a
+alias b c
+alias d e f
+alias g h i j

[] > foo
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
