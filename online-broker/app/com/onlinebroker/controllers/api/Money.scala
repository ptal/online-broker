package com.onlinebroker.controllers.api

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.libs.iteratee.Concurrent

import com.onlinebroker.models.TransferGameEvent


object Money extends Controller {

  val (out, channel) = Concurrent.broadcast[String]

  def makeTransferResponse(amount: Double) = {
    Json.obj(
      "status" -> "OK",
      "amount" -> Json.toJson(amount)
    )
  }

  def listCurrenciesNames = Action {
    Ok(CurrencyDAO.nameOfAllCurrencies().foldLeft(new JsArray){
      case (jarray, (acronym, name)) =>
        jarray :+ new JsObject(List((acronym, new JsString(name))))
    })
  }

  def updateCurrencies = Action { request =>
    implicit val writer = Json.writes[ExchangeRate]
    channel.push(Json.toJson(CurrencyDAO.getAllCurrencies).toString())
    Ok("Updated Currencies.")
  }

  def listCurrenciesWebSocket = WebSocket.using[String] { request =>
    // Log events to the console
    val in = Iteratee.foreach[String](println).map { _ =>
      println("Disconnected")
    }
    (in, out)
  }

  def listCurrencies = Action {
    implicit val writer = Json.writes[ExchangeRate]
    Ok(Json.obj(
      "status" -> "OK",
      "currencies" -> Json.toJson(CurrencyDAO.getAllCurrencies.sortBy(_.name))
    ))

  }

  def transfer = Action(parse.json) { request =>
    implicit val transferReads = (
      (__ \ "userId").read[String] and
      (__ \ "amount").read[Double] and
      (__ \ "currencyFrom").read[String] and
      (__ \ "currencyTo").read[String]
      tupled
    )
    request.body.validate[(String, Double, String, String)].fold(
      valid = { case (userID, amount, fromCurrencyAcronym, toCurrencyAcronym) =>
        val transfer = for {
          ratedAmount <- TransferGameEvent.transfer(fromCurrencyAcronym, toCurrencyAcronym, amount, userID)
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