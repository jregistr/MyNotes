package services

import play.api.libs.json._

object Constants {

  val AuthTokenHeader = "X-AUTH-TOKEN"

  def goodResult(jsonValue: JsValue): JsObject = JsObject(Seq(
    "success" -> JsBoolean(true),
    "data" -> jsonValue
  ))

  def badResult(message: String, code: Int): JsObject = JsObject(Seq(
    "success" -> JsBoolean(false),
    "error" -> JsNumber(code),
    "message" -> JsString(message)
  ))

  def badResult(json: JsValue, code: Int): JsObject = JsObject(Seq(
    "success" -> JsBoolean(false),
    "error" -> JsNumber(code),
    "message" -> json
  ))

}
