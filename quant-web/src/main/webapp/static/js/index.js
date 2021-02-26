$(function () {
    var rankType = 2;
    var changeType = "0015";


    function getGridHedgeCount(){
        $.ajax({
            url: '/strategy/index/gridCount',
            type: 'post',
            // data: {id: id},
            dataType: 'json',
            success: function (data) {
                console.log(data);
                if (data.code == 200) {
                    //绘制对冲次数折线图
                    var times_count =[];
                    var counts = [];
                    $.each(data.data, function (n, value) {
                        times_count.push(getFormatDateByLong(value.time,"yyyy-MM-dd hh"));
                        counts.push(value.count);
                    });
                    printPublicHedgeCountLine(times_count,counts);
                } else {
                    parent.$.messager.alert('提示', data.message, 'info');
                }

            }
        });
    }

    function printPublicHedgeCountLine(times,counts){
        var myChart = echarts.init(document.getElementById('index_main_count'));
        // 指定图表的配置项和数据
        var option={
            //标题
            title:{
                text:'每日对冲统计'
            },
            //工具箱
            //保存图片
            toolbox:{
                show:true,
                feature:{
                    saveAsImage:{
                        show:true
                    }
                }
            },
            //图例-每一条数据的名字叫销量
            legend:{
                data:['对冲次数']
            },
            //x轴
            xAxis:{
                data:times
            },
            //y轴没有显式设置，根据值自动生成y轴
            yAxis:{},
            //数据-data是最终要显示的数据
            series:[{
                name:'次数',
                type:'line',
                data:counts
            }]
        };
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    }



    function getProfitRank(){
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
                    $.each(rankType == 1 ? data.dayRank : data.hisRank, function (n, value) {
                        html += '<tr>';
                        html += '<td width="100" align="center">' + value.userName + '</td>';
                        html += '<td width="100" align="center">' + value.strategyType + '</td>';
                        html += '<td width="100" align="center">' + value.profit + '</td>';
                        html += '<td width="100" align="center">' + getFormatDateByLong(value.displayTime, "hh:mm:ss") + '</td>';
                        html += '</tr>';
                    });
                    $('#rankTb').html(html);
                } else {
                    parent.$.messager.alert('提示', data.message, 'info');
                }

            }
        });
    }

    $('#profitRank').panel({
        onOpen: function () {
            getProfitRank();
            getGridHedgeCount();
            setInterval(getProfitRank, 30000)
        }
    });

    $('#rankSwitch').switchbutton({
        onChange: function (checked) {
            if (checked == true) {
                rankType = 2;
            } else {
                rankType = 2;
            }
            $('#profitRank').panel('open').panel('refresh');
        }

    });
    $('#changeSwitch').switchbutton({
        onChange: function (checked) {
            if (checked == true) {
                changeType = "005";
            } else {
                changeType = "0015";
            }
            $('#changeRank').panel('open').panel('refresh');
        }

    });

    function getChangeRank(){
        $.ajax({
            url: '/strategy/index/changeRank',
            type: 'post',
            // data: {id: id},
            dataType: 'json',
            success: function (data) {
                console.log(data);
                var list = data.changeRank0015;
                if (changeType == "005") {
                    list = data.changeRank005;
                }
                if (data.code == 200) {
                    var html = '';
                    html += '<thread>\n' +
                        '                    <tr>\n' +
                        '<th width="100" align="center">币种名称</th>\n' +
                        '<th width="100" align="center">波动次数</th>\n' +
                        '<th width="100" align="center">最新价格</th>\n' +
                        '<th width="100" align="center">更新时间</th>\n' +
                        '                    </tr>\n' +
                        '                </thread>';
                    $.each(list, function (n, value) {
                        html += '<tr>';
                        html += '<td width="100" align="center">' + value.currency + '</td>';
                        html += '<td width="100" align="center">' + value.changeCount + '</td>';
                        html += '<td width="100" align="center">' + value.basePrice + '</td>';
                        html += '<td width="100" align="center">' + getFormatDateByLong(value.updateTime, "hh:mm:ss") + '</td>';
                        html += '</tr>';
                    });
                    $('#changeTb').html(html);
                } else {
                    parent.$.messager.alert('提示', data.message, 'info');
                }

            }
        });
    }

    $('#changeRank').panel({
        onOpen: function () {
            getChangeRank();
            setInterval(getChangeRank, 30000)
        }
    });


    function getPremiumRank() {
        $.ajax({
            url: '/strategy/index/premiumRank',
            type: 'post',
            // data: {id: id},
            dataType: 'json',
            success: function (data) {
                console.log(data);
                if (data.code == 200) {
                    var html = '';
                    html += '<thread>\n' +
                        '                    <tr>\n' +
                        '<th width="100" align="center">多头合约</th>\n' +
                        '<th width="100" align="center">空头合约</th>\n' +
                        '<th width="100" align="center">溢价率</th>\n' +
                        '<th width="100" align="center">更新时间</th>\n' +
                        '                    </tr>\n' +
                        '                </thread>';
                    $.each(data.premiumRank, function (n, value) {
                        html += '<tr>';
                        html += '<td width="100" align="center">' + value.longSymbol + '</td>';
                        html += '<td width="100" align="center">' + value.shortSymbol + '</td>';
                        html += '<td width="100" align="center">' + value.premiumRate + '</td>';
                        html += '<td width="100" align="center">' + getFormatDateByLong(value.updateTime, "hh:mm:ss") + '</td>';
                        html += '</tr>';
                    });
                    $('#premiumTb').html(html);
                } else {
                    parent.$.messager.alert('提示', data.message, 'info');
                }

            }
        })
    }

    $('#premiumRank').panel({
        onOpen:
            function () {
                getPremiumRank();
                setInterval(getPremiumRank, 30000)
            }
    });


})
;