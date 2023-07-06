package com.github.lolgab.mill.guardrail

import upickle.default._

sealed trait PropertyRequirement

/** Types that are represented as Option[T]
  */
object PropertyRequirement {
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

  implicit val requiredNullableRW: ReadWriter[RequiredNullable.type] =
    macroRW[RequiredNullable.type]
  implicit val optionalLegacyRW: ReadWriter[OptionalLegacy.type] =
    macroRW[OptionalLegacy.type]
  implicit val optionalRW: ReadWriter[Optional.type] =
    macroRW[Optional.type]
  implicit val optionalRequirementRW: ReadWriter[OptionalRequirement] =
    macroRW[OptionalRequirement]
  implicit val rw: ReadWriter[Configured] =
    macroRW[Configured]

}
