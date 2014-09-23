# JAva QUEry

library for parsing Java Lambdas to Expression Trees in runtime:

```java
void method(Predicate<Float> p) {
  LambdaExpression<Predicate<Float>> parsed = LambdaExpression.parse(p);
  //Use parsed Expression Tree...
}
```

The [jdk.internal.lambda.dumpProxyClasses](https://bugs.openjdk.java.net/browse/JDK-8023524) system property must be set and point to an existing writable directory to give the parser access to the lambda byte code.

#### Resources

//maven link
//Documentation (link)

[![Build Status](https://travis-ci.org/TrigerSoft/jaque.svg?branch=master)](https://travis-ci.org/TrigerSoft/jaque)
