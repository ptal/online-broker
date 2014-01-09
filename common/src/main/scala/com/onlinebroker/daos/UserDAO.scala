package com.onlinebroker.daos

import java.sql.{Time, Date}

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import com.onlinebroker.models.{UserAggregatedView, User}

object UserTable extends Table[(Long, String)]("Users") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def githubUserId = column[String]("providerId")


  //def lastSendTime = co lumn[Time]("lastSendTime")

  def * = id ~ githubUserId

  def autoInc = githubUserId returning id

  /**
   * Inserts a new user in the DB with its id automatically generated.
   *
   * @param userName name of the user
   * @return the id of the new created user
   */
  def add(githubUserId: String)(implicit s: Session) : Long =
    autoInc.insert(githubUserId)

}

object UserDAO {

  def findByGithubUserId(githubUserId: String): Option[User] = {
    DBAccess.db withSession { implicit session =>
      Query(UserTable).filter(_.githubUserId === githubUserId).firstOption.map(x => User(x._1, x._2))
    }
  }

  def findByIdWithAggView(userId: String): Option[UserAggregatedView] = {
    findByGithubUserId(userId).map{ (user: User) => UserAggregatedView.create(user, AccountDAO.findAccountByOwner(user.id).toSet) }
  }




}
