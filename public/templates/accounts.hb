
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