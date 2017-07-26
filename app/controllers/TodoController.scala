package controllers

import javax.inject.{Inject, Singleton}

import daos.TodoListRepository
import models.{Item, TodoList, User}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.data.Forms._
import play.api.mvc._
import services.Constants.{badResult, goodResult}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

@Singleton
class TodoController @Inject()(cc: ControllerComponents,
                               todoRepo: TodoListRepository)(implicit context: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {
  private val logger = Logger(getClass)

  private val internal: PartialFunction[Throwable, Result] = {
    case t: Throwable =>
      logger.error("An error occured", t)
      InternalServerError(badResult("Internal server error", INTERNAL_SERVER_ERROR))
  }

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
    todoRepo.getAllForUser(userId).map(groups => {
      Ok(goodResult(Json.toJson(groups)))
    }).recover(internal)
  }

//  def createTodoList(): Action[AnyContent] = Action.async {
//
//  }

}
