package com.github.lolgab.mill.guardrail

import upickle.default._

case class GuardrailTask(language: Language, args: GuardrailTask.Args)
object GuardrailTask {
  case class Args(
      kind: CodegenTarget,
      specPath: Option[mill.PathRef],
      packageName: Option[List[String]],
      dtoPackage: List[String],
      printHelp: Boolean,
      context: Context,
      defaults: Boolean,
      imports: List[String]
  ) { self =>
    def copyContext(
        framework: Option[String] = self.context.framework,
        customExtraction: Boolean = self.context.customExtraction,
        tracing: Boolean = self.context.tracing,
        modules: List[String] = self.context.modules,
        propertyRequirement: PropertyRequirement.Configured =
          self.context.propertyRequirement,
        tagsBehaviour: Context.TagsBehaviour = self.context.tagsBehaviour,
        authImplementation: AuthImplementation = self.context.authImplementation
    ): GuardrailTask.Args =
      self.copy(
        context = self.context.copy(
          framework = framework,
          customExtraction = customExtraction,
          tracing = tracing,
          modules = modules,
          propertyRequirement = propertyRequirement,
          tagsBehaviour = tagsBehaviour,
          authImplementation = authImplementation
        )
      )

    def copyPropertyRequirement(
        encoder: PropertyRequirement.OptionalRequirement =
          self.context.propertyRequirement.encoder,
        decoder: PropertyRequirement.OptionalRequirement =
          self.context.propertyRequirement.decoder
    ): GuardrailTask.Args =
      copyContext(
        propertyRequirement = self.context.propertyRequirement.copy(
          encoder = encoder,
          decoder = decoder
        )
      )
  }

  object Args {
    val empty: Args = Args(
      CodegenTarget.Client,
      Option.empty,
      Option.empty,
      List.empty,
      false,
      Context.empty,
      false,
      List.empty
    )
    def isEmpty: Args => Boolean = { args =>
      args.specPath.isEmpty &&
      args.packageName.isEmpty &&
      !args.printHelp
    }

    implicit val rw: ReadWriter[Args] = macroRW[Args]
  }

  implicit val rw: ReadWriter[GuardrailTask] = macroRW[GuardrailTask]
}
