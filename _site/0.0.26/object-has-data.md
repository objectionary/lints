# Object Has Data

Each object `<o/>` in [XMIR] must not have the data, unless it's base is
`org.eolang.bytes` (or just `bytes` for short).

Incorrect:

```xml
<program>
  <objects>
    <o name="bar" base="foo">
      A1-B2-C3-D4-E5
    </o>
  </objects>
</program>
```

Correct:

```xml
<program>
  <objects>
    <o name="bar" base="org.eolang.bytes">
      A1-B2-C3-D4-E5
    </o>
  </objects>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
