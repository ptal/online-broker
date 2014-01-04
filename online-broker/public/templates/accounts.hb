
<h2 class="content-subhead">My Accounts</h2>

<table class="table table-striped">
    <thead>
        <tr>
            <th>Currency Name</th>
            <th>Currency Acronym</th>
            <th>Amount</th>
        </tr>
    </thead>
    <tbody>
        {{#each userInfo.accounts}}
        <tr>
            <td>{{currencyName}}</td>
            <td>{{currencyAcronym}}</td>
            <td>{{amount}}</td>
        </tr>
        {{/each}}
    </tbody>

</table>

<form method="POST" action="#/transfer/" class="pure-form pure-form-aligned">
<fieldset>
    <legend>Transfer</legend>

    <div class="pure-control-group">
        <label for="currencyFrom"> From Currency </label>

        <select name="currencyFrom" id="currency-from-choice">
        {{#each userInfo.accounts}}
          <option value="{{currencyAcronym}}">{{currencyName}}({{currencyExchangeRate}})</option>
        {{/each}}
        </select>
    </div>
    <div class="pure-control-group">
        <label for="currencyTo"> To Currency </label>
        <select name="currencyTo" id="currency-to-choice">
          {{#each currencies}}
            <option value="{{acronym}}">{{name}}({{exchangeRate}})</option>
          {{/each}}
        </select>
    </div>
    <div class="pure-control-group">
        <label for="amount"> Amount </label>
        <input type="text" name="amount"/>
    </div>
    <div class="pure-controls">
        <button type="submit" class="pure-button pure-button-primary">Submit</button>
    </div>

</fieldset>
</form>


