<h2 class="content-subhead">Currencies</h2>

<table class="table table-striped"">
  <thead>
    <tr>
        <td>Name</td>
        <td>Acronym</td>
        <td>ExchangeRate</td>
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