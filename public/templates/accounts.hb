<h2>Your Accounts (<b>{{name}}</b>)</h2>
<table class="pure-table">
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