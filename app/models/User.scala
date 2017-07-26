package models

import play.api.libs.json._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class User(email: String, password: String,
                firstName: Option[String] = None,
                lastName: Option[String] = None, id: Long = -1) extends Model

object User {

  val users: TableQuery[UsersTable] = TableQuery[UsersTable]

  implicit val userFormat: OFormat[User] = new OFormat[User] {
    override def writes(o: User): JsObject = {
      val ess = Seq(
        "email" -> JsString(o.email),
        "id" -> JsNumber(o.id)
      )
      val name = Seq("firstName" -> o.firstName, "lastName" -> o.lastName).foldLeft(List[(String, JsString)]()) {
        case (list, (key, Some(value))) => list :+ key -> JsString(value)
        case (list, _) => list
      }
      JsObject(ess ++ name)
    }

    override def reads(json: JsValue): JsResult[User] = Json.format[User].reads(json)
  }
}

class UsersTable(tag: Tag) extends Table[User](tag, "users") {

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def email: Rep[String] = column[String]("email", O.Unique)

  def password: Rep[String] = column[String]("password")

  def firstName: Rep[Option[String]] = column[Option[String]]("first_name")

  def lastName: Rep[Option[String]] = column[Option[String]]("last_name")

  override def * : ProvenShape[User] = (email, password, firstName, lastName, id) <> ((User.apply _).tupled,
    User.unapply)
}
