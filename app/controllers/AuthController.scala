package controllers

import javax.inject.{Inject, Singleton}

import daos.UserRepository
import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsBoolean, JsObject, Json}
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

  private val welcome = JsObject(Seq(
    "authorized" -> JsBoolean(true)
  ))

  private val userForm = Form(
    mapping(
      "email" -> email,
      "password" -> text(minLength = 6),
      "firstName" -> optional[String](text),
      "lastName" -> optional[String](text),
      "id" -> ignored(-1L)
    )(User.apply)(User.unapply)
  )

  case class LoginDetails(email: String, password: String)

  private val loginForm = Form(
    mapping(
      "email" -> nonEmptyText(),
      "password" -> nonEmptyText()
    )(LoginDetails.apply)(LoginDetails.unapply)
  )

  def all: Action[AnyContent] = Action.async {
    userRepo.getAll.map(users => {
      Ok(Json.toJson(users))
    }).recover(internal)
  }

  def register(): Action[AnyContent] = Action.async { implicit request =>
    userForm.bindFromRequest().fold(badForm => Future {
      BadRequest(badResult(badForm.errorsAsJson, BAD_REQUEST))
    }, userData => {
      userRepo.create(userData).map {
        case Some(user) => Ok(goodResult(welcome)).withSession(request.session + ("username" -> user.email))
        case None => BadRequest(badResult("Check your credentials", BAD_REQUEST))
      }
    })
  }

  def login(): Action[AnyContent] = Action.async { implicit request =>
    val unauth = Unauthorized(badResult("No matching credentials found", UNAUTHORIZED))

    loginForm.bindFromRequest().fold(badForm => Future {
      Unauthorized(badResult(badForm.errorsAsJson, UNAUTHORIZED))
    }, userData => {
      userRepo.getByEmail(userData.email).map {
        case Some(fromDb) => BCrypt.checkpw(userData.password, fromDb.password) match {
          case false => unauth
          case _ => Ok(goodResult(welcome)).withSession(request.session + ("username" -> fromDb.email))
        }
        case None => unauth
      }
    })
  }

  def logout(): Action[AnyContent] = Action { implicit request =>
    Ok("bye").withNewSession
  }

}
