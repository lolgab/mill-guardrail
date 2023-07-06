package com.github.lolgab.mill.guardrail

import upickle.default._

sealed trait CodegenTarget
object CodegenTarget {
  case object Client extends CodegenTarget
  case object Server extends CodegenTarget
  case object Models extends CodegenTarget

  implicit val serverRW: ReadWriter[Server.type] = macroRW[Server.type]
  implicit val modelsRW: ReadWriter[Models.type] = macroRW[Models.type]
  implicit val clientRW: ReadWriter[Client.type] = macroRW[Client.type]
  implicit val rw: ReadWriter[CodegenTarget] = macroRW[CodegenTarget]
}
