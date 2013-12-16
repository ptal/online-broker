package models

import play.api.libs.json.{JsString, JsValue, Writes}

sealed abstract class Currency {
  def name = this.getClass.getName
  def rate: Double
}
object Dollar extends Currency {
  def rate = 1
}
object Euro extends Currency {
  def rate = 0.5
}
object Pound extends Currency {
  def rate = 2
}

object Currency {

  val writeCurrency = new Writes[Currency]{
    def writes(a: Currency) : JsValue = JsString(a.name)
  }

  val names = List(Dollar, Euro, Pound).map((x) => (x.name, x)).toMap

  def currencyForName(name: String) : Option[Currency] = names.get(name)

  def convert(from: Currency, to:Currency, amount:Double): Double =
    amount / from.rate * to.rate
}


case class User(id: Long, name: String)

case class Account(currency: Currency, amount: Double, owner: Long)

case class Transfer(currencyFrom: Currency, currencyTo: Currency, amount: Double, owner: Long)

object Account {


}

case class UserAggregatedView(id: Long, name: String, accounts: Set[Account])

object UserAggregatedView {

  def create(user: User, accounts: Set[Account]) : UserAggregatedView =
        UserAggregatedView(id = user.id, name = user.name, accounts = accounts)

}


