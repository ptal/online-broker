package controllers.api

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError

import models.{Account, Currency, Dollar}
import daos.AccountDAO


object Money extends Controller {

  def do_transfer(userID: Long, transferTo: Currency) = {
    Account.transferMoney(userID, transferTo) match {
      case Some(ratedAmount) => Ok(Json.toJson(
        Map("status" -> "OK", "money" -> ratedAmount.toString, "currency" -> transferTo.name)))
      case None => BadRequest(Json.toJson(
        Map("status" -> "KO", "error" -> "Bad request.")))
    }
  }

  def transfer = Action(parse.json) { request =>

    // Default to dollar
    def convertToCurrency(implicit s: Reads[String]): Reads[Currency] = 
      s.map[Currency](
        Currency.currencyForName(_) match {
          case Some(currency) => currency
          case None => Dollar
        }
      )

    def validCurrency(implicit s: Reads[String]): Reads[String] =
      s.filter(ValidationError("unknown currency"))(
        Currency.currencyForName(_) match {
          case Some(currency) => true
          case None => false
      })

    implicit val transferReads = (
      (__ \ "user-id").read[Long] and
      (__ \ "transfer-to").read[Currency](validCurrency andKeep convertToCurrency)
      tupled
    )
    request.body.validate[(Long,Currency)].fold(
      valid = { x => do_transfer(x._1, x._2) },
      invalid = { _ =>
        BadRequest(Json.toJson(
          Map("status" -> "KO", "error" -> "The request is invalid.")))
      }
    )
  }
}