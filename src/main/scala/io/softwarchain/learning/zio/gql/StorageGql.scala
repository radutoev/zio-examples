package io.softwarchain.learning.zio.gql

import caliban.GraphQL.graphQL
import caliban.schema.GenericSchema
import caliban.wrappers.Wrappers.timeout
import caliban.{GraphQL, RootResolver}
import io.softwarchain.learning.zio.aws.{Storage, buckets}
import io.softwarchain.learning.zio.aws.domain.Bucket
import zio.ZIO
import zio.clock.Clock
import zio.duration._
import zio.logging.Logging

object StorageGql extends GenericSchema[Storage with Logging] {
  case class Queries(
    buckets: () => ZIO[Storage with Logging, Throwable, List[Bucket]]
  )

  implicit val bucketSchema: StorageGql.Typeclass[Bucket] = gen[Bucket]

  val storageApi: GraphQL[Clock with Storage with Logging] =
    graphQL(
      RootResolver(
        Queries(
          () => buckets()
        )
      )
    ) @@ timeout(5 seconds)
}
