package com.github.lolgab.mill.guardrail

import mill._

sealed trait ClientServer {
  val kind: CodegenTarget
  val language: Language

  private[this] def impl(
      kind: CodegenTarget,
      specPath: PathRef,
      packageName: Option[String],
      dtoPackage: Option[String],
      framework: Option[String],
      tracing: Option[Boolean],
      modules: List[String],
      defaults: Boolean,
      imports: List[String],
      encodeOptionalAs: Option[PropertyRequirement.OptionalRequirement],
      decodeOptionalAs: Option[PropertyRequirement.OptionalRequirement],
      customExtraction: Option[Boolean],
      tagsBehaviour: Option[Context.TagsBehaviour],
      authImplementation: Option[AuthImplementation]
  ): GuardrailTask.Args = {
    val propertyRequirement = (encodeOptionalAs, decodeOptionalAs) match {
      case (None, None) => Context.empty.propertyRequirement
      case (encoder, decoder) =>
        val fallback = Context.empty.propertyRequirement
        PropertyRequirement.Configured(
          encoder.getOrElse(fallback.encoder),
          decoder.getOrElse(fallback.decoder)
        )
    }

    def kindaLens[A](
        member: Option[A]
    )(proj: A => Context => Context): Context => Context =
      member.fold[Context => Context](identity _)(proj)

    val contextTransforms = Seq[Context => Context](
      kindaLens(authImplementation)(a => _.copy(authImplementation = a)),
      kindaLens(customExtraction)(a => _.copy(customExtraction = a)),
      kindaLens(tagsBehaviour)(a => _.copy(tagsBehaviour = a)),
      kindaLens(tracing)(a => _.copy(tracing = a))
    )

    GuardrailTask.Args.empty.copy(
      defaults = defaults,
      kind = kind,
      specPath = specPath,
      packageName = packageName.map(_.split('.').toList),
      dtoPackage =
        dtoPackage.toList.flatMap(_.split('.').filterNot(_.isEmpty).toList),
      imports = imports,
      context = contextTransforms.foldLeft(
        Context.empty.copy(
          framework = framework,
          modules = modules,
          propertyRequirement = propertyRequirement
        )
      )({ case (acc, next) => next(acc) })
    )
  }

  def apply(
      specPath: PathRef,
      pkg: String = "swagger",
      dto: Option[String] = None,
      framework: Option[String] = None,
      tracing: Option[Boolean] = None,
      modules: Option[List[String]] = None,
      imports: Option[List[String]] = None,
      encodeOptionalAs: Option[PropertyRequirement.OptionalRequirement] = None,
      decodeOptionalAs: Option[PropertyRequirement.OptionalRequirement] = None,
      customExtraction: Option[Boolean] = None,
      tagsBehaviour: Option[Context.TagsBehaviour] = None,
      authImplementation: Option[AuthImplementation] = None
  ): GuardrailTask = GuardrailTask(
    language,
    impl(
      kind = kind,
      specPath = specPath,
      packageName = Some(pkg),
      dtoPackage = dto,
      framework = framework,
      tracing = tracing,
      modules = modules.getOrElse(List.empty),
      imports = imports.getOrElse(List.empty),
      encodeOptionalAs = encodeOptionalAs,
      decodeOptionalAs = decodeOptionalAs,
      customExtraction = customExtraction,
      tagsBehaviour = tagsBehaviour,
      authImplementation = authImplementation,
      defaults = false
    )
  )
}
object ScalaClient extends ClientServer {
  val kind = CodegenTarget.Client
  val language = Language.Scala
}

object ScalaModels extends ClientServer {
  val kind = CodegenTarget.Models
  val language = Language.Scala
}

object ScalaServer extends ClientServer {
  val kind = CodegenTarget.Server
  val language = Language.Scala
}

object JavaClient extends ClientServer {
  val kind = CodegenTarget.Client
  val language = Language.Java
}

object JavaModels extends ClientServer {
  val kind = CodegenTarget.Models
  val language = Language.Java
}

object JavaServer extends ClientServer {
  val kind = CodegenTarget.Server
  val language = Language.Java
}
