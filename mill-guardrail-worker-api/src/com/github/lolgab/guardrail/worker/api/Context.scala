package com.github.lolgab.mill.guardrail.worker.api

private[guardrail] case class Context(
    framework: String,
    customExtraction: Boolean,
    tracing: Boolean,
    modules: Array[String],
    propertyRequirement: PropertyRequirement.Configured,
    tagsBehaviour: Context.TagsBehaviour,
    authImplementation: AuthImplementation
)

private[guardrail] object Context {
  sealed trait TagsBehaviour
  case object PackageFromTags extends TagsBehaviour
  case object TagsAreIgnored extends TagsBehaviour
}
