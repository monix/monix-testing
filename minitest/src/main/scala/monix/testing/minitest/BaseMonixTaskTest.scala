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

import scala.concurrent.ExecutionContext


private[minitest] abstract class BaseMonixTaskTest[Ec <: ExecutionContext] extends AbstractTestSuite with Asserts {
  protected def makeExecutionContext(): Ec

  private[minitest] lazy val executionContext: Ec = makeExecutionContext()
  protected[minitest] implicit def suiteEc: ExecutionContext = executionContext

  protected[minitest] def mkSpec(name: String, ec: Ec, task: => Task[Unit]): TestSpec[Unit, Unit]

  def test(name: String)(f: => Task[Unit]): Unit =
    synchronized {
      if (isInitialized) throw new AssertionError("Cannot define new tests after TestSuite was initialized")
      propertiesSeq :+= mkSpec(name, executionContext, f)
    }

  lazy val properties: Properties[_] =
    synchronized {
      if (!isInitialized) isInitialized = true
      Properties[Unit](() => (), _ => Void.UnitRef, () => (), () => (), propertiesSeq)
    }

  private[this] var propertiesSeq = Vector.empty[TestSpec[Unit, Unit]]
  private[this] var isInitialized = false
}
