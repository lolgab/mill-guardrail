package com.github.lolgab.mill.guardrail.worker.api

case class Args(
    kind: CodegenTarget,
    specPath: Option[String],
    outputPath: Option[String],
    packageName: Option[List[String]],
    dtoPackage: List[String],
    printHelp: Boolean,
    context: Context,
    defaults: Boolean,
    imports: List[String]
)
