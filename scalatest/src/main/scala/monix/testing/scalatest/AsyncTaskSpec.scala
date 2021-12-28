package monix.testing.scalatest

import scala.language.implicitConversions

import cats.effect.testing.scalatest.AssertingSyntax
import org.scalatest.AsyncTestSuite
import org.scalatest.compatible.Assertion
import scala.concurrent.Future
import org.scalactic.source.Position
import org.scalatest.time.Span
import org.scalatest.enablers.Retrying
import monix.eval.Task
import monix.execution.Scheduler
import org.scalatest.Succeeded

/**
  * The code has been copied from `cats-effect-testing` and `fs2` effect test support,
  * with the difference it provides support for [[Task]] instead of [[IO]].
  *
  * @see https://github.com/typelevel/cats-effect-testing
  *      https://github.com/typelevel/cats-effect-testing/blob/series/1.x/scalatest/shared/src/main/scala/cats/effect/testing/scalatest/AsyncIOSpec.scala
  *      https://github.com/functional-streams-for-scala/fs2/blob/188a37883d7bbdf22bc4235a3a1223b14dc10b6c/core/shared/src/test/scala/fs2/EffectTestSupport.scala
  *
  * Provides support for testing Monix [[Task]] with scalatest [[AsyncTestSuite]].
  *
  * It provides a set of implicit conversions to convert [[Task]] to [[Future]], so the
  * user does not need to do it for every test.
  *
  * ==Example==
  *
  * {{{
  * import monix.eval.Task
  * import monix.execution.Scheduler
  * import org.scalatest.funsuite.AsyncFunSuite
  * import org.scalatest.matchers.should.Matchers
  *
  * class DummySpec extends AsyncFunSuite with MonixTaskSpec with Matchers {
  *
  *     override implicit def scheduler: Scheduler = Scheduler.io("monix-task-support-spec")
  *
  *     test("AsyncTestSuite with Task support") {
  *         for {
  *             r1 <- Task(2)
  *             r2 <- Task(r1 * 3)
  *         } yield {
  *             r1 shouldBe 2
  *             r2 shouldBe 6
  *         }
  *     // we do not have to append `.runToFuture` as we would would using a plain [[AsyncFunSuit]].
  *     }
  *
  *     test("AsyncTestSuite with Task and AssertingSyntax support") {
  *         Task(2).flatMap(r1 => Task(r1 * 3)).asserting(_ shouldBe 6)
  *     }
  * }}}
  */
trait AsyncTaskSpec extends AssertingSyntax {
  asyncTestSuite: AsyncTestSuite =>

  implicit def scheduler: Scheduler = Scheduler.global

  implicit def taskToFutureAssertion(task: Task[Assertion]): Future[Assertion] = task.runToFuture

  implicit def taskRetrying[T]: Retrying[Task[T]] = new Retrying[Task[T]] {
    override def retry(timeout: Span, interval: Span, pos: Position)(fun: => Task[T]): Task[T] =
      Task.fromFuture(
        Retrying.retryingNatureOfFutureT[T](executionContext).retry(timeout, interval, pos)(fun.runToFuture)
      )
  }

  implicit def taskUnitToFutureAssertion(task: Task[Unit]): Future[Assertion] =
    task.as(Succeeded).runToFuture
}
