package io.softwarchain.learning.zio.aws

import java.net.URI
import java.util.concurrent.CompletableFuture

import io.softwarchain.learning.zio.configuration
import io.softwarchain.learning.zio.configuration.S3Config
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{Bucket, ListBucketsResponse, ListObjectsV2Request}
import zio.logging._
import zio.{Has, IO, ZIO, ZLayer}

import scala.jdk.CollectionConverters._

final class StorageService(s3AsyncClient: S3AsyncClient) extends Storage.Service {
  override def buckets(): ZIO[Logging, Throwable, List[String]] = {
    for {
      _               <- logLocally(LogAnnotation.Name("StorageService" :: Nil)) {
                            logInfo("Fetching existent buckets")
                          }
      buckets           <- handleS3EffectAsync(_.listBuckets()).map(responseToBuckets(_).map(bucketName))
      _                 <- logLocally(LogAnnotation.Name("StorageService" :: Nil)) {
                            logInfo(s"Found ${buckets.size}")
                         }
    } yield buckets
  }

  def objects(bucketName: String): ZIO[Logging, Throwable, List[String]] = for {
    listResponse <- ZIO.fromCompletionStage(s3AsyncClient.listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).build()))
  } yield listResponse.contents().asScala.toList.map(_.key())

  private def handleS3EffectAsync[T](toS3Response: S3AsyncClient => CompletableFuture[T]): ZIO[Any, Throwable, T] =
      IO.effectAsync[Throwable, T] { callback =>
        toS3Response(s3AsyncClient).handle[Unit]((response, err) => {
          err match {
            case null => callback(IO.succeed(response))
            case ex   => callback(IO.fail(ex))
          }
        })
    }

  private val responseToBuckets: ListBucketsResponse => List[Bucket] = response => response.buckets().asScala.toList

  private val bucketName: Bucket => String = b => b.name()
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
