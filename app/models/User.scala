package models

import play.api.libs.json._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class User(email: String, firstName: Option[String] = None, lastName: Option[String] = None, id: Long = -1)

object User {
  implicit val userFormat: OFormat[User] = Json.format[User]
}

class UsersTable(tag: Tag) extends Table[User](tag, "users") {

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def email: Rep[String] = column[String]("email", O.Unique)

  def firstName: Rep[Option[String]] = column[Option[String]]("first_name")

  def lastName: Rep[Option[String]] = column[Option[String]]("last_name")

  override def * : ProvenShape[User] = (email, firstName, lastName, id) <> ((User.apply _).tupled, User.unapply)
}


