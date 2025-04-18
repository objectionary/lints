# `@line` Is Absent

In [XMIR], each `<o/>` with it's `@base`, must have `@line` attribute as well.

Incorrect:

```xml
<object>
  <o>
    <o name="bar" base="foo"/>
  </o>
</object>
```

Correct:

```xml
<object>
  <o>
    <o name="bar" base="foo" line="1"/>
  </o>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
