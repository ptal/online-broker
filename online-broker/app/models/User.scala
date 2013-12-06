package models

sealed abstract class Currency {
  def name = this.getClass.getName
}
object Dollar extends Currency
object Euro extends Currency
object Pound extends Currency

object Currency {

  val currencies = List(Dollar, Euro, Pound).map((x) => (x.name, x)).toMap

  def currencyForName(name: String) : Option[Currency] = currencies.get(name)

}


case class User(id: Long, name: String)

case class Account(id: Long, currency: Currency, amount: Long, owner: Long)

case class UserAggregatedView(id: Long, name: String, accounts: Set[Account])

object UserAggregatedView {

  def create(user: User, accounts: Set[Account]) : UserAggregatedView =
        UserAggregatedView(id = user.id, name = user.name, accounts = accounts)

}


