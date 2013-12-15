package daos


import java.sql.{Time, Date}

import scala.slick.session.Database
import scala.slick.driver.H2Driver.simple._

import models.{UserAggregatedView, User, Currency, Account}
import play.api.Logger

object UserTable extends Table[(Long, String)]("Users") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")


  //def lastSendTime = co lumn[Time]("lastSendTime")

  def * = id ~ name

  def autoInc = name returning id

  /**
   * Inserts a new user in the DB with its id automatically generated.
   *
   * @param userName name of the user
   * @return the id of the new created user
   */
  def add(userName: String)(implicit s: Session) : Long = autoInc.insert(userName)

}

object UserDAO {

  def findById(id: Long): Option[User] = {
    DBAccess.db withSession { implicit session =>
      Query(UserTable).filter(_.id === id).firstOption.map(x => User(x._1, x._2))
    }
  }

  def getAccounts(userId: Long): Set[Account] = {
    DBAccess.db withSession { implicit session =>
      Query(AccountTable).filter(_.owner === userId).list().map{
        case (id, currencyString, amount, owner) =>
          Some(Account(id, Currency.currencyForName(currencyString).get, amount, owner))
        case (id, currencyString, amount, owner) if !Currency.currencyForName(currencyString).isDefined =>
          Logger.error(s"The currency $currencyString, stored in the db doesn't exists")
          None
      }.flatten.toSet
    }
  }

  def findByIdWithAggView(userId: Long): Option[UserAggregatedView] = {
    findById(userId).map{ (user: User) => UserAggregatedView.create(user, getAccounts(userId)) }
  }




}