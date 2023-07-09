package com.github.lolgab.mill.guardrail.worker.api

private[guardrail] sealed trait CodegenTarget
private[guardrail] object CodegenTarget {
  case object Client extends CodegenTarget
  case object Server extends CodegenTarget
  case object Models extends CodegenTarget
}
