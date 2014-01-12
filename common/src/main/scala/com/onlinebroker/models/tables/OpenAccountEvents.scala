package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import scalaz.\/

import com.onlinebroker.models._

/*
TODO: Leaving the logic of opening accounts for later I'd rather have the rest working before
object OpenAccountEvents extends Table[OpenAccountEvent]("OpenAccountEvents") {

  val eventName = "OpenAccountEvents"

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def owner = column[Long]("owner")
  def whichAccount = column[Long]("whichAccount")
  def paymentMethod = column[String]("paymentMethod")

  def * = id.? ~ owner ~ whichAccount ~ paymentMethod <> (OpenAccountEvent.apply _, OpenAccountEvent.unapply _)

  def whichAccountFK = foreignKey("TR_WHICHACCOUNT_FK", whichAccount, Accounts)(_.id)
  def ownerFK = foreignKey("TR_OWNER_USER_FK", owner, Users)(_.id)

  def autoInc = owner ~ whichAccount ~ paymentMethod returning id

  def insert(open: OpenAccountEvent)(implicit s: Session) : Long = 
    autoInc.insert((open.owner, open.whichAccount, open.paymentMethod))
}
*/