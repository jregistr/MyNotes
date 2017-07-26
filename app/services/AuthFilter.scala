package services

import javax.inject.Inject

import akka.stream.Materializer
import daos.UserRepository
import play.api.http.Status._
import play.api.mvc.Results._
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

class AuthFilter @Inject()(val userRepository: UserRepository)
                          (override implicit val mat: Materializer, exec: ExecutionContext) extends Filter {

  private val mustLogin = Forbidden(Constants.badResult("You are not logged in", FORBIDDEN))

  override def apply(f: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    f(rh)
//    if (rh.path.contains("/api")) {
//      val emailOpt: Option[String] = rh.session.get("username")
//      emailOpt match {
//        case Some(_) => f(rh)
//        case None => Future.successful(mustLogin)
//      }
//    } else {
//      f(rh)
//    }
  }
}
