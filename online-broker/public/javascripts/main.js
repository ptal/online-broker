var app = $.sammy("#main", function() {
  // include the plugin and alias handlebars() to hb()
  this.use('Handlebars', 'hb');
  this.use(Sammy.JSON);

  var connection = new WebSocket('ws://localhost:9000/api/db/currenciesws', ['json']);

  this.before(/.*/, function () {
    connection.onmessage = function (e) {} ;
  });

  function mergeCurrencies(rates, names) {
     return _(rates).map( function (rate) {
        var currencyInfo = _(names).findWhere({ acronym : rate.currency });
        return { acronym : rate.currency, exchangeRate : rate.rate, fullName : currencyInfo.fullName };
     });
  }
  this.get('#/', function(context) {
    /*userInfo :
            accounts :
                fullCurrency:
                    name
                    acronym
                account :
                    amount */
    $.when($.ajax("/api/user/accounts"), $.ajax("/api/currencies"), $.ajax("/api/currencies/names")).done(function(userInfo, currencies, currencyNames){
      var accounts = _(userInfo[0].accounts).map( function (account) {
        var currencyInfo = _(currencyNames[0].currencies).findWhere({ acronym : account.currency });
        var currencyRate = _(currencies[0].rates).findWhere({ currency : account.currency });
        return { fullCurrency : currencyInfo, account: account, exchangeRate: currencyRate.rate };
      });


      context.render("/assets/templates/accounts.hb", {
        "currencies": mergeCurrencies(currencies[0].rates, currencyNames[0].currencies),
        "userInfo": { accounts : accounts},
      }).swap();
    });
  });

  this.get('#/currencies/', function(context) {

      connection.onmessage = function (e) {
        context.render("/assets/templates/currencies.hb", {
          "currencies": JSON.parse(e.data)
        }).swap();
      };

      $.when($.ajax("/api/currencies"),$.ajax("/api/currencies/names")).done(function(currencies, currencyNames){
        context.render("/assets/templates/currencies.hb", {
          "currencies": mergeCurrencies(currencies[0].rates, currencyNames[0].currencies)
        }).swap();
      });

    });

  this.post('#/transfer/', function(context){
    transfer_currencies(context);
  })

  this.post('#/openaccount/', function(context){
    open_account(context);
  })

});

$(function() {
  app.run();
  window.history.pushState({state:1}, "State 1", "#/");
});

function open_account(context) {
  $.ajax({
          url: "/api/account/open",
          type: "post",
          data: JSON.stringify({
            "account-to-open": context.params.currencyTo,
            "pay-with-account": context.params.currencyFrom
          }),
          dataType: "json",
          contentType: "application/json; charset=utf-8",
          success: function (data, text) {
            console.log(data);
            context.redirect('#/');
          },
          error: function (request, status, error) {
            console.log(request.responseText);
            $("#errorOpenAccount").text(JSON.parse(request.responseText).description);
            $("#errorOpenAccount").css("display", "block");
          }
      })
}

function transfer_currencies(context) {
  $.ajax({
          url: "/api/transfer",
          type: "post",
          data: JSON.stringify({
            "from": context.params.currencyFrom,
            "to": context.params.currencyTo,
            "amount" : parseInt(context.params.amount),
          }),
          dataType: "json",
          contentType: "application/json; charset=utf-8",
          success: function (data, text) {
            console.log(data);
            context.redirect('#/');
          },
          error: function (request, status, error) {
            console.log(request.responseText);
            $("#errorTransfer").text(JSON.parse(request.responseText).description);
            $("#errorTransfer").css("display", "block");
          }
      })
}