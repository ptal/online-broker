var app = $.sammy("#main", function() {
  // include the plugin and alias handlebars() to hb()
  this.use('Handlebars', 'hb');
  this.use(Sammy.JSON);

  this.get('#/list/', function(context) {
    // fetch handlebars-partial first

    this.load("/api/user/" + userId, {"cache": false}, function(userInfo){
      this.render("/assets/templates/accounts.hb", JSON.parse(userInfo)).swap();
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
          data: JSON.stringify({"user-id": parseInt(userId), "transfer-to": context.params.currency}),
          dataType: "json",
          contentType: "application/json; charset=utf-8",
          success: function (data, text) {
            console.log(data);
            context.redirect('#/list/');
          },
          error: function (request, status, error) {
            console.log(request.responseText);
          }
      })
}