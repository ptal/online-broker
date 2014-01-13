
<h2 class="content-subhead"> Accounts</h2>


<div class="pure-g">
        <div id="chart" class="pure-u-3-5">
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
                       <td>{{fullCurrency.fullName}}</td>
                       <td>{{fullCurrency.acronym}}</td>
                       <td>{{account.amount}}</td>
                   </tr>
                   {{/each}}
               </tbody>

           </table>
        </div>
        <div id="y_axis" class="pure-u-1-5"></div>
        <div id="y_axis" class="pure-u-1-5" style="text-align:center">
            <img data-src="holder.js/100x80" alt="Avatar" src="{{avatar}}" style="width:140px;height:140px">
            <p><b>{{userProfile.fullName}}</b></p>
        </div>

</div>

<form method="POST" action="#/transfer/" class="pure-form pure-form-aligned">
<fieldset>
    <legend>Transfer</legend>
    <div class="alert alert-danger" id="errorTransfer" style="display:none">
    </div>
    <div class="pure-control-group">
        <label for="currencyFrom"> From Currency </label>

        <select name="currencyFrom" id="currency-from-choice">
        {{#each userInfo.accounts}}
          <option value="{{fullCurrency.acronym}}">{{fullCurrency.fullName}}({{exchangeRate}})</option>
        {{/each}}
        </select>
    </div>
    <div class="pure-control-group">
        <label for="currencyTo"> To Currency </label>
        <select name="currencyTo" id="currency-to-choice">
          {{#each userInfo.accounts}}
            <option value="{{fullCurrency.acronym}}">{{fullCurrency.fullName}}({{exchangeRate}})</option>
          {{/each}}
        </select>
    </div>
    <div class="pure-control-group">
        <label for="amount"> Amount </label>
        <input type="text" name="amount"/>
    </div>
    <div class="pure-controls">
        <button type="submit" class="pure-button pure-button-primary">Transfer</button>
    </div>

</fieldset>
</form>

<form method="POST" action="#/openaccount/" class="pure-form pure-form-aligned">
<fieldset>
    <legend>Open Account</legend>
    <div class="alert alert-danger" id="errorOpenAccount" style="display:none">
    </div>
    <div class="pure-control-group">
        <label for="currencyFrom"> From Currency </label>

        <select name="currencyFrom" id="currency-from-choice">
        {{#each userInfo.accounts}}
          <option value="{{fullCurrency.acronym}}">{{fullCurrency.fullName}}({{exchangeRate}})</option>
        {{/each}}
        </select>
    </div>
    <div class="pure-control-group">
        <label for="currencyTo"> To Currency </label>
        <select name="currencyTo" id="currency-to-choice">
          {{#each currencies}}
            <option value="{{acronym}}">{{fullName}}({{exchangeRate}})</option>
          {{/each}}
        </select>
    </div>
    <div class="pure-controls">
        <p class="help-block">Opening an account costs 100 Dollars</p>
        <button type="submit" class="pure-button pure-button-primary">Open Account</button>
    </div>

</fieldset>
</form>


