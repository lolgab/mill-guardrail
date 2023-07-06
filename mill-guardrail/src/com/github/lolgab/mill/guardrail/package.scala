package com.github.lolgab.mill

package object guardrail {
  implicit def liftToSome[T](value: T): Option[T] = Some(value)
}
