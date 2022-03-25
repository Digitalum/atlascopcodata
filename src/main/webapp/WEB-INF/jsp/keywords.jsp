<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Keywords Page</title>
    <style>

    </style>
    <link rel="stylesheet" href="/content/main.css" />
    <script src="https://code.jquery.com/jquery-3.6.0.js"></script>
    <script src="https://code.jquery.com/ui/1.13.0/jquery-ui.js"></script>
</head>
<body>

<input type="text" id="myInput" onkeyup="myFunction()" placeholder="Search for names..">
<table id="myTable">
    <tr class="header">
        <th style="width:30%;">Keyword</th>
        <th style="width:30%;">count</th>
        <th style="width:40%;">synonyms</th>
    </tr>

    <c:forEach items="${keywords}" var="keyword">
        <tr>
            <td>
                <div class="keyword ${keyword.type}">
                    <a href="/keywords/detail/${keyword.key}">${keyword.key}</a>
                </div>
            </td>
            <td>
                    ${keyword.count}
            </td>

            <td class="synonyms">
                <c:if test="${keyword.synonymDto ne null}">
                    <c:forEach items="${keyword.synonymDto.synonyms}" var="s">
                        <div class="synonynm">
                                ${s}
                        </div>
                    </c:forEach>
                </c:if>

            </td>
        </tr>
    </c:forEach>

</table>

<script>
    function myFunction() {
        // Declare variables
        var input, filter, table, tr, td, i, txtValue;
        input = document.getElementById("myInput");
        filter = input.value.toUpperCase();
        table = document.getElementById("myTable");
        tr = table.getElementsByTagName("tr");

        // Loop through all table rows, and hide those who don't match the search query
        for (i = 0; i < tr.length; i++) {
            td = tr[i].getElementsByTagName("td")[0];
            if (td) {
                txtValue = td.textContent || td.innerText;
                if (txtValue.toUpperCase().indexOf(filter) > -1) {
                    tr[i].style.display = "";
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    }

    function getKeyword(keyword) {
        $.ajax({
            url: "/keywords/" + keyword,
            type: 'GET',
            dataType: 'json', // added data type
            success: function (res) {
                console.log(res);
            }
        });
    }

    $(function () {
        $("div.keyword").draggable( ); // { revert: "invalid" }
        $("div.keyword").droppable({
            accept: "td.synonyms",
            drop: function (event, ui) {
                var droppable = $(this);
                var draggable = ui.draggable;
                // Move draggable into droppable
                var keywordwrapper = droppable.parent();

                draggable.appendTo(keywordwrapper);
                draggable.css({top: '0px', left: '0px'});
            }
        });
    });
</script>
</body>
</html>