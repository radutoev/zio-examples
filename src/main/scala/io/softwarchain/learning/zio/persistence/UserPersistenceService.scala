package io.softwarchain.learning.zio.persistence

import cats.effect.Blocker
import doobie.h2.H2Transactor
import doobie.implicits._
import doobie.{Query0, Transactor, Update0}
import io.softwarchain.learning.zio.{User, UserNotFound, configuration}
import io.softwarchain.learning.zio.configuration.DbConfig

import scala.concurrent.ExecutionContext
import zio._
import zio.blocking.Blocking
import zio.interop.catz._
import zio.logging._

/**
  * Persistence Module for production using Doobie
  */
final class UserPersistenceService(tnx: Transactor[Task]) extends Persistence.Service[User] {
  import UserPersistenceService._

  def get(id: Int): ZIO[Logging, Throwable, User] =
    logLocally(LogAnnotation.Name("UserPersistenceService" :: Nil)) {
      logInfo(s"Fetching user with id=$id")
    }.flatMap(_ =>
      SQL
        .get(id)
        .option
        .transact(tnx)
        .foldM(
          err => Task.fail(err),
          maybeUser => Task.require(UserNotFound(id))(Task.succeed(maybeUser))
        )
    )

  def create(user: User): Task[User] =
    SQL
      .create(user)
      .run
      .transact(tnx)
      .foldM(err => Task.fail(err), _ => Task.succeed(user))

  def delete(id: Int): Task[Boolean] =
    SQL
      .delete(id)
      .run
      .transact(tnx)
      .fold(_ => false, _ => true)
}

object UserPersistenceService {

  object SQL {

    def get(id: Int): Query0[User] =
      sql"""SELECT * FROM USERS WHERE ID = $id """.query[User]

    def create(user: User): Update0 =
      sql"""INSERT INTO USERS (id, name) VALUES (${user.id}, ${user.name})""".update

    def delete(id: Int): Update0 =
      sql"""DELETE FROM USERS WHERE id = $id""".update
  }

  def mkTransactor(
      conf: DbConfig,
      connectEC: ExecutionContext,
      transactEC: ExecutionContext
  ): ZManaged[Any, Nothing, UserPersistenceService] = {
    import zio.interop.catz._

    val xa = H2Transactor
      .newH2Transactor[Task](conf.url,
                             conf.user,
                             conf.password,
                             connectEC,
                             Blocker.liftExecutionContext(transactEC))

    val res = xa.allocated.map {
      case (transactor, cleanupM) =>
        Reservation(ZIO.succeed(transactor), _ => cleanupM.orDie)
    }.uninterruptible

    Managed(res)
      .map(new UserPersistenceService(_))
      .orDie
  }

  def live(connectEC: ExecutionContext): ZLayer[Has[DbConfig] with Blocking, Throwable, UserPersistence] =
    ZLayer.fromManaged (
      for {
        config <- configuration.dbConfig.toManaged_
        blockingEC <- blocking.blocking { ZIO.descriptor.map(_.executor.asEC) }.toManaged_
        managed <- mkTransactor(config, connectEC, blockingEC)
      } yield managed
    )
}
