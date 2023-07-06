# Guardrail Mill Plugin

Port of the [Guardrail Sbt Plugin](https://github.com/guardrail-dev/guardrail)

## Getting Started

After importing it in the `build.sc` file:

```scala
import $ivy.`com.github.lolgab::mill-guardrail::x.y.z`
import com.github.lolgab.mill.guardrail._
```

this plugin can be mixed in a `ScalaModule` defining the `guardrailTasks` target:

```scala
object server extends ScalaModule with Guardrail {
  def guardrailTasks = T.input {
    Seq(
      ScalaServer(
        PathRef(T.workspace / "server.yml"),
        pkg = "com.example.server",
        framework = "http4s"
      )
    )
  }

  // ... other settings
}
```

## Changelog

### 0.0.1

- First version
