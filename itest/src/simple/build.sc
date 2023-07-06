import mill._

import mill.scalalib._
import $file.plugins
import com.github.lolgab.mill.guardrail._

object server extends ScalaModule with Guardrail {
  def scalaVersion = "2.13.11"
  def guardrailTasks = T.input {
    Seq(
      ScalaServer(
        PathRef(os.pwd / "server.yml"),
        pkg = "com.example.server",
        framework = "http4s"
      )
    )
  }
  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"org.http4s::http4s-core:0.23.19",
    ivy"org.http4s::http4s-client:0.23.19",
    ivy"org.http4s::http4s-circe:0.23.19",
    ivy"org.http4s::http4s-dsl:0.23.19",
    ivy"io.circe::circe-core:0.14.5"
  )
}

def verify() = T.command {
  server.compile()
  ()
}
