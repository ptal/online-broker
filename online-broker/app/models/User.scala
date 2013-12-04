package models

sealed abstract class Currency {
  def name = this.getClass.getName
}
case class Dollar() extends Currency
case class Euro() extends Currency
case class Pound() extends Currency


case class User(name: String, accounts: Set[Account])

case class Account(currency: Currency, amount: Long)
