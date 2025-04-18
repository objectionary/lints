# Bytes Without Data

Each object `<o/>` in [XMIR], with its parent base containing
`Q.org.eolang.bytes` must have text data inside.

Incorrect:

```xml
<object>
  <o name="bar" base="org.eolang.bytes">
    <o/>
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
