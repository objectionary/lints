# Void attributes `∅` not higher than other

In [XMIR], all void attributes must be placed before non-void attributes.

Incorrect:

```xml
<o name="foo">
  <o base="∅" name="x"/>
  <o base="Φ.foo" name="z"/>
  <o base="∅" name="y"/>
</o>
```

Correct:

```xml
<o name="foo">
  <o base="∅" name="x"/>
  <o base="∅" name="y"/>
  <o base="Φ.foo" name="z"/>
</o>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
