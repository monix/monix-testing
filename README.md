# Monix Testing

Library aimed to provide support to integrate Monix Task with different testing frameworks.

## Releases

Available for **Scala 2.12** and **2.13** and **3**.
## Credits

This project is ported from [typelevel/cats-effect-testing](https://github.com/typelevel/cats-effect-testing), with the difference that this library provides support for `Task` instead of `IO`. So credits to their authors and maintainers🙏

Mainly with the purpose of supporting task-testing on monix series 3.x, 
since once monix is compatible with cats effect `3.0` this project will most likely not be longer useful, 
since then we will be able to use `cats-effect-testing` 1.x 
which is implemented with _Tagless Final_, thus compatible with
either `IO` and `Task`.

## Scalatest

Add the following dependency to your *build.sbt*:

```sbt
"io.monix" %% "monix-testing-scalatest" % "0.3.0"
```

   It provides a set of implicit conversions to convert [[Task]] to [[Future]], so the
   user does not need to do it for every test.
  
  ```scala
   import monix.eval.Task
   import monix.execution.Scheduler
   import monix.testing.scalatest.MonixTaskTest
   import org.scalatest.funsuite.AsyncFunSuite
   import org.scalatest.matchers.should.Matchers
  
   class DummySpec extends AsyncFunSuite with MonixTaskTest with Matchers {
  
       override implicit def scheduler: Scheduler = Scheduler.io("monix-task-support-spec")
  
       test("AsyncTestSuite with Task support") {
           for {
               r1 <- Task(2)
               r2 <- Task(r1 * 3)
           } yield {
               r1 shouldBe 2
               r2 shouldBe 6
           }
       // no need for tranforming the [[Task]] to [[Future]] or to awaiting for its result.
       }
  
       test("AsyncTestSuite with Task and AssertingSyntax support") {
           Task(2).flatMap(r1 => Task(r1 * 3)).asserting(_ shouldBe 6)
       }
   }
   ```


## µTest

Add the following dependency to your *build.sbt*:

```sbt
"io.monix" %% "monix-testing-utest" % "0.3.0"
```

It provides a wrapper conversions from [[Task]] to [[Future]].

  ```scala
   import monix.eval.Task
   import monix.execution.Scheduler
   import monix.testing.utest.MonixTaskTest
   import utest.{Tests, test}

   import scala.concurrent.duration._

class DummySuite extends MonixTaskTest {

   override implicit val scheduler: Scheduler = Scheduler.io("monix-task-test")

   override val timeout = 1.second

   val tests = Tests {
      test("dummy test") {
         Task(assert(true))
      }
   }
}
  ```





## Minitest



Minitest is very similar to uTest, but being strongly typed:

Add the following dependency to your *build.sbt*:

```sbt
"io.monix" %% "monix-testing-utest" % "0.3.0"
```

```scala
  import monix.eval.Task
  import monix.execution.Scheduler
  import monix.testing.minitest.MonixTaskTest

  import scala.concurrent.duration._

object DummySuite extends MonixTaskTest {
  override val timeout = 1.second // Default timeout is 10 seconds

   override protected implicit val scheduler: Scheduler = Scheduler.global

   test("dummy test") {
    Task(assert(true))
  }
}
```


## Specs2

Finally, `specs2` can directly be used from `"com.codecommit" %% "cats-effect-testing-specs2" % "0.5.4"`, the latest release
that is compatible with monix 3.x.

  ```scala
   import monix.eval.Task
   import monix.execution.Scheduler
   import cats.effect.testing.specs2.CatsEffect
   import org.specs2.mutable.Specification

   import scala.concurrent.duration._

implicit val scheduler: Scheduler = Scheduler.global

class DummySpec extends Specification with CatsEffect {
   "dummy test" should {
      "be successful" in Task {
         true must beTrue
      }
   }
}
  ```
