package models

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

  val names = List(Dollar, Euro, Pound).map((x) => (x.name, x)).toMap

  def currencyForName(name: String) : Option[Currency] = names.get(name)

  def rate(from: Currency, to:Currency, amount:Double): Double =
    amount / from.rate * to.rate
}


case class User(id: Long, name: String)

case class Account(id: Long, currency: Currency, amount: Double, owner: Long)

object Account {

  def transferMoney(owner: Long, toCurrency: Currency): Option[Double] = {
    daos.AccountDAO.findByOwner(owner) match {
      case Some(account) => {
        val ratedAmount = Currency.rate(account.currency, toCurrency, account.amount)
        daos.AccountDAO.updateCurrency(owner, toCurrency.name)
        daos.AccountDAO.updateAmount(owner, ratedAmount)
        Some(ratedAmount)
      }
      case None => None // No user ID found.
    }
  }
}

case class UserAggregatedView(id: Long, name: String, accounts: Set[Account])

object UserAggregatedView {

  def create(user: User, accounts: Set[Account]) : UserAggregatedView =
        UserAggregatedView(id = user.id, name = user.name, accounts = accounts)

}


