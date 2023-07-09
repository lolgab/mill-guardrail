package com.github.lolgab.mill.guardrail.worker.api

private[guardrail] sealed trait PropertyRequirement

private[guardrail] object PropertyRequirement {
  case object Required extends PropertyRequirement
  case object OptionalNullable extends PropertyRequirement

  sealed trait OptionalRequirement extends PropertyRequirement

  case object RequiredNullable extends OptionalRequirement
  case object Optional extends OptionalRequirement
  case object OptionalLegacy extends OptionalRequirement

  final case class Configured(
      encoder: OptionalRequirement,
      decoder: OptionalRequirement
  ) extends PropertyRequirement
}
