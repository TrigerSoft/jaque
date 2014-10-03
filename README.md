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

#### How to write fluent interface with JaQue?

- Suppose you want to reference some class property

```java
public class Fluent<T> {

	public Fluent<T> property(Function<T, ?> propertyRef) {
		LambdaExpression<Function<T, ?>> parsed = LambdaExpression
				.parse(propertyRef);
		Expression body = parsed.getBody();
		Expression methodCall = body;
		
		//remove casts
		while (methodCall instanceof UnaryExpression)
			methodCall = ((UnaryExpression) methodCall).getFirst();

		//checks are omitted for brevity
		Member member = ((MemberExpression) ((InvocationExpression) methodCall)
				.getTarget()).getMember();
		
		//use member
		...
		
		return this;
	}
}
```

- Now your users will be able to write

```java
Fluent<Customer> f = new Fluent<Customer>();
f.property(Customer::getName);
```

#### Resources

- [Full Docs](http://trigersoft.github.io/jaque) [(noframes)](http://trigersoft.github.io/jaque/overview-summary.html)
- [Maven Artifact](http://search.maven.org/#artifactdetails%7Ccom.trigersoft%7Cjaque%7C2.0.3%7Cjar)
- [Technical Details](https://github.com/TrigerSoft/jaque/wiki/Technical-Details)
- [Quality Assurance and Continuous Integration](https://github.com/TrigerSoft/jaque/wiki/Quality-Assurance)

[![Build Status](https://travis-ci.org/TrigerSoft/jaque.svg?branch=master)](https://travis-ci.org/TrigerSoft/jaque)
