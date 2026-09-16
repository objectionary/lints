# Named object abstract nested

In [XMIR], named objects cannot be nested more than one level deep inside an
abstract object.

This shape can't be written directly in EO text. An indented named
child of an application is hoisted up to the enclosing formation by
the parser, so it's shown as XMIR instead:

Incorrect:

```xml
<o base="x" name="φ">
  <o base="y" name="a">
    <o base="z" name="b"/>
  </o>
</o>
```

Correct:

```eo
[] > foo
  num > abc
  xyz $.abc > @
```

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
