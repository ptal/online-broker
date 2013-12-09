
<h2 class="content-subhead">Grid Helper Classes</h2>

<p> Savings </p>

<table class="pure-table pure-table-bordered">
    <thead>

        <tr>
            <td>Currency</td>
            <td>Amount</td>
            <td>Since Last Week</td>
        </tr>


    </thead>
    {{#each accounts}}
    <tbody>
        <tr>
            <td>{{currency}}</td>
            <td>{{amount}}</td>
            <td>+ 3000</td>
        </tr>
    </tbody>
    {{/each}}
</table>