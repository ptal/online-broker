package controllers.api

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.{Account, Currency}
import daos.AccountDAO


object Money extends Controller {

  def transfer = Action(parse.json) { request =>
    (request.body \ "user-id").asOpt[Long].map { owner => 
      AccountDAO.findByOwner(owner) match {
        case Some(_) => (request.body \ "transfer-to").asOpt[String].map { cur =>
          Ok(Json.toJson(
            Map("status" -> "OK", "money" -> "test", "currency" -> cur)
          ))
        }.getOrElse {
          BadRequest(Json.toJson(
            Map("status" -> "KO", "error" -> "no transfer-to field found.")))
        }
        case None => NotFound("The user does not exist")
      }
    }.getOrElse {
      BadRequest(Json.toJson(
        Map("status" -> "KO", "error" -> "no user-id field found.")))
    }
  }
}