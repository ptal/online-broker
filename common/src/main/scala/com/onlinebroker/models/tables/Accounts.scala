package com.onlinebroker.models.tables

import scala.slick.session.Database
import scala.slick.driver.MySQLDriver.simple._

import com.onlinebroker.models.Account

object Accounts extends Table[Account]("Accounts") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def owner = column[Long]("owner")
  def currency = column[Long]("currency")
  def amount = column[Double]("amount")

  def currencyFK = foreignKey("TR_CURRENCY_FK", currency, Currencies)(_.id)
  def owningUserFK = foreignKey("TR_ACCOUNT_FK", owner, Users)(_.id)

  def * = id.? ~ owner ~ currency ~ amount <> (Account, Account.unapply _)

  def uniqueCurrencyAccountByUser = index("UNIQUE_CURRENCY_ACCOUNT_BY_USER", 
    (owner, currency), unique = true)

  def autoInc = owner ~ currency ~ amount returning id

  def insert(account: Account)(implicit s: Session) : Long = 
    autoInc.insert(account.owner, account.currency, account.amount)
}
