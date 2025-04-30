# Named Object Abstract Nested

In [XMIR], named objects cannot be nested deeper than one level inside an
abstract object.

Incorrect:

```xml
<o base="x" name="@">
  <o base="y" name="a">
    <o base="z" name="b"/>
  </o>
</o>
```

Correct:

```xml
<o name="f">
  <o base="x" name="@">
    <o base="$.a"/>
  </o>
  <o base="y" name="a">
    <o base="$.b"/>
  </o>
  <o base="z" name="b"/>
</o>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
