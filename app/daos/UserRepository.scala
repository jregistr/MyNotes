package daos

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import models.{User, UsersTable}
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

@Singleton
class UserRepository @Inject()(system: ActorSystem, dbConfigProvider: DatabaseConfigProvider)
  extends Repository(system, dbConfigProvider) {

  import dbConfig._
  import profile.api._

  private val users = TableQuery[UsersTable]

  def getAll: Future[Seq[User]] = db.run(users.result)

//  def getById(id: Long): Future[Option[User]] = db.run(users.filter(_.id == id).result.headOption)

}
