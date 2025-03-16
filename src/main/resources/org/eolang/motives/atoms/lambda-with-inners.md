# Lambda With Inners

In [XMIR], atom object cannot have inner objects inside lambda object.

Incorrect:

```xml
<program>
  <objects>
    <o name="obj">
      <o name="λ">
        <o name="foo"/>
      </o>
    </o>
  </objects>
</program>
```

Correct:

```xml
<program>
  <objects>
    <o name="obj">
      <o name="λ"/>
    </o>
  </objects>
</program>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
