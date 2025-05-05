# Duplicate `@as` Attribute

In [XMIR], object shouldn't have `@as` attributes duplicated inside.

Incorrect:

```xml
<o base="foo">
  <o base="f1" as="bar"/>
  <o base="f1" as="bar"/>
</o>
```

Correct:

```xml
<o base="foo">
  <o base="f1" as="bar"/>
  <o base="f1" as="buzz"/>
</o>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
