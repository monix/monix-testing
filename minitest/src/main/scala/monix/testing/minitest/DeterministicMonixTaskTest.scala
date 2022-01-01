/*
 * Copyright 2021 Daniel Spiewak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package monix.testing.minitest

import minitest.api.{DefaultExecutionContext, TestSpec}
import monix.eval.Task
import monix.execution.schedulers.TestScheduler

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

abstract class DeterministicMonixTaskTest extends BaseMonixTaskTest[TestScheduler] {

  override protected final def makeExecutionContext(): TestScheduler = TestScheduler()

  override protected[minitest] implicit def suiteEc: ExecutionContext = DefaultExecutionContext

  override protected[minitest] def mkSpec(name: String, ec: TestScheduler, task: => Task[Unit]): TestSpec[Unit, Unit] =
    TestSpec.sync(
      name,
      _ => {
        val f = task.runToFuture(ec)
        ec.tick(365.days)
        f.value match {
          case Some(value) => value.get
          case None =>
            throw new RuntimeException(
              s"The Task in ${this.getClass.getName}.$name did not terminate.\n" +
                "Consider using a different Scheduler."
            )
        }
      }
    )
}
