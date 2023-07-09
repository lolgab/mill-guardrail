package com.github.lolgab.mill.guardrail.worker

import cats.data.NonEmptyList
import com.github.lolgab.mill.guardrail.worker.api._
import dev.guardrail
import io.scalaland.chimney.dsl._

import scala.io.AnsiColor

class GuardrailWorkerImpl extends GuardrailWorkerApi {
  def run(
      input: Array[RunInputEntry]
  ): Array[java.nio.file.Path] = {
    val guardrailInput =
      input.map { case RunInputEntry(language, args) =>
        val guardrailArgs =
          args
            .map(
              _.into[dev.guardrail.Args]
                .withFieldComputed(
                  _.packageName,
                  args => Option(args.packageName).map(_.toList)
                )
                .transform
            )
            .toList
        language -> NonEmptyList
          .fromList(guardrailArgs)
          .getOrElse(
            throw new GuardrailError(
              "You need to provide at least one guardrailTask"
            )
          )
      }.toMap
    Runner.guardrailRunner.apply(guardrailInput) match {
      case value: guardrail.TargetValue[_] => value.value.toArray
      case error: guardrail.TargetError[_] =>
        error.error match {
          case guardrail.MissingArg(args, guardrail.Error.ArgName(arg)) =>
            throw new GuardrailError(
              s"Missing argument: ${arg} (In block ${args})"
            )
          case guardrail.MissingDependency(name) =>
            throw new GuardrailError(
              s"""Missing dependency: override def ivyDeps = super.ivyDeps() ++ Agg("dev.guardrail" %% "${name}" % "<check latest version>")"""
            )
          case guardrail.NoArgsSpecified =>
            Array.empty[java.nio.file.Path]
          case guardrail.NoFramework =>
            throw new GuardrailError("No framework specified")
          case guardrail.PrintHelp =>
            Array.empty[java.nio.file.Path]
          case guardrail.UnknownArguments(args) =>
            throw new GuardrailError(
              s"Unknown arguments: ${args.mkString(" ")}"
            )
          case guardrail.UnparseableArgument(name, message) =>
            throw new GuardrailError(
              s"Unparseable argument ${name}: ${message}"
            )
          case guardrail.UnknownFramework(name) =>
            throw new GuardrailError(s"Unknown framework specified: ${name}")
          case guardrail.RuntimeFailure(message) =>
            throw new GuardrailError(s"Error: ${message}")
          case guardrail.UserError(message) =>
            throw new GuardrailError(s"Error: ${message}")
          case guardrail.MissingModule(section, choices) =>
            throw new GuardrailError(
              s"Error: Missing module ${section}. Options are: ${choices
                  .mkString(", ")}"
            )
          case guardrail.ModuleConflict(section) =>
            throw new GuardrailError(
              s"Error: Too many modules specified for ${section}"
            )
          case guardrail.UnspecifiedModules(choices) =>
            val result =
              choices.toSeq
                .sortBy(_._1)
                .foldLeft(Seq.empty[String]) { case (acc, (module, choices)) =>
                  val nextLabel = Option(choices)
                    .filter(_.nonEmpty)
                    .fold("<no choices found>")(_.toSeq.sorted.mkString(", "))
                  acc :+ s"  ${AnsiColor.WHITE}${module}: [${AnsiColor.BLUE}${nextLabel}]"
                }
                .mkString("\n")
            throw new GuardrailError(s"Unsatisfied module(s):\n$result")
          case guardrail.UnusedModules(unused) =>
            throw new GuardrailError(
              s"Unused modules specified: ${unused.toList.mkString(", ")}"
            )
        }
    }
  }
}

object Runner extends dev.guardrail.runner.GuardrailRunner
