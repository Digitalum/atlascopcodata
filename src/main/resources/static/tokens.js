var dataTable;

function getColumns() {
    return [
        {
            "data": "uuid",
            render: function (uuid, type, token, meta) {
                a = '<div class="keyword ' + token.type + '"><a href="/tokens/detail/' + token.uuid + '">' + token.code + '</a>' +
                    '<span class="addkeyword" uuid="' + token.uuid + '" onclick="updateToken([\'' + token.code + '\'],[\'' + token.uuid + '\'], \'' + token.type + '\')"> <i class="fa-solid fa-pen"></i></span> ' +
                    '<span class="addkeyword" uuid="' + token.uuid + '" onclick="confirmToken([\'' + token.code + '\'],[\'' + token.uuid + '\'], \'' + token.type + '\')"> <i class="fa-solid fa-check-double"></i></span> ' +
                    '</div>';

                return a;//'<div class="keyword ' + row.type + '"><a href="/tokens/detail/' + uuid + '" >' + row.code + ' </a></div>';
            }
        },
        {"data": "documentCount"},
        {"data": "type"},
        {
            data: "synonyms",
            render: function (data, type) {
                var a = "";
                $(data).each(function (index, s) {
                    a += ' <div class="synonynm">' + s.code + '</div>';
                });

                return a;
            }
        },
        {
            data: "synonymParents",
            render: function (data, type) {
                var a = "";
                $(data).each(function (index, s) {
                    a += ' <div class="synonynmgroup">'
                    $(s.tokens).each(function (index, s) {
                        a += ' <div class="synonynm">'
                        a += s.code + " ";
                        a += '</div>';
                    });
                    a += '</div>'
                });
                return a;
            }
        },
        {
            data: "synonymGroups",
            render: function (data, type) {
                var a = "";
                $(data).each(function (index, s) {
                    a += ' <div class="synonynm">'
                    a += s.parent.code;
                    a += '</div>';
                });

                return a;
            }
        }
    ];
}

$(document).ready(function () {
    $('#tokenstable thead tr')
        .clone(true)
        .addClass('filters')
        .appendTo('#tokenstable thead');

    dataTable = $('#tokenstable').DataTable({
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
            url: '/api2/tokens',
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
        initComplete: function () {
            addSearchFunctionality(this);
        },
    })


});
