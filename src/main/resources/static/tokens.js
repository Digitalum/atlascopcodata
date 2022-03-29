$(document).ready(function () {


    $('#tokenstable').DataTable({
        iDisplayLength: 25,
        lengthMenu: [[10, 25, 50, 100, 250, 1000], [10, 25, 50, 100, 250, 1000]],
        processing: true,
        serverSide: true,
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
            {
                "data": "uuid",
                render: function (uuid, type, row, meta) {
                    return '<div class="keyword ' + row.type + '"><a href="/tokens/detail/' + uuid + '" >' + row.code + ' </a></div>';
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
                data: "synonymTokenParents",
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
                data: "synonymsTokenGroups",
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
        ],
    })
});