package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.{UserAggregatedView, User, Account, Currency}
import daos.UserDAO


object UserController extends Controller {

  def index(id: Long) = Action {
    implicit val writeCurrency = new Writes[Currency]{
      def writes(a: Currency) : JsValue = JsString(a.name)
    }
    implicit val writeAccount = Json.writes[Account]
    implicit val writeUser : Writes[User] = Json.writes[User]
    implicit val writeUserAgg : Writes[UserAggregatedView] = Json.writes[UserAggregatedView]

    UserDAO.findByIdWithAggView(id) match {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound("The user does not exist")
    }

  }

}