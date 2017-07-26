package models

import play.api.libs.json._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

case class Item(todoListId: Long, text: String, checked: Boolean = false, id: Long = -1) extends Model

object Item {
  implicit val itemFormat: OFormat[Item] = Json.format[Item]
  val items: TableQuery[ItemsTable] = TableQuery[ItemsTable]
}

class ItemsTable(tag: Tag) extends Table[Item](tag, "items") {

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def todoListId: Rep[Long] = column[Long]("todo_list")

  def text: Rep[String] = column[String]("text")

  def checked: Rep[Boolean] = column[Boolean]("checked")

  def todoListFK = foreignKey("todo_list_fk", todoListId, TodoList.todoLists)(_.id,
    onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  override def * : ProvenShape[Item] = (todoListId, text, checked, id) <> ((Item.apply _).tupled, Item.unapply)
}
