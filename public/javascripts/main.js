var app = $.sammy("#main", function() {
    // include the plugin and alias handlebars() to hb()
   this.use('Handlebars', 'hb');
   this.use(Sammy.JSON);

   this.get('#/user/:id/accounts/', function(context) {
   // fetch handlebars-partial first

     this.load("/rest/user/" + context.params.id, function(userInfo){
       this.render("/assets/templates/accounts.hb", JSON.parse(userInfo)).swap();
     });

   });
});

$(function() {
    app.run()
});