
Dynamic service examples
========================

This folder contains several alternative implementations of a logging client.
Several examples are intentionally broken to demonstrate common pitfalls when
dealing with dynamic OSGi services.

To see a menu of the different examples, use the "PICK_EXAMPLE" script:

  Dynamic service examples
  ------------------------

  1) Broken lookup (dangling field)
  2) Broken lookup (one-time lookup)
  3) Broken lookup (race condition)
  4) Correct lookup
  5) Broken listener
  6) Correct listener
  7) Standard tracker
  8) Customized tracker
  0) exit

  Choose an example (1-8):

Enter a number, press return and the example should build and launch itself.
You can also pass this directly to the script, for example "PICK_EXAMPLE 1",
in which case the menu won't appear.

