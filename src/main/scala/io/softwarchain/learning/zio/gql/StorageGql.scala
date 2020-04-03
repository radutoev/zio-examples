package io.softwarchain.learning.zio.gql

import caliban.GraphQL.graphQL
import caliban.schema.GenericSchema
import caliban.{GraphQL, RootResolver}
import io.softwarchain.learning.zio.aws.{Storage, buckets}
import io.softwarchain.learning.zio.aws.domain.Bucket
import zio.ZIO
import zio.logging.Logging

object StorageGql extends GenericSchema[Storage with Logging] {
  case class Queries(
    buckets: () => ZIO[Storage with Logging, Throwable, List[Bucket]]
  )

  implicit val bucketSchema: StorageGql.Typeclass[Bucket] = gen[Bucket]

  val gqlApi: GraphQL[Storage with Logging] =
    graphQL(
      RootResolver(
        Queries(
          () => buckets()
        )
      )
    )
}
