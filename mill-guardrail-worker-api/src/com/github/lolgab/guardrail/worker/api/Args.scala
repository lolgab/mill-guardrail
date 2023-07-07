package com.github.lolgab.mill.guardrail.worker.api

case class Args(
    kind: CodegenTarget,
    specPath: String,
    outputPath: String,
    packageName: Array[String],
    dtoPackage: Array[String],
    printHelp: Boolean,
    context: Context,
    defaults: Boolean,
    imports: Array[String]
)
