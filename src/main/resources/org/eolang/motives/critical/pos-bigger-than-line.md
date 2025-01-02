# `@pos` Bigger Than `@line`

In [XMIR], each `<o/>` must have the value of it's `@pos` attribute not bigger
than the value of it's `@line` attribute.

Incorrect:

```xml
<program>
  <objects>
    <o line="3" pos="4" name="foo"/>
  </objects>
</program>
```

Correct:

```xml
<program>
  <objects>
    <o line="3" pos="3" name="foo"/>
  </objects>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
