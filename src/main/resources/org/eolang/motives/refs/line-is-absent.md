# `@line` is absent

In [XMIR], each `<o/>` element with a `@base` attribute must also have a `@line` attribute.

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
