$(function () {
    var rankType = 1;


    $('#profitRank').panel({
        onOpen: function () {
            $.ajax({
                url: '/strategy/index/profitRank',
                type: 'post',
                // data: {id: id},
                dataType: 'json',
                success: function (data) {
                    console.log(data);
                    if (data.code == 200) {
                        var html = '';
                        html += '<thread>\n' +
                            '                    <tr>\n' +
                            '<th width="100" align="center">账户名</th>\n' +
                            '<th width="100" align="center">策略类型</th>\n' +
                            '<th width="100" align="center">盈亏值(美金)</th>\n' +
                            '<th width="100" align="center">更新时间</th>\n' +
                            '                    </tr>\n' +
                            '                </thread>';
                        $.each(rankType==1?data.dayRank:data.hisRank, function (n, value) {
                            html += '<tr>';
                            html += '<td width="100" align="center">' + value.userName + '</td>';
                            html += '<td width="100" align="center">' + value.strategyType + '</td>';
                            html += '<td width="100" align="center">' + value.profit + '</td>';
                            html += '<td width="100" align="center">' + getFormatDateByLong(value.displayTime,"yyyy-MM-dd") + '</td>';
                            html += '</tr>';
                        });
                        $('#rankTb').html(html);
                    } else {
                        parent.$.messager.alert('提示', data.message, 'info');
                    }

                }
            });
        }
    });

    $('#rankSwitch').switchbutton({
        onChange: function (checked) {
            if (checked == true) {
                rankType = 1;
            } else {
                rankType = 2;
            }
            $('#profitRank').panel('open').panel('refresh');
        }

    });


});