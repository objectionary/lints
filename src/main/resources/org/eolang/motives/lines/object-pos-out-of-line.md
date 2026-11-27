# Object position out of line

In [XMIR], the `@pos` attribute of an `<o/>` is the position
of the first character of the object inside the line of the listing
that its `@line` points to, counting from zero.
Thus, it must be smaller than the number of characters in that line.

Incorrect:

```xml
<object>
  <listing>[] &gt; foo
  42 &gt; @</listing>
  <o line="1" pos="0" name="foo">
    <o line="2" pos="40" base="Φ.number" name="φ"/>
  </o>
</object>
```

Correct:

```xml
<object>
  <listing>[] &gt; foo
  42 &gt; @</listing>
  <o line="1" pos="0" name="foo">
    <o line="2" pos="2" base="Φ.number" name="φ"/>
  </o>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
