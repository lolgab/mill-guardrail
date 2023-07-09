package com.github.lolgab.mill.guardrail.worker.api

private[guardrail] sealed trait AuthImplementation
private[guardrail] object AuthImplementation {
  case object Disable extends AuthImplementation
  case object Native extends AuthImplementation
  case object Simple extends AuthImplementation
  case object Custom extends AuthImplementation
}
