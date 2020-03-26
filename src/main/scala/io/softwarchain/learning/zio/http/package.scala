package io.softwarchain.learning.zio

package object http {
  final case class ApiError(reason: String) extends AnyVal
}
