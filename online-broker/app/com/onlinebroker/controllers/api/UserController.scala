package com.onlinebroker.controllers.api

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import com.onlinebroker.models._


object UserController extends Controller {

  def index(providerId: String) = Action {

    // implicit val writeAccount : Writes[Account] = Json.writes[Account]
    // implicit val writeUser : Writes[User] = Json.writes[User]
    // implicit val writeUserAgg : Writes[UserAggregatedView] = Json.writes[UserAggregatedView]

    // UserDAO.findByIdWithAggView(githubUserId) match {
    //   case Some(user) => Ok(Json.toJson(user))
    //   case None => NotFound("The user does not exist")
    // }
  }
}