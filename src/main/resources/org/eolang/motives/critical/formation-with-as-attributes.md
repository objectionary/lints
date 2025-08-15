# Formation with `@as` attributes

In [XMIR], it's prohibited to have `@as` attributes inside the formation. The
only valid usage for them is object app.

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
