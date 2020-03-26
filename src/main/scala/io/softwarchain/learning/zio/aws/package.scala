package io.softwarchain.learning.zio

import software.amazon.awssdk.services.s3.model.Bucket
import zio.logging.Logging
import zio.{Has, RIO, Task, ZIO}

package object aws {
  object Storage {
    trait Service {
      def buckets(): ZIO[Logging, Throwable, List[String]]
    }
  }

  type Storage = Has[Storage.Service]

  def buckets(): ZIO[Storage with Logging, Throwable, List[String]] = RIO.accessM(_.get.buckets())
}
