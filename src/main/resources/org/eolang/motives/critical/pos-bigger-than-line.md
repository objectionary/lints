# `@pos` Bigger Than `@line`

In [XMIR], each `<o/>` must have the value of it's `@pos` attribute not bigger
than the length of the `@line` it belongs to.

Incorrect:

```xml
<program>
  <listing># No comments.
    [] &gt;foo</listing>
  <objects>
    <o line="2" pos="10" name="foo"/>
  </objects>
</program>
```

Since, line with `foo` object has length `7` (according to the `<listing/>`),
while `@pos` points to the `10`.

Correct:

```xml
<program>
  <listing># No comments.
    [] &gt;foo</listing>
  <objects>
    <o line="2" pos="3" name="foo"/>
  </objects>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
