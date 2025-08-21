# Formation with `@as` attributes

In [XMIR], `@as` attributes are prohibited inside formations. They are
only valid in object applications.

Incorrect:

```xml
<o name="foo">
  <o as='due'/>
</o>
```

Correct:

```xml
<o name="foo" base="Φ.org.eolang.f">
  <o as='due'/>
</o>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
