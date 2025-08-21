# Duplicate `@as` Attribute

In [XMIR], objects must not have duplicate `@as` attributes.

Incorrect:

```xml
<o base="foo">
  <o base="Φ.f1" as="bar"/>
  <o base="Φ.f1" as="bar"/>
</o>
```

Correct:

```xml
<o base="foo">
  <o base="Φ.f1" as="bar"/>
  <o base="Φ.f1" as="buzz"/>
</o>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
