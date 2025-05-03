# '@base' attributes of objects must be merged if possible

In [XMIR], the object must be merged with parent if the following
conditions are met:

1. Object is not abstract
2. Attribute `@base` of parent object should start with dot
3. Attribute `@base` object should not start with dot
4. Object should not have data

Incorrect:

```xml
<o base=".foo">
    <o base=".bar">
        <o base="x"/>
    </o>
</o>
```

Correct:

```xml
<o base="x.bar.foo"/>
```

Correct:

```xml
<o base=".foo">
    <o base=".bar"/>
</o>
```

Correct:

```xml
<o base=".foo">
    <o/>
</o>
```

Correct:

```xml
<o base=".foo">
    <o base="bar">
        A1-B2-C3-D4-E5
    </o>
</o>
```

Correct:

```xml
<o base="foo">
    <o base=".bar"/>
</o>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
