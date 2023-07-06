package com.github.lolgab.mill.guardrail.worker

import com.github.lolgab.mill.guardrail.worker.api.GuardrailWorkerApi
import mill.Agg
import mill.PathRef
import mill.T
import mill.define.Discover
import mill.define.Worker

private[guardrail] class GuardrailWorker {
  private var scalaInstanceCache =
    Option.empty[(Long, GuardrailWorkerApi, ClassLoader)]

  def run(
      guardrailWorkerClasspath: Agg[PathRef],
      input: Map[String, Seq[api.Args]]
  )(implicit
      ctx: mill.api.Ctx.Home
  ): Either[String, List[java.nio.file.Path]] = {
    val classloaderSig = guardrailWorkerClasspath.hashCode
    val (bridge, classloader) = scalaInstanceCache match {
      case Some((sig, bridge, classloader)) if sig == classloaderSig =>
        (bridge, classloader)
      case _ =>
        val cl = mill.api.ClassLoader.create(
          guardrailWorkerClasspath
            .map(_.path.toIO.toURI.toURL)
            .iterator
            .to(Seq),
          parent = getClass.getClassLoader,
          sharedLoader = getClass.getClassLoader,
          sharedPrefixes = Seq(
            "com.github.lolgab.mill.guardrail.worker.api.",
            "io.scalaland.chimney.",
            "scala.collection."
          )
        )
        try {
          val bridge = cl
            .loadClass(
              "com.github.lolgab.mill.guardrail.worker.GuardrailWorkerImpl"
            )
            .getDeclaredConstructor()
            .newInstance()
            .asInstanceOf[
              com.github.lolgab.mill.guardrail.worker.api.GuardrailWorkerApi
            ]
          scalaInstanceCache = Some((classloaderSig, bridge, cl))
          (bridge, cl)
        } catch {
          case e: Exception =>
            e.printStackTrace()
            throw e
        }
    }

    val currentThread = Thread.currentThread()
    val previousContextClassloader =
      Thread.currentThread().getContextClassLoader()
    try {
      currentThread.setContextClassLoader(classloader)
      bridge.run(input)
    } finally {
      currentThread.setContextClassLoader(previousContextClassloader)
    }
  }
}

private[guardrail] object GuardrailWorkerExternalModule
    extends mill.define.ExternalModule {
  def guardrailWorker: Worker[GuardrailWorker] = T.worker {
    new GuardrailWorker()
  }
  lazy val millDiscover = Discover[this.type]
}
