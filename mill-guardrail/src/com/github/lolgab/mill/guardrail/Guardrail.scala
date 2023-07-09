package com.github.lolgab.mill.guardrail

import com.github.lolgab.mill.guardrail.worker.api.GuardrailError
import com.github.lolgab.mill.guardrail.worker.api.RunInputEntry
import io.scalaland.chimney.dsl._
import mill._
import mill.api.Result
import mill.main.BuildInfo
import mill.scalalib._

import scala.util.control.NonFatal

trait Guardrail extends ScalaModule {
  def guardrailTasks: T[Seq[GuardrailTask]]

  override def generatedSources = T {
    super.generatedSources() ++ guardrail()
  }

  def guardrail: T[Seq[PathRef]] = T {
    val input: Array[RunInputEntry] = guardrailTasks()
      .groupBy(_.language)
      .map { case (language, tasks) =>
        RunInputEntry(
          language = language.name,
          args = tasks
            .map(
              _.args
                .into[worker.api.Args]
                .withFieldConst(_.outputPath, T.dest.toString)
                .withFieldComputed(
                  _.specPath,
                  _.specPath.map(_.path.toString).orNull
                )
                .withFieldComputed(
                  _.packageName,
                  _.packageName.map(_.toArray).orNull
                )
                .withFieldComputed(
                  _.context,
                  _.context
                    .into[worker.api.Context]
                    .withFieldComputed(_.framework, _.framework.orNull)
                    .transform
                )
                .transform
            )
            .toArray
        )
      }
      .toArray

    try {
      val result = worker.GuardrailWorkerExternalModule
        .guardrailWorker()
        .run(guardrailWorkerClasspath(), input)

      Result.Success(
        result.map(nioPath => PathRef(os.Path(nioPath))).toSeq
      )
    } catch {
      case GuardrailError(message) => Result.Failure(message)
      case NonFatal(t) =>
        Result.Exception(
          t,
          new Result.OuterStack(
            new java.lang.Exception().getStackTrace().toIndexedSeq
          )
        )
    }
  }

  private def guardrailWorkerClasspath: T[Agg[PathRef]] = T {
    Lib
      .resolveDependencies(
        repositoriesTask(),
        Agg(
          ivy"com.github.lolgab:mill-guardrail-worker-impl_2.13:${worker.GuardrailBuildInfo.publishVersion}"
            .exclude("com.github.lolgab" -> "mill-guardrail-worker-api_2.13")
        ).map(Lib.depToBoundDep(_, BuildInfo.scalaVersion)),
        ctx = Some(T.log)
      )
  }
}
