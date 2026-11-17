# `+package` contains more than one part

The special `+package` meta must contain a single value.
The EO parser already rejects a `+package` with more than one argument,
so this defect shows up in [XMIR] produced by other tools.

Incorrect:

```xml
<object>
  <metas>
    <meta>
      <head>package</head>
      <tail>foo bar</tail>
      <part>foo</part>
      <part>bar</part>
    </meta>
  </metas>
  <o name="foo"/>
</object>
```

Correct:

```eo
+package foo.bar

[] > foo
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
