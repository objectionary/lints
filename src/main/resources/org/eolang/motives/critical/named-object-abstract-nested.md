# Named object abstract nested

In [XMIR], named objects can't be nested deeper than one level inside an
abstract object.

Incorrect:

```xml
<o base="x" name="φ">
  <o base="y" name="a">
    <o base="z" name="b"/>
  </o>
</o>
```

Correct:

```xml
<o name="f">
  <o base="x" name="φ">
    <o base="ξ.a"/>
  </o>
  <o base="y" name="a">
    <o base="ξ.b"/>
  </o>
  <o base="z" name="b"/>
</o>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
