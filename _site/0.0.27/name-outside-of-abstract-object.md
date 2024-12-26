# `@name` Outside of Abstract Object

The `@name` attribute may only be present in `<o/>` in [XMIR], only if the
parent of the object has `@abstract` attribute.

Incorrect:

```xml
<o base="foo">
  <o name="bar"/>
</o>
```

Correct:

```xml
<o>
  <o name="bar"/>
</o>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
