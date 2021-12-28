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

import minitest.api._
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

abstract class MonixTaskTest extends BaseMonixTaskTest[ExecutionContext] {
  protected def makeExecutionContext(): ExecutionContext = DefaultExecutionContext

  protected implicit val scheduler: Scheduler = Scheduler.global
  protected def timeout: FiniteDuration = 10.seconds

  protected[minitest] def mkSpec(name: String, ec: ExecutionContext, task: => Task[Unit]): TestSpec[Unit, Unit] =
    TestSpec.async[Unit](name, _ => task.timeout(timeout).runToFuture)

}
