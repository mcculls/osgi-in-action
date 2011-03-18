
OSGi in Action source code examples
===================================

Requirements:

 Java SDK (1.5 or later http://java.sun.com/javase/downloads/index.jsp)
 Apache Ant (1.7 or later http://ant.apache.org/bindownload.cgi)

Contents:

 chapter01

  * greeting-example          - "Hello, world" using each OSGi layer in turn

 chapter02

  * paint-nonmodular          - Original non-OSGi paint application

  * paint-modular             - Painting with modularity

 chapter03

  * shell-example             - Remote shell example (telnet 127.0.0.1 7070)

  * paint-example             - Painting with lifecycles (extender pattern)

 chapter04

  * dynamics                  - Examples & counter-examples of handling services

  * paint-example             - Painting with services (whiteboard pattern)

 chapter05

  * paint-example             - Painting with advanced modularity

 chapter06

  * BeanUtils-example         - Wrapping BeanUtils

  * HttpClient-example        - Wrapping HttpClient

  * jEdit-example             - Migrating jEdit to OSGi

 chapter07

  * migration-example         - Migrating tests to OSGi

  * mocking-example           - Mocking OSGi APIs

  * testing-example           - OSGi integration testing

 chapter08

  * debugging-bundles         - Debugging OSGi code

  * classloading              - Common classloading issues

  * memory-leaks              - OSGi memory leak example

  * dangling-services         - Dangling service example

 chapter09

  * combined-example          - Managed shell example

 chapter10

  * combined-example          - Managed paint example

 chapter11

  * non-component-example     - Non-component example

  * paint-example-common      - Shared component code

  * paint-example-ds          - Declarative Services example

 chapter12

  * non-component-example     - Non-component example

  * paint-example-common      - Shared component code

  * paint-example-bp          - Blueprint example

  * paint-example-bp-fragment - Blueprint fragment example

  * paint-example-ip          - Apache Felix iPOJO example

  * paint-example-mixed       - Mixed component model example

 chapter13

  * paint-example             - Launching and embedding example

 chapter14

  * combined-example          - Secure paint example

 chapter15

  * httpservice               - Simple servlet example

  * pax-web                   - Advanced JSP example

  * gwtapp                    - OSGi web application (GWT)

  * webservice                - Remote OSGi service example

  * webservice-client         - ... client

  * webservice-impl           - ... distribution provider

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

