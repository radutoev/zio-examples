package io.softwarchain.learning.zio

import io.softwarchain.learning.zio.auth.Auth
import io.softwarchain.learning.zio.aws.domain.Bucket
import zio.logging.Logging
import zio.{Has, RIO, ZIO}

package object aws {
  object Storage {
    trait Service {
      def buckets(): ZIO[Logging, Throwable, List[Bucket]] //Auth with
      def objects(bucketName: String): ZIO[Logging, Throwable, List[String]]
    }
  }

  type Storage = Has[Storage.Service]

  def buckets(): ZIO[Storage with Logging, Throwable, List[Bucket]] = RIO.accessM(_.get.buckets()) //Auth with
  def objects(bucketName: String): ZIO[Storage with Logging, Throwable, List[String]] = RIO.accessM(_.get.objects(bucketName))
}
