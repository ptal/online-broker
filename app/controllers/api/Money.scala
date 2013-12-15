package controllers.api

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.{Account, Currency}
import daos.AccountDAO


object Money extends Controller {

  def transfer = Action(parse.json) { request =>
    implicit val transferReads = (
      (__ \ "user-id").read[Long] and
      (__ \ "transfer-to").read[String]
      tupled
    )
    request.body.validate[(Long,String)].fold(
      valid = { case (userID, transferTo) => 
        AccountDAO.findByOwner(userID) match {
          case Some(_) => Ok(Json.toJson(
            Map("status" -> "OK", "money" -> "test", "currency" -> "cur")))
          case None => BadRequest(Json.toJson(
            Map("status" -> "KO", "error" -> "Bad user id.")))
        }
      },
      invalid = { _ =>
        BadRequest(Json.toJson(
          Map("status" -> "KO", "error" -> "The request is invalid.")))
      }
    )
  }
}