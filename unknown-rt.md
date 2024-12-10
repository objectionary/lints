# Unknown `+rt`

Special `+rt` meta must use only allowed values at it's first part. Currently,
the following runtimes are supported:

* [jvm](https://github.com/objectionary/eo)
* [node](https://github.com/objectionary/eo2js)

Incorrect:

```eo
+rt test
+rt foo bar
[] > foo
```

Correct:

```eo
+rt jvm org.eolang:eo-runtime:0.0.0
+rt node eo2js-runtime:0.0.0
[] > foo
```
