package io.softwarchain.learning.zio.echo

import caliban.GraphQL.graphQL
import caliban.{GraphQL, RootResolver}
import caliban.schema.GenericSchema
import io.softwarchain.learning.zio.EchoError
import zio.ZIO
import zio.logging.Logging

object EchoGql extends GenericSchema[Echo with Logging] {
  case class Queries(
    echo: Message => ZIO[Echo with Logging, EchoError, Message]
  )

  implicit val messageSchema: EchoGql.Typeclass[Message] = gen[Message]

  val gqlApi: GraphQL[Echo with Logging] =
    graphQL(
      RootResolver(
        Queries(
          args => echo(Message(args.message))
        )
      )
    )

}
