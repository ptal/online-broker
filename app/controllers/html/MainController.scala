package controllers.html

import play.api.mvc._


object MainController extends Controller {

  def userAccounts(id: Long) = Action {
    Ok(views.html.main.render(id))
  }

}

