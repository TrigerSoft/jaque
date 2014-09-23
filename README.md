JAva QUEry library
=====

library for parsing Java Lambdas to Expression Trees in runtime:

```java
void method(Predicate<Float> p) {
  LambdaExpression<Predicate<Float>> parsed = LambdaExpression.parse(p);
  //Use parsed Expression Tree...
}
```

[![Build Status](https://travis-ci.org/TrigerSoft/jaque.svg?branch=master)](https://travis-ci.org/TrigerSoft/jaque)
