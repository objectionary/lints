# Idempotent attribute is not first

The idempotent attribute (object with `@name="ξ"` and `@name="xi🌵"`) must be
declared first in the formation.

Incorrect:

```xml
<o name="foo">
  <o base="∅" name="x"/>
  <o base="ξ" name="xi🌵"/>
</o>
```

Correct:

```xml
<o name="foo">
  <o base="ξ" name="xi🌵"/>
  <o base="∅" name="x"/>
</o>
```
