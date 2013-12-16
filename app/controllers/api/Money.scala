package controllers.api

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError

import models.{Transfer, Currency, Dollar}
import daos.{UserDAO, AccountDAO}


object Money extends Controller {

  def doTransfer(userID: Long, transferFrom: Currency, transferTo: Currency, amount: Double) = {
    implicit val currencyWriter = models.Currency.writeCurrency
    implicit val writer = Json.writes[Transfer]
    val result = AccountDAO.transfer(transferFrom, transferTo, amount, userID)
    Json.toJson(result)
  }

  def transfer = Action(parse.json) { request =>
    print ("In transfer\n")
    // Default to dollar
    // TODO: It should exist a way to replace Dollar by ValidationError("unknown currency")
    //       and avoid the use of validCurrency...
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
      (__ \ "userId").read[Long] and
      (__ \ "amount").read[Double] and
      (__ \ "currencyFrom").read[Currency](validCurrency andKeep convertToCurrency) and
      (__ \ "currencyTo").read[Currency](validCurrency andKeep convertToCurrency)
      tupled
    )
    request.body.validate[(Long, Double, Currency, Currency)].fold(
      valid = { x => Ok(doTransfer(x._1, x._3, x._4, x._2)) },
      invalid = { error =>
        BadRequest(Json.toJson(
          Map("status" -> "KO", "error" -> s"The request is invalid. Error: ${error.toString}")))
      }
    )
  }
}