package io.softwarchain.learning.zio

import zio.logging.Logging
import zio.{Has, RIO, Task, ZIO}

package object persistence {

  object Persistence {
    trait Service[A] {
      def get(id: Int): ZIO[Logging, Throwable, A]
      def create(a: A): Task[A]
      def delete(id: Int): Task[Boolean]
    }
  }

  type UserPersistence = Has[Persistence.Service[User]]

  def getUser(id: Int): RIO[UserPersistence with Logging, User] = RIO.accessM(_.get.get(id))
  def createUser(a: User): RIO[UserPersistence with Logging, User] = RIO.accessM(_.get.create(a))
  def deleteUser(id: Int): RIO[UserPersistence with Logging, Boolean] = RIO.accessM(_.get.delete(id))
}
