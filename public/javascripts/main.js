var app = $.sammy("#main", function() {
  // include the plugin and alias handlebars() to hb()
  this.use('Handlebars', 'hb');
  this.use(Sammy.JSON);

  this.get('#/user/:id/accounts/', function(context) {
    // fetch handlebars-partial first

    this.load("/api/user/" + context.params.id, function(userInfo){
      this.render("/assets/templates/accounts.hb", JSON.parse(userInfo)).swap();
    });
  });
});

$(function() {
  app.run()
});

function transfer_currencies() {
  var cur = "models." + $("#currency-choice option:selected").text() + "$";
  $.ajax({
    url: "localhost:9000/api/transfer",
    type: "POST",
    data: {"user-id": 1,
           "transfer-to": cur},
    datatype: "json",
    success: function (data, text) {
      alert(text);
    },
    error: function (request, status, error) {
      alert(request.responseText);
    }
  })
}