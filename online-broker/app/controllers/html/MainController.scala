package controllers.html

import play.api.mvc._


object MainController extends Controller with securesocial.core.SecureSocial {

  def userAccounts = SecuredAction { implicit request =>
    Ok(views.html.main.render(request.user.identityId.userId))
  }

}

