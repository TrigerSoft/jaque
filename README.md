# JAva QUEry

This library enables language-level code expressions to be represented as objects in the form of expression trees at runtime:

```java
void method(Predicate<Customer> p) {
  LambdaExpression<Predicate<Customer>> parsed = LambdaExpression.parse(p);
  //Use parsed Expression Tree...
}
```

making it possible to create type-safe fluent interfaces, i.e. instead of:

```java
Customer obj = ...
obj.property("name").eq("John")
```

one can write

```java
method<Customer>(obj -> obj.getName() == "John")
```

in type-safe, refactoring friendly manner. And then the library developer will be able to parse the produced Lambda to the corresponding Expression Tree for analysis.

The [jdk.internal.lambda.dumpProxyClasses](https://bugs.openjdk.java.net/browse/JDK-8023524) system property must be set and point to an existing writable directory to give the parser access to the lambda byte code.

#### Resources

- [Full Docs](http://trigersoft.github.io/jaque) [(noframes)](http://trigersoft.github.io/jaque/overview-summary.html)
- [Maven Artifact](http://search.maven.org/#artifactdetails%7Ccom.trigersoft%7Cjaque%7C2.0.3%7Cjar)
- [Technical Details](https://github.com/TrigerSoft/jaque/wiki/Technical-Details)

[![Build Status](https://travis-ci.org/TrigerSoft/jaque.svg?branch=master)](https://travis-ci.org/TrigerSoft/jaque)
