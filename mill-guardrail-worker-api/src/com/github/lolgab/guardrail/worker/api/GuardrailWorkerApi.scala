package com.github.lolgab.mill.guardrail.worker.api

trait GuardrailWorkerApi {
  def run(input: Array[RunInputEntry]): Array[java.nio.file.Path]
}

case class RunInputEntry(language: String, args: Array[Args])
case class GuardrailError(message: String) extends Exception(message)
