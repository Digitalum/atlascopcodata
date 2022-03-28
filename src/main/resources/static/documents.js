$(document).ready(function () {
    var token = window.location.pathname.substring(window.location.pathname.lastIndexOf('/') + 1);

    $('#documentstable').DataTable({
        processing: true,
        serverSide: true,
        search: {
            return: true
        },
        ajax: {
            url: '/api2/documents/' + token,
            dataSrc: 'page.content',
            dataType: "json",
            type: "POST",
            contentType: "application/json",
            dataFilter: function (data) {
                var json = jQuery.parseJSON(data);
                console.log(json.page.totalElements);
                json.recordsTotal = json.page.totalElements;
                json.recordsFiltered = json.page.totalElements;
                json.data = json.page.content;
                return JSON.stringify(json); // return JSON string
            },
            "data": function (d) {
                return JSON.stringify(d);
            }
        },

        "columns": [
            {"data": "code"},
            {"data": "original_name"},
            {"data": "new_name"},
            {
                "data": "tokens",
                render: function (tokens, type) {
                    var a = "";
                    $(tokens).each(function (index, token) {
                        a += '<div class="keyword ' + token.type + '"><a href="' + token.id + '">' + token.id + '</a>' +
                            '<span class="addkeyword" onclick="updateToken(\'' + token.id + '\', \'' + token.type + '\')">(+)</span> </div>';
                    });

                    return a;
                }
            },
            {"data": "category"},
            {"data": "brand"},
            {"data": "changes"}],
        "drawCallback": function (settings) {
            reloadDragg();
        }

    })
});

function reloadDragg() {
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
}


function updateToken(token, type) {
    if (type == 'UNDEFINED') {
        type = 'WORD';
    }
    if (type == 'UNDEFINED_ABBR') {
        type = 'FIXED_NAME';
    }
    $('#popuptoken').text(token);
    $('#popuptype').val(type);
    $('#exampleModalLong').modal('show');
}

function saveToken() {
    if ("SYNONYM" == ($('#popuptype').val())) {
        $.ajax({
            url: "/keywords/synonym",
            data: JSON.stringify({
                id: $('#synonymtoken').val(),
                type: $('#popuptype').val(),
                synonyms: [$('#popuptoken').text()]
            }),
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json', // added data type
            success: function (res) {
                console.log(res);
            }
        });
        $('#exampleModalLong').modal('hide');
    } else {
        $.ajax({
            url: "/keywords/token",
            data: JSON.stringify({id: $('#popuptoken').text(), type: $('#popuptype').val()}),
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json', // added data type
            success: function (res) {
                console.log(res);
            }
        });
        $('#exampleModalLong').modal('hide');
    }
}