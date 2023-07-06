package com.github.lolgab.mill.guardrail.worker.api

trait GuardrailWorkerApi {
  def run(
      input: Map[String, Seq[Args]]
  ): Either[String, List[java.nio.file.Path]]
}
