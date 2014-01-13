
require.config({
    paths:{
        d3:'d3.v3',
        rickshaw:'http://cdnjs.cloudflare.com/ajax/libs/rickshaw/1.4.6/rickshaw.min',
        sammy:'sammy',
        sammyHandlebars: 'sammy.handlebars',
        jquery:'jquery',
        handlebars: 'handlebars'
    },
    shim: {
        d3: {
            exports: 'd3'
        },
        rickshaw: {
            deps: ["d3"],
            exports: "Rickshaw"
        },
        sammyHandlebars : {
            deps: ["handlebars", "jquery"]
        },
        waitSeconds: 15
    }
});

require(["underscore", "d3", "rickshaw", "sammy", "sammyHandlebars"],function(_, d3, rickshaw, sammy, sammyHandlebars) {
   sammy.Handlebars = sammyHandlebars;
   $.sammy = sammy;
   var app = $.sammy("#main", function() {
     // include the plugin and alias handlebars() to hb()
     this.use('Handlebars', 'hb');

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
       $.when($.ajax("/api/user/accounts"), $.ajax("/api/currencies"), $.ajax("/api/currencies/names")).done(function(userInfo, currencies, currencyNames){
         var accounts = _(userInfo[0].accounts).map( function (account) {
           var currencyInfo = _(currencyNames[0].currencies).findWhere({ acronym : account.currency });
           var currencyRate = _(currencies[0].rates).findWhere({ currency : account.currency });
           return { fullCurrency : currencyInfo, account: account, exchangeRate: currencyRate.rate };
         });


         context.render("/assets/templates/accounts.hb", {
           "currencies": mergeCurrencies(currencies[0].rates, currencyNames[0].currencies),
           "userInfo": { accounts : accounts},
           "userProfile" : profile.user,
           "avatar" : profile.avatar
         }).swap();
       });
     });

     this.get('#/currencies/', function(context) {

         connection.onmessage = function (e) {
           updateCurrencies();
         };

         function updateCurrencies() {
           $.when($.ajax("/api/currencies"),$.ajax("/api/currencies/names")).done(function(currencies, currencyNames){
             context.render("/assets/templates/currencies.hb", {
               "currencies": mergeCurrencies(currencies[0].rates, currencyNames[0].currencies)
             }).swap();
           });
         }
         updateCurrencies();
       });

     this.get('#/currency/graph/:currencyAcronym', function(context){

           function renderGraph(data) {
               var max = _.max(_.pluck(data, "y"));
               var min = _.min(_.pluck(data, "y"));
               var linearScale = d3.scale.linear().domain([min, max]).range([0, max]);
               var graph = new Rickshaw.Graph( {
                   element: document.querySelector("#chart"),
                   //renderer : 'line',
                   width: 500,
                   height: 200,
                   series: [{
                       color: 'steelblue',
                       data:  data,
                       scale: linearScale
                   }]
               });
               new Rickshaw.Graph.Axis.Y.Scaled( {
                 graph: graph,
                 orientation: 'left',
                 tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
                 element: document.getElementById('y_axis'),
                 scale: linearScale,
                 grid: true
               } );

               graph.render();
           }

           $.when($.ajax("/api/historic/rates/" + context.params.currencyAcronym)).done(function(history){
               var data = _(history.historic).map(function(elem, index) { return {x : index + 1, y: parseFloat(elem.rate) }});
               context.render("/assets/templates/graph.hb", {
                       "currency": context.params.currencyAcronym
               }).swap().then(function() { renderGraph(data) });

           });
     })

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
               "amount" : parseInt(context.params.amount),
               "from": context.params.currencyFrom,
               "to": context.params.currencyTo,
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
});

