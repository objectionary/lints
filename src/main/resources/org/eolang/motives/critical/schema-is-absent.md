# Schema Is Absent

[XMIR] document must have `xsi:noNamespaceSchemaLocation` inside it.

Incorrect:

```xml
<object>
  <o/>
</object>
```

Correct:

```xml
<object xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="https://www.eolang.org/xsd/XMIR-anything.xsd">
  <o/>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
