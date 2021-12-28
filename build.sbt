
inThisBuild(List(
  organization := "io.monix",
  homepage := Some(url("https://connect.monix.io")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "paualarco",
      "Pau Alarcón Cerdan",
      "pau.alarcon.b@gmail.com",
      url("https://connect.monix.io")
    )
  )
))

skip in publish := true //required by sbt-ci-release

lazy val sharedSettings = Seq(
  scalaVersion       := "2.13.5",
  crossScalaVersions := Seq("2.12.14", "2.13.5"),
  scalafmtOnCompile  := false,
  scalacOptions ++= Seq(
    // warnings
    "-unchecked", // able additional warnings where generated code depends on assumptions
    "-deprecation", // emit warning for usages of deprecated APIs
    "-feature", // emit warning usages of features that should be imported explicitly
    // Features enabled by default
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:experimental.macros"
  ),
  //warnUnusedImports
  scalacOptions in (Compile, console) ++= Seq("-Ywarn-unused:imports"),
    // Linter
  scalacOptions ++= Seq(
    "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
    "-Ywarn-dead-code", // Warn when dead code is identified.
    // Turns all warnings into errors ;-)
    //temporary disabled for mongodb warn, -YWarn (2.13) and Silencer (2.12) should fix it...
    //"-Xfatal-warnings", //Turning of fatal warnings for the moment
    // Enables linter options
    "-Xlint:adapted-args", // warn if an argument list is modified to match the receiver
    "-Xlint:infer-any", // warn when a type argument is inferred to be `Any`
    "-Xlint:missing-interpolator", // a string literal appears to be missing an interpolator id
    "-Xlint:doc-detached", // a ScalaDoc comment appears to be detached from its element
    "-Xlint:private-shadow", // a private field (or class parameter) shadows a superclass field
    "-Xlint:type-parameter-shadow", // a local type parameter shadows a type already in scope
    "-Xlint:poly-implicit-overload", // parameterized overloaded implicit methods are not visible as view bounds
    "-Xlint:option-implicit", // Option.apply used implicit view
    "-Xlint:delayedinit-select", // Selecting member of DelayedInit
    //"-Xlint:package-object-classes" // Class or object defined in package object
  ),

  // ScalaDoc settings
  scalacOptions in (Compile, doc) ++= Seq("-no-link-warnings"),
  autoAPIMappings := true,
  scalacOptions in ThisBuild ++= Seq(
    // Note, this is used by the doc-source-url feature to determine the
    // relative path of a given source file. If it's not a prefix of a the
    // absolute path of the source file, the absolute path of that file
    // will be put into the FILE_SOURCE variable, which is
    // definitely not what we want.
    "-sourcepath",
    file(".").getAbsolutePath.replaceAll("[.]$", "")
  ),
  parallelExecution in Test             := false,
  parallelExecution in IntegrationTest  := false,
  parallelExecution in ThisBuild        := false,
  testForkedParallel in Test            := false,
  testForkedParallel in IntegrationTest := false,
  testForkedParallel in ThisBuild       := false,
  concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
  logBuffered in Test            := false,
  logBuffered in IntegrationTest := false,
  // https://github.com/sbt/sbt/issues/2654
  incOptions := incOptions.value.withLogRecompileOnMacro(false),
  pomIncludeRepository    := { _ => false }, // removes optional dependencies

  // ScalaDoc settings
  autoAPIMappings := true,
  apiURL := Some(url("https://monix.github.io/monix-connect/api/")),

  headerLicense := Some(HeaderLicense.Custom(
    """|Copyright (c) 2020-2021 by The Monix Connect Project Developers.
       |See the project homepage at: https://connect.monix.io
       |
       |Licensed under the Apache License, Version 2.0 (the "License");
       |you may not use this file except in compliance with the License.
       |You may obtain a copy of the License at
       |
       |    http://www.apache.org/licenses/LICENSE-2.0
       |
       |Unless required by applicable law or agreed to in writing, software
       |distributed under the License is distributed on an "AS IS" BASIS,
       |WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       |See the License for the specific language governing permissions and
       |limitations under the License."""
      .stripMargin)),
)

lazy val skipOnPublishSettings = Seq(
  skip in publish := true,
  publishArtifact := false,
)
//=> published modules
lazy val monixTesting = (project in file("."))
  .settings(sharedSettings)
  .settings(name := "monix-testing")
  .aggregate(scalatest, specs2, minitest, utest)
  .dependsOn(scalatest, specs2, minitest, utest)

val Monix = "3.4.0"
val CatsEffectTesting = "0.5.4"
val Scalatest = "3.2.9"
val Specs2 = "4.13.1"

val scalatestDeps = Seq(
  "io.monix" %% "monix-eval" % Monix,
  "com.codecommit" %% "cats-effect-testing-scalatest" % CatsEffectTesting,
  "org.scalatest" %% "scalatest"   % Scalatest)

val specs2Deps = Seq(
  "io.monix" %% "monix-eval" % Monix,
  "org.specs2" %% "specs2-core" % Specs2,
  "com.codecommit" %% "cats-effect-testing-utest" % CatsEffectTesting)

lazy val scalatest = testFramework("scalatest", scalatestDeps)

lazy val specs2 = testFramework("specs2", specs2Deps)
  .settings(skipOnPublishSettings)

def testFramework(
  testFrameworkName: String,
  projectDependencies: Seq[ModuleID]): Project = {
  Project(id = testFrameworkName, base = file(testFrameworkName))
    .settings(name := s"monix-testing-$testFrameworkName", libraryDependencies ++= projectDependencies)
    .settings(sharedSettings)
}

lazy val skipOnPublishSettings = Seq(
  skip in publish := true,
  publishArtifact := false,
)

def minorVersion(version: String): String = {
  val (major, minor) =
    CrossVersion.partialVersion(version).get
  s"$major.$minor"
}
