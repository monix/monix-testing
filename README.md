# Monix Testing

Library aimed to provide support to integrate Monix Task with different testing frameworks.
At the moment only for [scalatest async test suites](https://www.scalatest.org/user_guide/async_testing). 

Add the following dependency to your *build.sbt*:

```sbt
"io.monix" %% "monix-testing-scalatest" % "0.2.0"
```

## Introduction

   It provides a set of implicit conversions to convert [[Task]] to [[Future]], so the
   user does not need to do it for every test.
  
  ```scala
   import monix.eval.Task
   import monix.execution.Scheduler
   import org.scalatest.funsuite.AsyncFunSuite
   import org.scalatest.matchers.should.Matchers
  
   class DummySpec extends AsyncFunSuite with MonixTaskSpec with Matchers {
  
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

## Credits

The code has been ported from [cats-effect-testing](https://github.com/typelevel/cats-effect-testing) and [fs2 effect test support](https://github.com/functional-streams-for-scala/fs2/blob/188a37883d7bbdf22bc4235a3a1223b14dc10b6c/core/shared/src/test/scala/fs2/EffectTestSupport.scala), with the difference that this library provides support for `Task` instead of `IO`.
      
