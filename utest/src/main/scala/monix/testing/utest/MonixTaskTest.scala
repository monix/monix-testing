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

package monix.testing.utest

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import utest._

abstract class MonixTaskTest extends TestSuite {

  protected def makeExecutionContext(): ExecutionContext = ExecutionContext.global
  protected def timeout: FiniteDuration = 10.seconds
  protected def allowNonIOTests: Boolean = false

  protected lazy val executionContext: ExecutionContext = makeExecutionContext()

  override def utestWrap(path: Seq[String], runBody: => Future[Any])(implicit ec: ExecutionContext): Future[Any] = {
    // Shadow the parameter EC with our EC
    implicit val ec: ExecutionContext = this.executionContext
    runBody.flatMap {
      case task: Task[Any] => task.timeout(timeout).runToFuture
      case other if allowNonIOTests => Future.successful(other)
      case other => throw new RuntimeException(s"Test body must return an Task value. Got $other")
    }
  }
}
