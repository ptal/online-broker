package com.onlinebroker.controllers.api

import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.onlinebroker.models.OnlineBrokerError

object GenericError {

  def makeErrorResponse(error: OnlineBrokerError): JsObject =
    Json.obj(
      "status" -> "KO",
      "name" -> error.name,
      "description" -> error.description)
}