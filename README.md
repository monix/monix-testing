# Monix Testing

Library aimed to provide support to integrate Monix Task with different testing frameworks.
At the moment only for [scalatest async test suites](https://www.scalatest.org/user_guide/async_testing). 

Add the following dependency to your *build.sbt*:

```sbt
"io.monix" %% "monix-testing-scalatest" % "0.1.0"
```

## Credits

The code has been ported from [cats-effect-testing](https://github.com/typelevel/cats-effect-testing) and [fs2 effect test support](https://github.com/functional-streams-for-scala/fs2/blob/188a37883d7bbdf22bc4235a3a1223b14dc10b6c/core/shared/src/test/scala/fs2/EffectTestSupport.scala), with the difference that this library provides support for `Task` instead of `IO`.
      