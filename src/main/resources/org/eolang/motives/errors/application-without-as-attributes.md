# Application without `@as` attributes

In the [XMIR], in the application, all inner objects must contain `@as`
attributes.

Incorrect:

```xml
<o base="Q.org.eolang.start" name="@" line="2">
  <o base="Q.org.eolang.x" line="3"/>
  <o base="Q.org.eolang.y" line="4"/>
</o>
```

Correct:

```xml
<o base="Q.org.eolang.start" name="@" line="2">
  <o base="Q.org.eolang.x" as="α0" line="3"/>
  <o base="Q.org.eolang.y" as="α1" line="4"/>
</o>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
