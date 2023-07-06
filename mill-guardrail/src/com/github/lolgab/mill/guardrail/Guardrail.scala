package com.github.lolgab.mill.guardrail

import io.scalaland.chimney.dsl._
import mill._
import mill.api.Result
import mill.main.BuildInfo
import mill.scalalib._

trait Guardrail extends ScalaModule {
  def guardrailTasks: T[Seq[GuardrailTask]]

  override def generatedSources = T {
    super.generatedSources() ++ guardrail()
  }

  def guardrail: T[Seq[PathRef]] = T {
    val input: Map[String, Seq[worker.api.Args]] = guardrailTasks()
      .groupBy(_.language)
      .map { case (language, tasks) =>
        language.name -> tasks.map(
          _.args
            .into[worker.api.Args]
            .withFieldConst(_.outputPath, Some(T.dest.toString))
            .withFieldComputed(_.specPath, _.specPath.map(_.path.toString))
            .transform
        )
      }
      .toMap
    worker.GuardrailWorkerExternalModule
      .guardrailWorker()
      .run(guardrailWorkerClasspath(), input) match {
      case Right(result) =>
        Result.Success(
          result.map(nioPath => PathRef(os.Path(nioPath)))
        )
      case Left(error) =>
        Result.Failure(error)
    }
  }

  private def guardrailWorkerClasspath: T[Agg[PathRef]] = T {
    Lib
      .resolveDependencies(
        repositoriesTask(),
        Agg(
          ivy"com.github.lolgab:mill-guardrail-worker-impl_2.13:${worker.GuardrailBuildInfo.publishVersion}"
            .exclude("com.github.lolgab" -> "mill-guardrail-worker-api_2.13")
            .exclude("io.scalaland" -> "chimney_2.13")
        ).map(Lib.depToBoundDep(_, BuildInfo.scalaVersion)),
        ctx = Some(T.log)
      )
  }
}
