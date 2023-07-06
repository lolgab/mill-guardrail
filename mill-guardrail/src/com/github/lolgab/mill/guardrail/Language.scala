package com.github.lolgab.mill.guardrail

import upickle.default._

sealed trait Language {
  def name: String
}
object Language {
  case object Scala extends Language {
    def name: String = "scala"
  }
  case object Java extends Language {
    def name: String = "java"
  }

  implicit val javaRW: ReadWriter[Java.type] = macroRW[Java.type]
  implicit val scalaRW: ReadWriter[Scala.type] = macroRW[Scala.type]
  implicit val rw: ReadWriter[Language] = macroRW[Language]
}
