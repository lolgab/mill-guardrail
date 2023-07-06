package com.github.lolgab.mill.guardrail.worker.api

sealed trait CodegenTarget
object CodegenTarget {
  case object Client extends CodegenTarget
  case object Server extends CodegenTarget
  case object Models extends CodegenTarget
}
