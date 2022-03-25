<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head><title>Keywords Page</title>

    <link rel="stylesheet" href="/content/main.css"/>

    <script src="https://code.jquery.com/jquery-3.6.0.js"></script>
    <script src="https://code.jquery.com/ui/1.13.0/jquery-ui.js"></script>
    <script src="https://kit.fontawesome.com/16456af41e.js" crossorigin="anonymous"></script>

    <link rel="stylesheet" href="//code.jquery.com/ui/1.13.0/themes/base/jquery-ui.css">
</head>
<body>

<input type="text" id="myInput" onkeyup="myFunction()" placeholder="Search for names..">
<table id="myTable">
    <tr class="header">
        <th>Code</th>
        <th>Original</th>
        <th>New</th>
        <th>New</th>
        <th>Category</th>
        <th>Changes</th>
    </tr>

    <c:forEach items="${documents}" var="document">
        <tr>
            <td>
                    ${document.code}
            </td>
            <td>
                    ${document.original_name}
            </td>
            <td>
                    ${document.new_name}
            </td>
            <td>
                <c:forEach items="${document.tokens}" var="token">
                    <div class="keyword ${token.type}">
                        <a href="${token.id}">${token.id}</a>
                        <span class="addkeyword" onclick="updateToken('${token.id}', '${token.type}')">(+)</span>
                    </div>
                </c:forEach>
            </td>
            <td>
                    ${document.category}
            </td>
            <td>
                    ${document.brand}
            </td>
            <td>
                <ul>
                    <c:forEach items="${document.changes}" var="change">
                        <li>${change}</li>
                    </c:forEach>
                </ul>
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

    function updateToken(token, type) {
        if (type == 'UNDEFINED') {
            type = 'WORD';
        }
        if (type == 'UNDEFINED_ABBR') {
            type = 'FIXED_NAME';
        }
        $.ajax({
            url: "/keywords/token",
            data: JSON.stringify({id: token, type: type}),
            type: 'POST',
            contentType : 'application/json; charset=utf-8',
            dataType: 'json', // added data type
            success: function (res) {
                console.log(res);
            }
        });
    }

    $(function () {
        $("div.keyword").draggable(); // { revert: "invalid" }
        $("div.keyword").droppable({
            accept: "div.keyword",
            drop: function (event, ui) {
                var droppable = $(this);
                var draggable = ui.draggable;
                // Move draggable into droppable
                var keywordwrapper = droppable.parent();
                if (!keywordwrapper.hasClass('keywordwrapper')) {
                    droppable.wrap('<div class="keywordwrapper"></div>');
                    keywordwrapper = droppable.parent();
                    keywordwrapper.on("click", function () {
                        var sentence = "";
                        $(this).find("a").each(function (x, value) {
                            sentence += $(this).text() + " ";
                        })
                        sentence = sentence.trim();
                        updateToken(sentence, "SENTENCE");
                    });
                }
                draggable.appendTo(keywordwrapper);
                ;
                draggable.css({top: '0px', left: '0px'});
            }
        });
    });
</script>
<style>
    div.keywordwrapper {
        background-color: blue;
        display: inline-block;
        border-radius: 5px;
        padding: 3px;
        color: white;
        cursor: pointer;
    }

    div.keywordwrapper:after {
        content: ' (+) '
    }

    span.addkeyword {
        cursor: pointer;
    }
</style>
</body>
</html>