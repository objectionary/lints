# Comments must be valid markdownlint texts

Comments in the code must be checked for correctness from the point of view of
[markdownlint](https://github.com/DavidAnson/markdownlint)

## Examples

### [MD001](https://github.com/DavidAnson/markdownlint/blob/main/doc/md001.md)

Incorrect:

```eo
# # Main object.
#
# This is main object of program.
#
# ### Subsection of main object.
#
# Sub information about main object. 
[] > foo
```

Output:

```shell
[foo invalid-markdown-comment WARNING]:5 [MD001] Heading levels should only increment by one level at a time. See https://github.com/DavidAnson/markdownlint/blob/v0.37.4/doc/md001.md
```

Correct:

```eo
# # Main object.
#
# This is main object of program.
#
# ## Subsection of main object.
#
# Sub information about main object. 
[] > foo
```

### [MD004](https://github.com/DavidAnson/markdownlint/blob/main/doc/md004.md)

Incorrect:

```eo
# Main object.
#
# - Item1
# + Item2
# * Item3
[] > foo
```

Output:

```shell
[foo invalid-markdown-comment WARNING]:4 [MD004] Unordered list style. See https://github.com/DavidAnson/markdownlint/blob/v0.37.4/doc/md004.md
[foo invalid-markdown-comment WARNING]:5 [MD004] Unordered list style. See https://github.com/DavidAnson/markdownlint/blob/v0.37.4/doc/md004.md
```

Correct:

```eo
# Main object.
#
# - Item1
# - Item2
# - Item3
[] > foo
```

### [MD018](https://github.com/DavidAnson/markdownlint/blob/main/doc/md018.md)

Incorrect:

```eo
# #Main object.
[] > foo
```

Output:

```shell
[foo invalid-markdown-comment WARNING]:1 [MD018] No space after hash on atx style heading. See https://github.com/DavidAnson/markdownlint/blob/v0.37.4/doc/md018.md
```

Correct:

```eo
# # Main object.
[] > foo
```

## Other information

You can find all the rules here
<https://github.com/DavidAnson/markdownlint/blob/main/doc/Rules.md>

We ignore the following errors:

* [MD041](https://github.com/DavidAnson/markdownlint/blob/main/doc/md041.md)
* [MD047](https://github.com/DavidAnson/markdownlint/blob/main/doc/md047.md)
* [MD026](https://github.com/DavidAnson/markdownlint/blob/main/doc/md026.md)
