package daos

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import models.{Item, TodoList}
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

@Singleton
class TodoListRepository @Inject()(system: ActorSystem, dbConfigProvider: DatabaseConfigProvider)
  extends Repository(system, dbConfigProvider) {

  import dbConfig._
  import profile.api._

  private val todoLists = TodoList.todoLists
  private val items = Item.items

  def getAllForUser(userId: Long): Future[Seq[(TodoList, Seq[Item])]] = {
    val usersTodoLists = todoLists.filter(_.userId === userId)

    val temp: Future[Seq[(TodoList, Seq[Item])]] = db.run(usersTodoLists.result).map(todos => {
      todos.map(todo => {
        db.run(items.filter(_.todoListId === todo.id).result).map(items => todo -> items)
      })
    }).flatMap(l => Future.sequence(l))
    temp
  }

  def getTodoListById(listId: Long): Future[Option[(TodoList, Seq[Item])]] = {
    val listQuery = todoLists.filter(_.id === listId).result.headOption
    db.run(listQuery).map {
      case Some(list) =>
        val itemsQuery = items.filter(_.todoListId === list.id).result
        val paired: Future[Option[(TodoList, Seq[Item])]] = db.run(itemsQuery).map(items => Some(list -> items))
        paired
      case None => Future {
        None
      }
    }.flatMap(identity)
  }

  def createTodoList(list: TodoList): Future[TodoList] = {
    val query = (todoLists returning todoLists.map(t => (t.userId, t.name, t.description, t.id))) += list
    db.run(query).map(tuple => (TodoList.apply _).tupled(tuple))
  }

  def deleteTodoList(listId: Long): Future[Boolean] = {
    val query = todoLists.filter(_.id === listId).delete
    db.run(query).map(_ > 0)
  }

  def addItem(item: Item): Future[Item] = {
    val query = (items returning items.map(t => (t.todoListId, t.text, t.checked, t.id))) += item
    db.run(query).map(tuple => (Item.apply _).tupled(tuple))
  }

  def deleteItem(itemId: Long): Future[Boolean] = {
    val query = items.filter(_.id === itemId).delete
    db.run(query).map(_ > 0)
  }

}
