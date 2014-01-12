<h2 class="content-subhead">Currencies</h2>

<table class="table table-striped"">
  <thead>
    <tr>
        <th>Name</th>
        <th>Acronym</th>
        <th>ExchangeRate</th>
        <th></th>
    </tr>
  </thead>
  <tbody>
    {{#each currencies}}
    <tr>

        <td>{{name}}</td>
        <td>{{acronym}}</td>
        <td>{{exchangeRate}}</td>
        {{#exchangeIncrease}}
            <td><span class="glyphicon glyphicon-arrow-up" style="color: green;"> </span><td>
        {{/exchangeIncrease}}
        {{^exchangeIncrease}}
            <td><span class="glyphicon glyphicon-arrow-down" style="color: red;"> </span><td>
        {{/exchangeIncrease}}
    </tr>
    {{/each}}
  </tbody>

</div>