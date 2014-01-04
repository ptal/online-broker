var app = $.sammy("#main", function() {
  // include the plugin and alias handlebars() to hb()
  this.use('Handlebars', 'hb');
  this.use(Sammy.JSON);

  var connection = new WebSocket('ws://localhost:9000/api/db/currenciesws', ['json']);

  this.before(/.*/, function () {
    connection.onmessage = function (e) {} ;
  });

  this.get('#/accounts/', function(context) {
    // fetch handlebars-partial first
    $.when($.ajax("/api/user/" + userId), $.ajax("/api/currencies")).done(function(userInfoText, currenciesText){
      var userInfo = userInfoText[0];
      var currencies = currenciesText[0];
      context.render("/assets/templates/accounts.hb", {
        "currencies": currencies.currencies,
        "userInfo": userInfo
      }).swap();
    });
  });

  this.get('#/currencies/', function(context) {

      connection.onmessage = function (e) {
        context.render("/assets/templates/currencies.hb", {
          "currencies": JSON.parse(e.data)
        }).swap();
      };

      $.when($.ajax("/api/currencies")).done(function(currenciesText){
        context.render("/assets/templates/currencies.hb", {
          "currencies": currenciesText.currencies
        }).swap();
      });

    });

  this.post('#/transfer/', function(context){
    transfer_currencies(context);
  })

});

$(function() {
  app.run()
});

function transfer_currencies(context) {
  $.ajax({
          url: "/api/transfer",
          type: "post",
          data: JSON.stringify({
            "userId": parseInt(userId),
            "currencyFrom": context.params.currencyFrom,
            "currencyTo": context.params.currencyTo,
            "amount" : parseInt(context.params.amount),
          }),
          dataType: "json",
          contentType: "application/json; charset=utf-8",
          success: function (data, text) {
            console.log(data);
            context.redirect('#/accounts/');
          },
          error: function (request, status, error) {
            console.log(request.responseText);
          }
      })
}