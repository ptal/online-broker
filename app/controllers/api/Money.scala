package controllers.api

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError

import models.{Transfer}
import daos.{ExchangeRate, CurrencyDAO, UserDAO, AccountDAO}


object Money extends Controller {

  def makeTransferResponse(amount: Double) = {
    Json.obj(
      "status" -> "OK",
      "amount" -> Json.toJson(amount)
    )
  }

  def listCurrencies = Action {
    implicit val writer = ExchangeRate.writeExchangeRate
    Ok(Json.obj(
      "status" -> "OK",
      "currencies" -> Json.toJson(CurrencyDAO.getAllCurrencies)
    ))
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
      valid = { case (userID, amount, fromCurrencyAcronym, toCurrencyAcronym) =>
        val transfer = for {
          ratedAmount <- AccountDAO.transfer(fromCurrencyAcronym, toCurrencyAcronym, amount, userID)
        } yield {Ok(makeTransferResponse(ratedAmount))}
        transfer.getOrElse(
          BadRequest(
            Json.obj(
              "status" -> "KO",
              "error" -> "Error when retrieving the exchange rate for a currency"
            )
          )
        )
      },
      invalid = { error => BadRequest(Json.toJson(Map(
        "status" -> "KO", 
        "error" -> s"The request is invalid. Error: ${error.toString}"
      )))}
    )
  }
}