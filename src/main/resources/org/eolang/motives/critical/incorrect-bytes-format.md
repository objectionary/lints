# Incorrect bytes format

The byte format inside [XMIR] objects must follow this regular expression:

```regexp
^--|[0-9A-F]{2}(-|(-[0-9A-F]{2})+)$
```

Incorrect:

```xml
<object>
  <o name="app">
    <o name="a">
      hello, world!
    </o>
    <o name="b">
      test
    </o>
    <o name="c">
      AB
    </o>
    <o name="d">
      12
    </o>
    <o name="e">
      F
    </o>
    <o name="f">
      FF-1
    </o>
    <o name="g">
      哈囉世界
    </o>
  </o>
</object>
```

Correct:

```xml
<object>
  <o name="app">
    <o name="a">
      --
    </o>
    <o name="b">
      12-34
    </o>
    <o name="c">
      AB-12
    </o>
    <o name="d">
      AB-CD
    </o>
    <o name="e">
      AB-CD-EF
    </o>
    <o name="f">
      01-23-45
    </o>
    <o name="g">
      FF-FF-FF
    </o>
    <o name="h">
      A1-B2-C3-D4-E5
    </o>
    <o name="i">
      10-20-30-40-50
    </o>
    <o name="z"/>
  </o>
</object>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
