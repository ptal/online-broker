
<h2 class="content-subhead">Grid Helper Classes</h2>

<p> Savings </p>

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

<form method="POST" action="#/transfer/">
    <label for="currency"> Currency </label>
    <select name="currency" id="currency-choice">
      <option value="models.Dollar$">Dollar</option>
      <option value="models.Euro$">Euro</option>
      <option value="models.Pound$">Pound</option>
    </select>
    <!-- <label for="amount"> Amount </label>
    <input type="text" name="amount"/> --!>
    <input type="submit" value="Transfer"/>
    <!-- <button type="button" onclick="transfer_currencies()">Transfer</button> --!>
</form>


