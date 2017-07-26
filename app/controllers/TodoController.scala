package controllers

import javax.inject.{Inject, Singleton}

import daos.TodoListRepository
import models.{Item, TodoList}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc._
import services.Constants.{badResult, goodResult}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TodoController @Inject()(cc: ControllerComponents,
                               todoRepo: TodoListRepository)(implicit context: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {
  private val logger = Logger(getClass)

  private val internal: PartialFunction[Throwable, Result] = {
    case t: Throwable =>
      logger.error("An error occured", t)
      BadRequest(badResult("An error occurred during query", BAD_REQUEST))
  }

  private def deleted(value: Boolean = false) = JsObject(Seq(
    "deleted" -> JsBoolean(value)
  ))

  private implicit def pairToJson(pair: (TodoList, Seq[Item])): JsObject = pair match {
    case (todoList, items) => JsObject(Seq(
      "userId" -> JsNumber(todoList.userId),
      "name" -> JsString(todoList.name),
      "description" -> (todoList.description match {
        case Some(desc) => JsString(desc)
        case _ => JsNull
      }),
      "id" -> JsNumber(todoList.id),
      "items" -> Json.toJson(items)
    ))
  }

  private implicit def pairsToJson(pairs: Seq[(TodoList, Seq[Item])]): JsArray = JsArray(pairs.map(pairToJson))

  private val todoListForm = Form(
    mapping(
      "userId" -> longNumber(),
      "name" -> nonEmptyText(maxLength = 100),
      "description" -> optional[String](text(maxLength = 200)),
      "id" -> ignored(-1L)
    )(TodoList.apply)(TodoList.unapply)
  )

  private val itemForm = Form(
    mapping(
      "todoListId" -> longNumber(),
      "text" -> nonEmptyText(maxLength = 100),
      "checked" -> default[Boolean](boolean, false),
      "id" -> ignored(1L)
    )(Item.apply)(Item.unapply)
  )

  def getForUser(userId: Long): Action[AnyContent] = Action.async {
    todoRepo.getAllForUser(userId).map(pairs => {
      Ok(goodResult(pairs))
    }).recover(internal)
  }

  def getById(id: Long): Action[AnyContent] = Action.async {
    todoRepo.getTodoListById(id).map {
      case Some(value) => Ok(goodResult(value))
      case None => NotFound(badResult("Not found", NOT_FOUND))
    }.recover(internal)
  }

  def createTodoList(): Action[AnyContent] = Action.async { implicit request =>
    todoListForm.bindFromRequest().fold(badForm => {
      Future {
        BadRequest(badResult(badForm.errorsAsJson, BAD_REQUEST))
      }
    }, todoListData => {
      todoRepo.createTodoList(todoListData)
        .map(createdTodoList => Ok(goodResult(Json.toJson(createdTodoList))))
        .recover(internal)
    })
  }

  def deleteTodoList(id: Long): Action[AnyContent] = Action.async {
    todoRepo.deleteTodoList(id).map {
      case true => Ok(goodResult(deleted(true)))
      case _ => BadRequest(badResult(deleted(), BAD_REQUEST))
    }
  }

  def addItem(): Action[AnyContent] = Action.async { implicit request =>
    itemForm.bindFromRequest().fold(badForm => {
      Future {
        BadRequest(badResult(badForm.errorsAsJson, BAD_REQUEST))
      }
    }, data => {
      todoRepo.addItem(data)
        .map(createdItem => Ok(goodResult(Json.toJson(createdItem))))
        .recover(internal)
    })
  }

  def deleteItem(itemId: Long): Action[AnyContent] = Action.async {
    todoRepo.deleteItem(itemId).map {
      case true => Ok(goodResult(deleted(true)))
      case _ => BadRequest(badResult(deleted(), BAD_REQUEST))
    }
  }

}
