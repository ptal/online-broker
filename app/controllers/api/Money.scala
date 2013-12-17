package controllers.api

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError

import models.{Transfer, Currency}
import daos.{CurrencyDAO, UserDAO, AccountDAO}


object Money extends Controller {

  def doTransfer(userID: Long, transferFrom: Currency, transferTo: Currency, amount: Double) = {
    implicit val currencyWriter = models.Currency.writeCurrency
    implicit val writer = Json.writes[Transfer]
    val result = AccountDAO.transfer(transferFrom, transferTo, amount, userID)
    Json.toJson(result)
  }

  def transfer = Action(parse.json) { request =>
    implicit val transferReads = (
      (__ \ "userId").read[Long] and
      (__ \ "amount").read[Double] and
      (__ \ "currencyFrom").read[String] and
      (__ \ "currencyTo").read[String]
      tupled
    )
    request.body.validate[(Long, Double, String, String)].fold(
      valid = { x =>
        val (userId, amount, fromCurrencyName, toCurrencyName) = x
        val transfer = for {
          fromCurrency <- daos.CurrencyDAO.findCurrentExchangeRate(fromCurrencyName)
          toCurrency <- daos.CurrencyDAO.findCurrentExchangeRate(toCurrencyName)
        } yield {Ok(doTransfer(userId, fromCurrency, toCurrency, amount))}
        transfer.getOrElse(
          BadRequest(
            Json.obj(
              "status" -> "KO",
              "error" -> "Error when retrieving the exchange rate for a currency"
            )
          )
        )
      },
      invalid = { error =>
        BadRequest(Json.toJson(
          Map("status" -> "KO", "error" -> s"The request is invalid. Error: ${error.toString}")))
      }
    )
  }
}