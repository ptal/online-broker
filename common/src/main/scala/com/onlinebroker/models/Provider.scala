package com.onlinebroker.models

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._
import com.onlinebroker.models.SQLDatabase.DBAccess
import com.onlinebroker.models.tables._

case class Provider(
  id: Option[Long],
  name: String
)

object Provider {
  def findNameById(providerId: Long): Option[String] = {
    DBAccess.db withSession { implicit session : Session =>
      Providers.findById(providerId).map(_.name)
    }
  }

  def findIdByName(providerName: String): Option[Long] = {
    DBAccess.db withSession { implicit session : Session =>
      Providers.findByName(providerName).map(_.id.get)
    }
  }
}