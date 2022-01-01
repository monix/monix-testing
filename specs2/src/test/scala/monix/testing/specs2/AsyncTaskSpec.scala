package monix.testing.specs2

import cats.effect.{IO, Resource}
import cats.effect.concurrent.{Deferred, Ref}
import org.specs2.mutable.Specification
import cats.effect.testing.specs2.CatsEffect
import monix.eval.Task
import monix.execution.Scheduler

// File copied from `cats-effect-testing` specs2 module.
class CatsEffectSpecs extends Specification with CatsEffect {

  implicit def scheduler: Scheduler = Scheduler.global

  "cats effect specifications" should {
    "run a non-effectful test" in {
      true must beTrue
    }

    "run a simple effectful test" in Task {
      true must beTrue
      false must beFalse
    }

    "run a simple resource test" in {
      true must beTrue
    }

    "resource must be live for use" in {
      Resource.make(Ref[Task].of(true))(_.set(false)).map {
        _.get.map(_ must beTrue)
      }
    }

    "really execute effects" in {
      "First, this check creates a deferred value.".br

      val deferredValue = Deferred.unsafeUncancelable[IO, Boolean]

      "Then it executes two mutually associated steps:".br.tab

      "forcibly attempt to get the deferred value" in {
        deferredValue.get.unsafeRunTimed(Timeout) must beSome(true)
      }

      "Since specs2 executes steps in parallel by default, the second step gets executed anyway.".br

      "complete the deferred value inside IO context" in {
        deferredValue.complete(true) *> IO.pure(success)
      }

      "If effects didn't get executed then the previous step would fail after timeout.".br
    }

    // "timeout a failing test" in (IO.never: IO[Boolean])
  }
}
