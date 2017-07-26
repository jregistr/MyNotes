package daos

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

@Singleton
class UserRepository @Inject()(system: ActorSystem, dbConfigProvider: DatabaseConfigProvider)
  extends Repository(system, dbConfigProvider) {

  import dbConfig._
  import profile.api._

  private val users = User.users

  def getAll: Future[Seq[User]] = db.run(users.result)

  def getById(id: Long): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  def getByEmail(email: String): Future[Option[User]] = db.run(users.filter(_.email === email).result.headOption)

  def create(user: User): Future[Option[User]] = {
    val hashed = BCrypt.hashpw(user.password, BCrypt.gensalt())
    val query = (users returning users.map(table => (table.id, table.email, table.password,
      table.firstName, table.lastName))) += user.copy(password = hashed)
    val run: Future[(Long, String, String, Option[String], Option[String])] = db.run(query)

    run map {
      case (id, email, pass, first, last) => Some(User(email, pass, first, last, id))
    }
  }

}
