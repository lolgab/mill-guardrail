package com.github.lolgab.mill.guardrail.worker.api

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
}
