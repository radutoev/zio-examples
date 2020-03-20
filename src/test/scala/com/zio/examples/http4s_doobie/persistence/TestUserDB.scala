//package com.zio.examples.http4s_doobie.persistence
//import com.zio.examples.http4s_doobie.{User, UserNotFound}
//import zio.logging.Logging
//import zio.logging.slf4j.Slf4jLogger
//import zio.{Ref, Task, ZLayer}
//
//case class TestUserDB(users: Ref[Vector[User]]) extends Persistence.Service[User] {
//  val createTable: Task[Unit] =
//    Ref.make(Vector.empty[User]).unit
//  def get(id: Int): Task[User] =
//    users.get.flatMap(users =>
//      Task.require(UserNotFound(id))(Task.succeed(users.find(_.id == id))))
//  def create(user: User): Task[User] =
//    users.update(_ :+ user).map(_ => user)
//  def delete(id: Int): Task[Boolean] =
//    users.modify(users => true -> users.filterNot(_.id == id))
//}
//
//object TestUserDB {
//  val layer: ZLayer[Any, Nothing, UserPersistence with Logging] =
//    ZLayer.fromEffect(Ref.make(Vector.empty[User]).map(TestUserDB(_))) ++
//     Slf4jLogger.make((_, message) => message)
//}
