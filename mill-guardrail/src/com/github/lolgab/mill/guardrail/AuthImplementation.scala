package com.github.lolgab.mill.guardrail

import upickle.default._

sealed trait AuthImplementation
object AuthImplementation {
  case object Disable extends AuthImplementation
  case object Native extends AuthImplementation
  case object Simple extends AuthImplementation
  case object Custom extends AuthImplementation

  implicit val simpleRW: ReadWriter[Simple.type] = macroRW[Simple.type]
  implicit val nativeRW: ReadWriter[Native.type] = macroRW[Native.type]
  implicit val disableRW: ReadWriter[Disable.type] = macroRW[Disable.type]
  implicit val customRW: ReadWriter[Custom.type] = macroRW[Custom.type]
  implicit val rw: ReadWriter[AuthImplementation] = macroRW[AuthImplementation]
}
