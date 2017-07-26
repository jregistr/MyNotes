package controllers

import javax.inject.{Inject, Singleton}

import daos.UserRepository
import models.User
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc._
import services.Constants.{badResult, goodResult}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthController @Inject()(cc: ControllerComponents, userRepo: UserRepository)
                              (implicit executionContext: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {

  private val internal: PartialFunction[Throwable, Result] = {
    case _ => InternalServerError(badResult("Internal server error", INTERNAL_SERVER_ERROR))
  }

  private val logger = Logger(getClass)

  private val userForm = Form(
    mapping(
      "email" -> email,
      "password" -> text(minLength = 6),
      "firstName" -> optional[String](text),
      "lastName" -> optional[String](text),
      "id" -> ignored(-1L)
    )(User.apply)(User.unapply)
  )

  def all: Action[AnyContent] = Action.async {
    userRepo.getAll.map(users => {
      Ok(Json.toJson(users))
    }).recover(internal)
  }

  def getById(id: Long): Action[AnyContent] = Action.async {
    userRepo.getById(id).map {
      case Some(user) => Ok(goodResult(Json.toJson(user)))
      case None => NotFound(badResult("No such user exists", NOT_FOUND))
    }.recover(internal)
  }

  def create(): Action[AnyContent] = {
    val bad = BadRequest(badResult("Unable to make user with such fields", BAD_REQUEST))
    Action.async { implicit request =>
      userForm.bindFromRequest().fold(formWithErrors => {
        Future {
          BadRequest(badResult(formWithErrors.errorsAsJson, BAD_REQUEST))
        }
      },
        userData => {
          userRepo.create(userData).map {
            case Some(user) => Ok(goodResult(Json.toJson(user)))
            case None => bad
          }.recover { case t: Throwable =>
            logger.error("Error during create", t)
            BadRequest(badResult(t.getMessage, BAD_REQUEST))
          }
        })
    }
  }

}
