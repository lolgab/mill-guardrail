package com.github.lolgab.mill.guardrail.worker

import cats.data.NonEmptyList
import com.github.lolgab.mill.guardrail.worker.api._
import dev.guardrail
import io.scalaland.chimney.dsl._

import scala.io.AnsiColor

class GuardrailWorkerImpl extends GuardrailWorkerApi {
  def run(
      input: Map[String, Seq[Args]]
  ): Either[String, List[java.nio.file.Path]] = {
    val guardrailInput =
      input.view.mapValues { seq =>
        val guardrailSeq = seq.map(_.transformInto[dev.guardrail.Args]).toList
        NonEmptyList
          .fromList(guardrailSeq)
          .getOrElse(
            throw new Exception(
              "You need to provide at least one guardrailTask"
            )
          )
      }.toMap
    Runner.guardrailRunner.apply(guardrailInput) match {
      case value: guardrail.TargetValue[_] => Right(value.value)
      case error: guardrail.TargetError[_] =>
        error.error match {
          case guardrail.MissingArg(args, guardrail.Error.ArgName(arg)) =>
            Left(
              s"Missing argument: ${arg} (In block ${args})"
            )
          case guardrail.MissingDependency(name) =>
            Left(
              s"""Missing dependency: override def ivyDeps = super.ivyDeps() ++ Agg("dev.guardrail" %% "${name}" % "<check latest version>")"""
            )
          case guardrail.NoArgsSpecified =>
            Right(List.empty)
          case guardrail.NoFramework =>
            Left("No framework specified")
          case guardrail.PrintHelp =>
            Right(List.empty)
          case guardrail.UnknownArguments(args) =>
            Left(s"Unknown arguments: ${args.mkString(" ")}")
          case guardrail.UnparseableArgument(name, message) =>
            Left(s"Unparseable argument ${name}: ${message}")
          case guardrail.UnknownFramework(name) =>
            Left(s"Unknown framework specified: ${name}")
          case guardrail.RuntimeFailure(message) =>
            Left(s"Error: ${message}")
          case guardrail.UserError(message) =>
            Left(s"Error: ${message}")
          case guardrail.MissingModule(section, choices) =>
            Left(
              s"Error: Missing module ${section}. Options are: ${choices
                  .mkString(", ")}"
            )
          case guardrail.ModuleConflict(section) =>
            Left(
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
            Left(s"Unsatisfied module(s):\n$result")
          case guardrail.UnusedModules(unused) =>
            Left(
              s"Unused modules specified: ${unused.toList.mkString(", ")}"
            )
        }
    }
  }
}

object Runner extends dev.guardrail.runner.GuardrailRunner
