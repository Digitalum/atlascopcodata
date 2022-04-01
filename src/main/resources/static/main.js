$(document).ready(function () {
    //set initial state.
    $('#synonymtokenwrapper').hide();
    $('#invertSynonymwrapper').hide();
    $('#useAsSynonym').prop('checked', false);

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

function containsCharacter(value) {
    var regExp = /[a-zA-Z]/g;
    return regExp.test(value);
}

function containsNumber(value) {
    var regExp = /[0-9]/g;
    return regExp.test(value);
}

function confirmToken(tokens, tokenIds, type) {
    updateToken2(tokens, tokenIds, type, false);
    saveToken();
}

function updateToken(tokens, tokenIds, type) {
    updateToken2(tokens, tokenIds, type, true);
}
function updateToken2(tokens, tokenIds, type, showPopup) {
    var value = tokens.join('');
    var invertSynonym = false;
        if (containsNumber(value) && containsCharacter(value)) {
            type = 'FIXED_NAME';
        } else {
            type = 'WORD';
        }
    if (containsNumber(value) && containsCharacter(value)) {
        $('#synonymtoken').val(tokens.join(''));
        invertSynonym = false;
    } else {
        $('#synonymtoken').val(tokens.join(' '));
        invertSynonym = true;
    }

    currentTokens = tokens;
    currentTokenUuids = tokenIds;
    $('#popuptype').val(type);
    if (tokens.length > 1) {
        $('#popuptoken').val('[' + tokens.join('] [') + ']');
        $('#useAsSynonym').prop("disabled", true);
        $('#useAsSynonym').prop('checked', true)
        $('#invertSynonym').prop('checked', invertSynonym)
        $('#invertSynonymwrapper').show();
        $('#synonymtokenwrapper').show();
    } else {
        $('#synonymtoken').val("");
        $('#popuptoken').val(tokens.join());
        $('#popuptokenUuid').val(tokenIds.join());
        $('#useAsSynonym').prop('checked', false)
        $('#invertSynonym').prop('checked', false)
        $('#useAsSynonym').prop("disabled", false);
        $('#invertSynonymwrapper').show();
        $('#synonymtokenwrapper').show();
    }
    if (showPopup) {
        $('#exampleModalLong').modal('show');
    }
}

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
                            dataTable.ajax.reload( null, false );
                            $('#spinner-div').hide();//Request is complete so hide spinner
                        }
                    });
                } else {
                    dataTable.ajax.reload( null, false );
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
                dataTable.ajax.reload( null, false );
                $('#spinner-div').hide();//Request is complete so hide spinner
            }
        });
        $('#exampleModalLong').modal('hide');
    }
}


function addSearchFunctionality(that) {
    var api = that.api();
    // For each column
    api.columns().eq(0).each(function (colIdx) {
        // Set the header cell to contain the input element
        var cell = $('.filters th').eq($(api.column(colIdx).header()).index());
        var title = $(cell).text();
        $(cell).html('<input type="text" placeholder="' + title + '" />');
        // On every keypress in this input
        $('input', $('.filters th').eq($(api.column(colIdx).header()).index()))
            .on('keypress keyup', function (e) {
                e.stopPropagation();
                // Get the search value
                $(this).attr('title', $(this).val());
                var regexr = '{search}'; //$(this).parents('th').find('select').val();
                var cursorPosition = this.selectionStart;
                // Search the column for that value
                let search = api.column(colIdx).search(this.value != '' ? regexr.replace('{search}', this.value) : '', this.value != '', this.value == '');

                if (e.which == 13) {
                    search.draw();
                    $(this).focus()[0].setSelectionRange(cursorPosition, cursorPosition);
                }
            });
    });
}