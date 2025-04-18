# Lambda With Inners

In [XMIR], atom object cannot have inner objects inside lambda object.

Incorrect:

```xml
<object>
  <o name="obj">
    <o name="λ">
      <o name="foo"/>
    </o>
  </o>
</object>
```

Correct:

```xml
<object>
  <o name="obj">
    <o name="λ"/>
  </o>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
