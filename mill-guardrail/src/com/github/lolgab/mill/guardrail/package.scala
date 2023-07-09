package com.github.lolgab.mill

import scala.language.implicitConversions

package object guardrail {
  implicit def liftToSome[T](value: T): Option[T] = Some(value)
}
