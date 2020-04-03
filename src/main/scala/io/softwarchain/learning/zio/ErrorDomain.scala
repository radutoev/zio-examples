package io.softwarchain.learning.zio

import scala.util.control.NoStackTrace

trait ProgramError extends NoStackTrace

final case class EchoError() extends ProgramError
