import $file.utils

import mill._
import mill.scalalib._
import mill.scalalib.api.ZincWorkerUtil.scalaNativeBinaryVersion
import mill.scalalib.publish._
import $ivy.`com.lihaoyi::mill-contrib-buildinfo:`
import mill.contrib.buildinfo.BuildInfo
import $ivy.`de.tototec::de.tobiasroeser.mill.integrationtest::0.7.1`
import de.tobiasroeser.mill.integrationtest._
import $ivy.`com.goyeau::mill-scalafix::0.3.1`
import com.goyeau.mill.scalafix.ScalafixModule
import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.4.0`
import de.tobiasroeser.mill.vcs.version.VcsVersion
import os.Path
import scala.util.Try

val chimney = ivy"io.scalaland::chimney::0.7.5"

val millVersions = Seq("0.11.1")
val itestMillVersions = Seq("0.11.1")
val millBinaryVersions = millVersions.map(millBinaryVersion)

def millBinaryVersion(millVersion: String) =
  scalaNativeBinaryVersion(millVersion)

def millVersion(binaryVersion: String) =
  millVersions.find(v => millBinaryVersion(v) == binaryVersion).get

trait Common extends ScalaModule with PublishModule with ScalafixModule {
  def pomSettings = PomSettings(
    description = "Guardrail Mill Plugin",
    organization = "com.github.lolgab",
    url = "https://github.com/lolgab/mill-guardrail",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github("lolgab", "mill-guardrail"),
    developers = Seq(
      Developer("lolgab", "Lorenzo Gabriele", "https://github.com/lolgab")
    )
  )
  def publishVersion = VcsVersion.vcsState().format()
  def scalaVersion = "2.13.11"

  def scalacOptions =
    super.scalacOptions() ++ Seq("-Ywarn-unused", "-deprecation")

  def scalafixIvyDeps = Agg(ivy"com.github.liancheng::organize-imports:0.6.0")
}

object `mill-guardrail` extends Cross[MillGuardrailCross](millBinaryVersions)
trait MillGuardrailCross
    extends Common
    with BuildInfo
    with Cross.Module[String] {
  def millBinaryVersion = crossValue
  override def moduleDeps = super.moduleDeps ++ Seq(`mill-guardrail-worker-api`)
  override def artifactName = s"mill-guardrail_mill$millBinaryVersion"
  override def sources = T.sources(
    super.sources() ++ Seq(
      millSourcePath / s"src-mill${millVersion(millBinaryVersion).split('.').take(2).mkString(".")}"
    )
      .map(PathRef(_))
  )
  override def compileIvyDeps = super.compileIvyDeps() ++ Agg(
    ivy"com.lihaoyi::mill-scalalib:${millVersion(millBinaryVersion)}"
  )
  override def ivyDeps = super.ivyDeps() ++ Agg(chimney)
  override def buildInfoMembers = Seq(
    BuildInfo.Value("publishVersion", publishVersion())
  )
  override def buildInfoObjectName = "GuardrailBuildInfo"
  override def buildInfoPackageName = "com.github.lolgab.mill.guardrail.worker"
}

object `mill-guardrail-worker-api` extends Common
object `mill-guardrail-worker-impl` extends Common {
  override def moduleDeps = super.moduleDeps ++ Seq(`mill-guardrail-worker-api`)
  override def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"dev.guardrail::guardrail-core:0.75.3",
    ivy"dev.guardrail::guardrail-java-support:0.73.1",
    ivy"dev.guardrail::guardrail-scala-support:0.75.3",
    // Pending::before we hit 1.0.0, as per https://github.com/guardrail-dev/guardrail/issues/1195
    ivy"dev.guardrail::guardrail-java-async-http:0.72.0",
    ivy"dev.guardrail::guardrail-java-dropwizard:0.72.0",
    ivy"dev.guardrail::guardrail-java-spring-mvc:0.71.2",
    ivy"dev.guardrail::guardrail-scala-akka-http:0.76.0",
    ivy"dev.guardrail::guardrail-scala-dropwizard:0.72.0",
    ivy"dev.guardrail::guardrail-scala-http4s:0.76.0",
    ivy"org.snakeyaml:snakeyaml-engine:2.6",
    chimney
  )
}

object itest extends Cross[itestCross](itestMillVersions)
trait itestCross extends MillIntegrationTestModule with Cross.Module[String] {
  def millVersion: String = crossValue
  def millTestVersion = millVersion
  def pluginsUnderTest = Seq(`mill-guardrail`(millBinaryVersion(millVersion)))
  def temporaryIvyModules = Seq(
    `mill-guardrail-worker-impl`,
    `mill-guardrail-worker-api`
  )
  def testBase = millSourcePath / "src"
}
