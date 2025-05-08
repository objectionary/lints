# Application duality

In EO, it isn't supported to have an app with and without binding at
the same time, so it's prohibited in the [XMIR] too.

Incorrect:

```xml
<o base="man">
  <o base="string" as="name"/>
  <o base="number"/>
</o>
```

Correct:

```xml
<o base="man">
  <o base="string" as="name"/>
  <o base="number" as="age"/>
</o>
```

Or

```xml
<o base="man">
  <o base="string"/>
  <o base="number"/>
</o>
```

[XMIR]: https://news.eolang.org/2022-11-25-xmir-guide.html
