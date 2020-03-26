package io.softwarchain.learning.zio

import zio.logging.Logging
import zio.{Has, RIO, ZIO}

package object aws {
  object Storage {
    trait Service {
      def buckets(): ZIO[Logging, Throwable, List[String]]
      def objects(bucketName: String): ZIO[Logging, Throwable, List[String]]
    }
  }

  type Storage = Has[Storage.Service]

  def buckets(): ZIO[Storage with Logging, Throwable, List[String]] = RIO.accessM(_.get.buckets())
  def objects(bucketName: String): ZIO[Storage with Logging, Throwable, List[String]] = RIO.accessM(_.get.objects(bucketName))
}
