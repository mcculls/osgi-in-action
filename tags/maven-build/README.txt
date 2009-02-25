OSGi in Action code examples
============================

Requirements:

 Java SDK (at least 1.5 or above for certain examples)
 Apache Maven 2 (http://maven.apache.org/download.html)
 Active connection to the internet

Contents:

 Chapter01

  * greeting-example - "Hello, world" using each OSGi layer in turn

 Chapter02

  * paint-nonmodular - Original non-OSGi paint application

  * paint-example    - Painting with modularity

 Chapter03

  * org.foo.shell    - Remote shell example (telnet 127.0.0.1 8080)

  * paint-example    - Painting with lifecycles (extender pattern)

 Chapter04

  * dynamics         - Examples & counter-examples of handling services

  * paint-example    - Painting with services (whiteboard pattern)

Building:

 To build all the examples, type "mvn install" from the main directory.

 Batch files and shell scripts are also provided for each major example.
 Each script builds the example and either runs it STANDALONE or DEPLOYs
 it to an instance of Apache Felix OSGi framework, along with a console.

 If you want to use Eclipse instead of the command line, then you can
 also generate Eclipse IDE files, by using "mvn install pax:eclipse".

