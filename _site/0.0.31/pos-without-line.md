# `@pos` Without `@line`

In [XMIR], each `<o/>` that has `@pos` attribute, must have `@line` attribute
as well.

Incorrect:

```xml
<program>
  <objects>
    <o pos="3" name="foo"/>
  </objects>
</program>
```

Correct:

```xml
<program>
  <objects>
    <o line="2" pos="3" name="foo"/>
  </objects>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
