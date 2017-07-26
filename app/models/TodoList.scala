package models

import play.api.libs.json._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class TodoList(userId: Long, name: String, description: Option[String] = None, id: Long = -1) extends Model

object TodoList {
  val todoLists: TableQuery[TodoListTable] = TableQuery[TodoListTable]
  implicit val todoListFormat: OFormat[TodoList] = Json.format[TodoList]
}

class TodoListTable(tag: Tag) extends Table[TodoList](tag, "todo_lists") {

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def userId: Rep[Long] = column[Long]("user_id")

  def name: Rep[String] = column[String]("name")

  def description: Rep[Option[String]] = column[Option[String]]("description")

  def userFK = foreignKey("user_id_fk", userId, User.users)(_.id, onUpdate = ForeignKeyAction.Restrict,
    onDelete = ForeignKeyAction.Cascade)

  override def * : ProvenShape[TodoList] = (userId, name, description, id) <> ((TodoList.apply _).tupled, TodoList.unapply)
}
