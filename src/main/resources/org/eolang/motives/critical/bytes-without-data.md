# Bytes Without Data

Each object `<o/>` in [XMIR], with its base containing `org.eolang.bytes` must
have text data inside.

Incorrect:

```xml
<program>
  <objects>
    <o name="bar" base="foo"/>
  </objects>
</program>
```

Correct:

```xml
<program>
  <objects>
    <o name="bar" base="foo">
      A1-B2-C3-D4-E5
    </o>
  </objects>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
