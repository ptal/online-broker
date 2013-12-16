
<h2 class="content-subhead">Savings</h2>

<table class="pure-table pure-table-bordered">
    <thead>
        <tr>
            <td>Currency</td>
            <td>Amount</td>
        </tr>
    </thead>
    {{#each accounts}}
    <tbody>
        <tr>
            <td>{{currency}}</td>
            <td>{{amount}}</td>
        </tr>
    </tbody>
    {{/each}}
</table>

<form method="POST" action="#/transfer/" class="pure-form pure-form-aligned">
<fieldset>
    <legend>Transfer</legend>

    <div class="pure-control-group">
        <label for="currencyFrom"> From Currency </label>
        <select name="currencyFrom" id="currency-from-choice">
          <option value="models.Dollar$">Dollar</option>
          <option value="models.Euro$">Euro</option>
          <option value="models.Pound$">Pound</option>
        </select>
    </div>
    <div class="pure-control-group">
        <label for="currencyTo"> To Currency </label>
        <select name="currencyTo" id="currency-to-choice">
          <option value="models.Dollar$">Dollar</option>
          <option value="models.Euro$">Euro</option>
          <option value="models.Pound$">Pound</option>
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


