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

        <td>{{newC.name}}</td>
        <td>{{newC.acronym}}</td>
        <td>{{newC.exchangeRate}}</td>
        {{#increase}}
            <td><span class="glyphicon glyphicon-arrow-up" style="color: green;"> </span><td>
        {{/increase}}
        {{#decrease}}
            <td><span class="glyphicon glyphicon-arrow-down" style="color: red;"> </span><td>
        {{/decrease}}
        {{#equal}}
                    <td><td>
        {{/equal}}
    </tr>
    {{/each}}
  </tbody>

</div>