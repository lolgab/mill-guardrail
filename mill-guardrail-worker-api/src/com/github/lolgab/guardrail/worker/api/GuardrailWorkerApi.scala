package com.github.lolgab.mill.guardrail.worker.api

private[guardrail] trait GuardrailWorkerApi {
  def run(input: Array[RunInputEntry]): Array[java.nio.file.Path]
}

private[guardrail] case class RunInputEntry(language: String, args: Array[Args])
private[guardrail] case class GuardrailError(message: String)
    extends Exception(message)
