# `@pos` Bigger Than `@line`

In [XMIR], each `<o/>` must have the value of it's `@pos` attribute not bigger
than the length of the `@line` it belongs to.

Incorrect:

```xml
<program>
  <objects>
    <o line="2" pos="5" name="foo">
      <o line="2" name="xyz">
        <o line="2">data</o>
      </o>
    </o>
  </objects>
</program>
```

Since, line with `foo` object has length `4`, while `@pos` points to the `5`.

Correct:

```xml
<program>
  <objects>
    <o line="2" pos="2" name="foo">
      <o line="2" name="xyz">
        <o line="2">data</o>
      </o>
    </o>
  </objects>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
