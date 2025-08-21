# `@pos` without `@line`

In [XMIR], each `<o/>` element with a `@pos` attribute must also have a `@line` attribute.

Incorrect:

```xml
<object>
  <o pos="3" name="foo"/>
</object>
```

Correct:

```xml
<object>
  <o line="2" pos="3" name="foo"/>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
