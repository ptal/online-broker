package com.onlinebroker.models

case class User(id: Long, userProviderId: String)

case class Account( currencyAcronym: String,
                    amount: Double,
                    owner: Long,
                    currencyName:String,
                    currencyExchangeRate: Double
                   )

case class Transfer(currencyFrom: String, currencyTo: String, amount: Double, owner: Long)

case class UserAggregatedView(id: Long, name: String, accounts: Set[Account], totalAmountInDollars: Double)

object UserAggregatedView {

  def create(user: User, accounts: Set[Account], totalAmountInDollars: Double) : UserAggregatedView =
        UserAggregatedView(id = user.id, name = user.userProviderId, accounts = accounts, totalAmountInDollars = totalAmountInDollars)

}


