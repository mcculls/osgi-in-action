
OSGi in Action source code examples
===================================

Requirements:

 Java SDK (at least 1.5 or above for certain examples)
 Apache Ant (http://ant.apache.org/bindownload.cgi)

Contents:

 chapter01

  * greeting-example - "Hello, world" using each OSGi layer in turn

 chapter02

  * paint-nonmodular - Original non-OSGi paint application

  * paint-example    - Painting with modularity

 chapter03

  * org.foo.shell    - Remote shell example (telnet 127.0.0.1 8080)

  * paint-example    - Painting with lifecycles (extender pattern)

 chapter04

  * dynamics         - Examples & counter-examples of handling services

  * paint-example    - Painting with services (whiteboard pattern)

Building:

 To build all the examples, just type "ant" from the top directory.

Running:

 A few examples are packaged as normal Java applications, to run these type

   java -jar main.jar

 Most examples are packaged as collections of OSGi bundles, to run these type

   java -jar launcher.jar bundles

 The source for the basic OSGi launcher can be found in the "launcher" directory.

Additional Ant targets:

 ant clean - remove all compiled/cached files

 ant wipe  - remove all generated content

 ant dist  - build examples (this is the default target)

 ant pde   - generate Eclipse/PDE project files

Please raise any questions or issues at http://code.google.com/p/osgi-in-action/

