
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


<select name="currencies" id="currency-choice">
  <option value="dollar">Dollar</option>
  <option value="euro">Euro</option>
  <option value="pound">Pound</option>
</select>

<button type="button" onclick="transfer_currencies()">Transfer</button>
