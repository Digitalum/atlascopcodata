var dataTable;



$(document).ready(function () {
    var token = window.location.pathname.substring(window.location.pathname.lastIndexOf('/') + 1);

    if (window.location.pathname == '/documents') {
        token = "";
    }

    $('#documentstable thead tr')
        .clone(true)
        .addClass('filters')
        .appendTo('#documentstable thead');

    dataTable = $('#documentstable').DataTable({
        iDisplayLength: 25,
        lengthMenu: [[10, 25, 50, 100, 250, 1000], [10, 25, 50, 100, 250, 1000]],
        processing: true,
        serverSide: true,
        orderCellsTop: true,
        fixedHeader: true,
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
                json.recordsTotal = json.page.totalElements;
                json.recordsFiltered = json.page.totalElements;
                json.data = json.page.content;
                return JSON.stringify(json); // return JSON string
            },
            "data": function (d) {
                return JSON.stringify(d);
            }
        },
        "columns": getColumns(),
        columnDefs: [{
            targets: 4,
            createdCell: function (td, cellData, rowData, row, col) {
                if (rowData.completelyTokenized) {
                    $(td).css('background-color', '#8bc34a');
                    $(td).css('color', 'white');
                }
            }
        }],
        "drawCallback": function (settings) {
            reloadDragg();
        },
        initComplete: function () {
            addSearchFunctionality(this);
        },
    });

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

function updateDoc(value, productCode) {
    $('#originalname').val(decodeURI(value));
    $('#productcode').val(productCode);
    $('#documentModel').modal('show');
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

            dataTable.ajax.reload( null, false );
            $('#spinner-div').hide();//Request is complete so hide spinner
        }
    });
    $('#documentModel').modal('hide');

}

function getColumns() {
    return [
        {"data": "code"},
        {
            "data": "original_name",
            render: function (originalname, type, row) {
                return '<span onclick="updateDoc(\'' + encodeURI(originalname) + '\' , \'' + row.code + '\')" > ' + originalname + '</span>';
            }
        },
        {"data": "new_name"},
        {
            "data": "tokens",
            render: function (tokens, type) {
                var a = "";
                $(tokens).each(function (index, token) {
                    a += '<div class="keyword ' + token.type + '"><a href="/tokens/detail/' + token.uuid + '">' + token.code + '</a>' +
                        '<span class="addkeyword" uuid="' + token.uuid + '" onclick="updateToken([\'' + token.code + '\'],[\'' + token.uuid + '\'], \'' + token.type + '\')"> <i class="fa-solid fa-pen"></i></span> ' +
                        '<span class="addkeyword" uuid="' + token.uuid + '" onclick="confirmToken([\'' + token.code + '\'],[\'' + token.uuid + '\'], \'' + token.type + '\')"> <i class="fa-solid fa-check-double"></i></span> ' +
                        '</div>';
                });

                return a;
            }
        },
        {"data": "newNameTranslated"},
        {"data": "completelyTokenized"},
        {"data": "category"},
        {"data": "brand"},
        {"data": "changes"}];
}