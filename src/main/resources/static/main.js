
$(document).ready(function () {
    //set initial state.
    $('#synonymtokenwrapper').hide();
    $('#invertSynonymwrapper').hide();
    $('#useAsSynonym').prop('checked',false);

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