# Application without `@as` attributes

In the [XMIR], in the application, all inner objects must contain `@as`
attributes.

Incorrect:

```xml
<o base="Φ.org.eolang.start" name="φ" line="2">
  <o base="Φ.org.eolang.x" line="3"/>
  <o base="Φ.org.eolang.y" line="4"/>
</o>
```

Correct:

```xml
<o base="Φ.org.eolang.start" name="φ" line="2">
  <o base="Φ.org.eolang.x" as="α0" line="3"/>
  <o base="Φ.org.eolang.y" as="α1" line="4"/>
</o>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
