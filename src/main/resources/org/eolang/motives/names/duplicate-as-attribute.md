# Duplicate `@as` Attribute

In [XMIR], objects must not have duplicate `@as` attributes.
The EO parser already rejects an argument bound to a name another argument
has taken, so this defect shows up in [XMIR] produced by other tools.

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
