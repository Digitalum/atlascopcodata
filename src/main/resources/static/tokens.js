$(document).ready(function () {


    $('#tokenstable').DataTable({
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
                        a += ' <div class="synonynm">' + s + '</div>';
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
                            a += s + " ";
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
                        a +=  s.parent;
                        a += '</div>';
                    });

                    return a;
                }
            }
        ],
    })
});