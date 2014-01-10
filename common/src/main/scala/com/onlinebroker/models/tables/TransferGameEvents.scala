package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import com.onlinebroker.models.TransferGameEvent

object TransferGameEvents extends Table[TransferGameEvent]("TransferGameEvents") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def owner = column[Long]("owner")
  def fromAccount = column[Long]("fromAccount")
  def toAccount = column[Long]("toAccount")
  def amount = column[Double]("amount")

  def * = id.? ~ owner ~ fromAccount ~ toAccount 
    ~ amount <> (TransferGameEvent, TransferGameEvent.unapply _)

  def fromAccountFK = foreignKey("TR_FROMACCOUNT_FK", fromAccount, Accounts)(_.id)
  def toAccountFK = foreignKey("TR_TOACCOUNT_FK", toAccount, Accounts)(_.id)
  def ownerFK = foreignKey("TR_OWNER_USER_FK", owner, Users)(_.id)

  def autoInc = owner ~ fromAccount ~ toAccount ~ amount returning id

  def insert(transfer: TransferGameEvent)(implicit s: Session) : Long = 
    autoInc.insert(transfer.owner, transfer.fromAccount,
      transfer.toAccount, account.amount)
}
