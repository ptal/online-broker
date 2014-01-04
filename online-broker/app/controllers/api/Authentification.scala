package controllers.api

import scala.util._

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import fr.jussieu.models._

case class SubscribeData(email: String, password: String)

object Authentication extends Controller {

  def makeSubscribeResponse(idToken: String) =
    Json.obj(
      "status" -> "OK",
      "name" -> "subscribe",
      "id-token" -> idToken)

  def subscribeData(data: SubscribeData) =
    fr.jussieu.models.Authentication.subscribe(data.email, data.password) match {
      case Left(idToken) => makeSubscribeResponse(idToken)
      case Right(error) => GenericError.makeErrorResponse(error)
    }

  def subscribe() = Action(parse.json) { request =>
    implicit val writeSubscription : Reads[SubscribeData] = Json.reads[SubscribeData]
    request.body.validate[SubscribeData].fold(
      valid = {res => Ok(subscribeData(res))},
      invalid = {_ => Ok(GenericError.makeErrorResponse(MalformedRequest()))}
    )
  }
}