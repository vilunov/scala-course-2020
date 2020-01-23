# Lab 1: SBT & Rational
> **Due Date**: 30.01.2020 9:00 AM


## Stage 1 – Simple Build Tool (sbt)

1. Download and Install IntelliJ IDEA. [Link](https://www.jetbrains.com/idea/download/)
2. Download and Install the Scala Plugin: [Link](https://plugins.jetbrains.com/plugin/1347-scala)

![](https://i.imgur.com/cQCDpvl.png)

<!-- 3. Congratz! You found an easter egg! Contact your TA for bonus points -->
4. Create a new sbt project using these versions:
- Java Version: `1.8`
- Scala Version: `2.12.10`
- sbt Version: `1.2.8`

Make sure that you selected "Use sbt shell for imports and build".

![](https://i.imgur.com/c4PSNCh.png)

5. Create a package `<your-name>.<your-surname>` in the directory `src/main/scala`.
6. Create a file `Rational.scala` in that package.

## Stage 2 – Rational

```scala
package <your-name>.<your-surname>

class Rational(n: Int, d: Int) { 
  require(d != 0)
  private val g = gcd(n.abs, d.abs)
  val numer = n / g
  val denom = d / g
  def this(n: Int) = this(n, 1)
  def + (that: Rational): Rational = new Rational(numer * that.denom + that.numer * denom, denom * that.denom)
  def + (i: Int): Rational = ???
  def - (that: Rational): Rational = new Rational(numer * that.denom - that.numer * denom, denom * that.denom)
  def - (i: Int): Rational = ???
  def * (that: Rational): Rational = new Rational(numer * that.numer, denom * that.denom)
  def * (i: Int): Rational = ???
  def / (that: Rational): Rational = new Rational(numer * that.denom, denom * that.numer)
  def / (i: Int): Rational = ???
  override def toString = numer + "/" + denom
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
}
```

Tasks:
1. Introduce a companion object `Rational` and move the `gcd` method into that object.
2. Make the constructor of `Rational` private and create a method called `apply` in the companion object, which will call the constructor.
3. Calculate the GCD of the numerator and the denominator in the `apply` method and invoke the constructor of `Rational` inside `apply` with normalized arguments.
4. Implement `def == (other: Rational): Boolean` method to the `Rational` class. Make sure that it outputs `true` for `Rational(4, 2)` and `Rational(2, 1)`. At this stage two objects should be equal iff all their member fields are equal.


## Stage 3 – Tests

Now we are going to write some unit-tests. Open your `build.sbt` file and add the following line:

```scala
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test
```

This line brings the [ScalaTest](http://www.scalatest.org/) framework as a dependency of your project. It will be used to create and run test suites for your code.

Similarly to Stage 1, create a package `<your-name>.<your-surname>` in the directory `src/test/scala`. Notice the directory `test` – it will contain all test-related code and resources. The ScalaTest framework will be unavailable in the main code.

Create a file `RationalSpec.scala` in the `test` section and paste the following code:
```scala
package <your-name>.<your-surname>

import org.scalatest.{FlatSpec, Matchers}

class RationalSpec extends FlatSpec with Matchers {
  "==" should "be correct for 2/1 and 4/2" in {
    val isEqual = Rational(2, 1) == Rational(4, 2)
    isEqual shouldBe true
  }
}
```

You can run the spec via your IDE – notice the green next to class name and individual tests, as well as via your sbt shell – run `test` command in the panel `sbt shell` below. Try to do it both ways.

Task:
- Implement 2 tests for any desired operations of `Rational`

## Stage 4 - REPL

Scala has a convinient REPL that you can use to test your code. To start the REPL with all the dependencies of sbt project - run `console` command in the sbt shell.

Tasks:
- Import your `Rational` implementation into the scope of REPL
- Create two instances and perform any operation
- Screenshot the REPL panel with all entered instructions and outputs

## Deliverables

1. zip archive with your sbt project – 0.5 points
2. screenshot of your REPL output – 0.5 points
