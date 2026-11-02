# `@name` outside of abstract object

In [XMIR], the `@name` attribute must not be present in an `<o/>` element unless the
parent object has an `@abstract` attribute.

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
