$(document).ready(function () {
    var token = window.location.pathname.substring(window.location.pathname.lastIndexOf('/') + 1);

    if (window.location.pathname == '/documents') {
        token = "";
    }

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
                    return '<span onclick="updateDoc(\'' + originalname + '\' , \'' + row.uuid + '\')" > ' + originalname + '</span>';
                }
            },
            {"data": "new_name"},
            {
                "data": "tokens",
                render: function (tokens, type) {
                    var a = "";
                    $(tokens).each(function (index, token) {
                        a += '<div class="keyword ' + token.type + '"><a href="/tokens/detail/' + token.uuid + '">' + token.code + '</a>' +
                            '<span class="addkeyword" uuid="' + token.uuid + '" onclick="updateToken([\'' + token.code + '\'],[\'' + token.uuid + '\'], \'' + token.type + '\')"> <i class="fa-solid fa-pen"></i></span> </div>';
                    });

                    return a;
                }
            },
            {"data": "newNameTranslated"},
            {"data": "completelyTokenized"},
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
                        var tokenIds = [];
                        $(this).find("a").each(function (x, value) {
                            sentence.push($(this).text());
                            tokenIds.push($(this).text());
                        })
                        updateToken(sentence, tokenIds, "WORD");
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
        data: JSON.stringify({uuid: token}),
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
var currentTokenUuids = [];

function updateDoc(tokenCode, productCode) {
    $('#originalname').val(tokenCode);
    $('#productcode').val(productCode);
    $('#documentModel').modal('show');
}

function updateToken(tokens, tokenIds, type) {
    if (type == 'UNDEFINED') {
        type = 'WORD';
    }
    if (type == 'UNDEFINED_ABBR') {
        type = 'FIXED_NAME';
    }
    currentTokens = tokens;
    currentTokenUuids = tokenIds;
    $('#popuptype').val(type);
    if (tokens.length > 1) {
        $('#popuptoken').val('[' + tokens.join('] [') + ']');
        $('#synonymtoken').val(tokens.join(' '));
        $('#useAsSynonym').prop('checked', true)
        $('#invertSynonym').prop('checked', true)
        $('#useAsSynonym').prop("disabled", true);
        $('#invertSynonymwrapper').show();
        $('#synonymtokenwrapper').show();
    } else {
        $('#popuptoken').val(tokens.join());
        $('#popuptokenUuid').val(tokenIds.join());
        $('#useAsSynonym').prop('checked', false)
        $('#invertSynonym').prop('checked', false)
        $('#useAsSynonym').prop("disabled", false);
        $('#invertSynonymwrapper').show();
        $('#synonymtokenwrapper').show();
    }
    $('#exampleModalLong').modal('show');
}

$(document).ready(function () {
    //set initial state.
    $('#useAsSynonym').val(false);
    $('#synonymtokenwrapper').hide();
    $('#invertSynonymwrapper').hide();

    $('#useAsSynonym').change(function () {
        if (this.checked) {
            $('#synonymtokenwrapper').show();
            $('#invertSynonymwrapper').show();
        } else {
            $('#synonymtokenwrapper').hide();
            $('#invertSynonymwrapper').hide();
        }
    });

    $("#synonymtoken").autocomplete({
        source: '/tokens/suggest',
    });
});


function saveToken() {
    if ($('#useAsSynonym').prop('checked')) {
        $('#spinner-div').show();
        var tokenDtos = [];
        for (const currentToken of currentTokens) {
            tokenDtos.push({
                code: currentToken
            });
        }
        $.ajax({
            url: "/tokens/tokengroup/add",
            data: JSON.stringify({
                parent: {
                    code: $('#synonymtoken').val(),
                    type: $('#popuptype').val()
                },
                tokens: tokenDtos
            }),
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json', // added data type
            success: function (res) {
                console.log(res);
            },
            complete: function () {
                if ($('#invertSynonym').prop('checked')) {
                    $.ajax({
                        url: "/tokens/tokengroup/add",
                        data: JSON.stringify({
                            parent: {
                                code: $('#synonymtoken').val(),
                                type: $('#popuptype').val()
                            },
                            tokens: tokenDtos.reverse()
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
                } else {
                    $('#spinner-div').hide();//Request is complete so hide spinner
                }
            }
        });


        $('#exampleModalLong').modal('hide');

    } else {
        $('#spinner-div').show();
        $.ajax({
            url: "/tokens/token",
            data: JSON.stringify({
                    uuid: $('#popuptokenUuid').val(),
                    code: $('#popuptoken').val(),
                    type: $('#popuptype').val()
                }
            ),
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