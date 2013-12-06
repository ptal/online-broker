package daos

import scala.slick.session.Database

/**
 * Created with IntelliJ IDEA.
 * User: zenexity
 * Date: 06/12/13
 * Time: 09:21
 * To change this template use File | Settings | File Templates.
 */
object DBAccess {

  def db = Database.forURL("jdbc:h2:file:test1", driver = "org.h2.Driver")

}
