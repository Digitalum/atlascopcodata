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
            {
                "data": "original_name",
                render: function (originalname, type, row) {
                    return '<span onclick="updateDoc(\'' + originalname + '\' , \'' + row.code + '\')" > ' + originalname + '</span>';
                }
            },
            {"data": "new_name"},
            {
                "data": "tokens",
                render: function (tokens, type) {
                    var a = "";
                    $(tokens).each(function (index, token) {
                        a += '<div class="keyword ' + token.type + '"><a href="' + token.id + '">' + token.id + '</a>' +
                            '<span class="addkeyword" onclick="updateToken([\'' + token.id + '\'], \'' + token.type + '\')">(+)</span> </div>';
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
                        var sentence = [];
                        $(this).find("a").each(function (x, value) {
                            sentence.push($(this).text());
                        })
                        updateToken(sentence, "WORD");
                    });
                }
                draggable.appendTo(keywordwrapper);
                draggable.css({top: '0px', left: '0px'});
            }
        });
    });
}

function deleteCurrentToken() {
    deleteToken(decodeURI(window.location.pathname.substring(window.location.pathname.lastIndexOf('/') + 1) + ''));
}

function deleteToken(token) {
    $('#spinner-div').show()
    $.ajax({
        url: "/tokens/token/delete",
        data: JSON.stringify({id: null, code: token}),
        type: 'POST',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json', // added data type
        success: function (res) {
            console.log(res);
        },
        complete: function () {
            $('#spinner-div').hide();//Request is complete so hide spinner
        }
    });
}

var currentTokens = [];

function updateDoc(tokenCode, productCode) {
    $('#originalname').val(tokenCode);
    $('#productcode').val(productCode);
    $('#documentModel').modal('show');
}

function updateToken(tokens, type) {
    if (type == 'UNDEFINED') {
        type = 'WORD';
    }
    if (type == 'UNDEFINED_ABBR') {
        type = 'FIXED_NAME';
    }
    currentTokens = tokens;
    $('#popuptype').val(type);
    if (tokens.length > 1) {
        $('#popuptoken').val('[' + tokens.join('] [') + ']');
        $('#synonymtoken').val(tokens.join(' '));
        $('#useAsSynonym').prop('checked', true)
        $('#useAsSynonym').prop("disabled", true);
        $('#synonymtokenwrapper').show();
    } else {
        $('#popuptoken').val(tokens.join());
        $('#useAsSynonym').prop('checked', false)
        $('#useAsSynonym').prop("disabled", false);
        $('#synonymtokenwrapper').show();
    }
    $('#exampleModalLong').modal('show');
}

$(document).ready(function () {
    //set initial state.
    $('#useAsSynonym').val(false);
    $('#synonymtokenwrapper').hide();

    $('#useAsSynonym').change(function () {
        if (this.checked) {
            $('#synonymtokenwrapper').show();
        } else {
            $('#synonymtokenwrapper').hide();
        }
    });

    $("#synonymtoken").autocomplete({
        source: '/tokens/suggest',
    });
});


function saveToken() {
    if ($('#useAsSynonym').prop('checked')) {
        $('#spinner-div').show();
        $.ajax({
            url: "/tokens/tokengroup/add",
            data: JSON.stringify({
                parent: $('#synonymtoken').val(),
                parentType: 'WORD',
                tokens: currentTokens
            }),
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json', // added data type
            success: function (res) {
                console.log(res);
            },
            complete: function () {
                $('#spinner-div').hide();//Request is complete so hide spinner
            }
        });
        $('#exampleModalLong').modal('hide');
    } else {
        $('#spinner-div').show();
        $.ajax({
            url: "/tokens/token",
            data: JSON.stringify({id: null, code: $('#popuptoken').val(), type: $('#popuptype').val()}),
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json', // added data type
            success: function (res) {
                console.log(res);
            },
            complete: function () {
                $('#spinner-div').hide();//Request is complete so hide spinner
            }
        });
        $('#exampleModalLong').modal('hide');
    }
}

function saveDocument() {
    $('#spinner-div').show();
    $.ajax({
        url: "/document/update",
        data: JSON.stringify({
            code: $('#productcode').val(),
            original_name: $('#originalname').val(),
        }),
        type: 'POST',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json', // added data type
        success: function (res) {
            console.log(res);
        },
        complete: function () {
            $('#spinner-div').hide();//Request is complete so hide spinner
        }
    });
    $('#documentModel').modal('hide');

}