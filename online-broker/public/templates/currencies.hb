<h2 class="content-subhead">Currencies</h2>

<table class="table table-striped"">
  <thead>
    <tr>
        <th>Name</th>
        <th>Acronym</th>
        <th>ExchangeRate</th>
    </tr>
  </thead>
  <tbody>
    {{#each currencies}}
    <tr>

        <td>{{name}}</td>
        <td>{{acronym}}</td>
        <td>{{exchangeRate}}</td>
    </tr>
    {{/each}}
  </tbody>

</div>