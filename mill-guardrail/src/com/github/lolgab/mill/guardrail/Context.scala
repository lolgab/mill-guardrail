package com.github.lolgab.mill.guardrail

import upickle.default._

case class Context(
    framework: Option[String],
    customExtraction: Boolean,
    tracing: Boolean,
    modules: List[String],
    propertyRequirement: PropertyRequirement.Configured,
    tagsBehaviour: Context.TagsBehaviour,
    authImplementation: AuthImplementation
)

object Context {
  sealed trait TagsBehaviour
  case object PackageFromTags extends TagsBehaviour
  case object TagsAreIgnored extends TagsBehaviour

  val empty: Context = Context(
    None,
    customExtraction = false,
    tracing = false,
    modules = List.empty,
    propertyRequirement = PropertyRequirement.Configured(
      PropertyRequirement.OptionalLegacy,
      PropertyRequirement.OptionalLegacy
    ),
    tagsBehaviour = TagsAreIgnored,
    authImplementation = AuthImplementation.Disable
  )

  implicit val tagsAreIgnoredRW: ReadWriter[TagsAreIgnored.type] =
    macroRW[TagsAreIgnored.type]
  implicit val packageFromTagsRW: ReadWriter[PackageFromTags.type] =
    macroRW[PackageFromTags.type]
  implicit val tagsBehaviourRW: ReadWriter[TagsBehaviour] =
    macroRW[TagsBehaviour]
  implicit val rw: ReadWriter[Context] = macroRW[Context]
}
