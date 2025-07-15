# Object has data

Each object `<o/>` in [XMIR] must not have the data, unless it's base is
`org.eolang.bytes` (or just `bytes` for short).

Incorrect:

```xml
<object>
  <o name="bar" base="foo">
    <o>A1-B2-C3-D4-E5</o>
  </o>
</object>
```

Correct:

```xml
<object>
  <o name="bar" base="org.eolang.bytes">
    <o>A1-B2-C3-D4-E5</o>
  </o>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
