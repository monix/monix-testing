import monix.eval.Task
import monix.execution.Scheduler
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

class MonixTaskSupportSpec extends AsyncFunSuite with MonixTaskSpec with Matchers {

  override implicit def scheduler: Scheduler = Scheduler.io("monix-task-support-spec")

  test("AsyncTestSuite with Task support") {
    for {
      r1 <- Task(2)
      r2 <- Task(r1 * 3)
    } yield {
      r1 shouldBe 2
      r2 shouldBe 6
    }
    // we do not have to append `.runToFuture` as we would would using a plain [[AsyncFunSuit]].
  }

  test("AsyncTestSuite with Task and AssertingSyntax support") {
    Task(2).flatMap(r1 => Task(r1 * 3)).asserting(_ shouldBe 6)
  }

}
