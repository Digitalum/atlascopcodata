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
                "data": "id",
                render: function (data, type,  row, meta) {
                    return '<div class="keyword ' + row.type + '"><a href="/tokens/detail/' + data + '" >' + data + ' </a></div>';
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
            }
        ],
    })


});