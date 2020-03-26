package io.softwarchain.learning.zio.aws

import java.net.URI
import java.util.concurrent.CompletableFuture

import io.softwarchain.learning.zio.configuration
import io.softwarchain.learning.zio.configuration.S3Config
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{Bucket, ListBucketsResponse}
import zio.clock.Clock
import zio.logging._
import zio.{Has, ZIO, ZLayer}

import scala.jdk.CollectionConverters._

final class StorageService(s3AsyncClient: S3AsyncClient) extends Storage.Service {
  override def buckets(): ZIO[Logging, Throwable, List[String]] = {
//    val response = s3AsyncClient.listBuckets().get()
//    println(response)

    for {
      _               <- logLocally(LogAnnotation.Name("StorageService" :: Nil)) {
                            logInfo("Fetching existent buckets")
                          }
      bucketResponse  <- ZIO.fromCompletionStage[ListBucketsResponse](s3AsyncClient.listBuckets())
      buckets         =  bucketResponse.buckets().asScala.toList.map(_.name())
      _               <- logLocally(LogAnnotation.Name("StorageService" :: Nil)) {
                            logInfo(s"Found ${buckets.length}")
                         }
    } yield buckets
  }
}

object StorageService {
  def live(): ZLayer[Has[S3Config], Throwable, Storage] =
    ZLayer.fromEffect(
      for {
        s3Config <- configuration.s3Config
        storageService = new StorageService(S3AsyncClient.builder()
          //credentials can also be set as env variables directly.
          .endpointOverride(URI.create("http://localhost:4572")) //just for example
          .region(Region.US_EAST_1)
          .credentialsProvider(() => AwsBasicCredentials.create(s3Config.accessKey.value, s3Config.secretAccessKey.value))
          //      .httpClient() custom http client?
          .build())
      } yield storageService
    )
}
