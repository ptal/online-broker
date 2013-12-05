package models

sealed abstract class Currency {
  def name = this.getClass.getName
}
object Dollar extends Currency
object Euro extends Currency
object Pound extends Currency

object Currency {
  def currencyForName(name: String) : Currency = ???
}


case class User(id: Long, name: String)

case class Account(id: Long, currency: Currency, amount: Long, owner: Long)
