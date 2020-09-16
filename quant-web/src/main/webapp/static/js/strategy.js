/**
 *
 * 增加formatString功能
 *
 * 使用方法：$.formatString('字符串{0}字符串{1}字符串','第一个变量','第二个变量');
 *
 * @returns 格式化后的字符串
 */
$.formatString = function (str) {
    for (var i = 0; i < arguments.length - 1; i++) {
        str = str.replace("{" + i + "}", arguments[i + 1]);
    }
    return str;
};

function printStatus(value, row, index) {

    if (value == 1) {
        return '<font color="green">运行中</font>';
    } else if (value == 2) {
        return '<font color="orange">运行异常</font>';
    } else if (value == 0) {
        return '<font color="red">已停止</font>';
    } else if (value == 3) {
        return '<font color="green">启动中</font>';
    } else if (value == 4) {
        return '<font color="red">停止中</font>';
    }
}

function printStats(value, row, index) {
    return $.formatString('<a href="javascript:void(0)" data-options="plain:true,iconCls:\'fi-page-edit icon-blue\'" onclick="getStrategyStats(\'{0}\');" >统计详情</a>', row.id);
}


/**
 * 打开统计信息详情
 */
function getStrategyStats(id) {
    $('#tbody').text('');
    $.ajax({
        url: '/strategy/index/stats',
        type: 'post',
        data: {id: id},
        dataType: 'json',
        success: function (data) {

            if (data.code == 200) {
                $('#statsTable').dialog({
                    title: '统计信息',
                    closed: false,
                    top: $(window).height() / 4,
                    width: 700,
                    height: 600,
                    onOpen: function () {
                        var html = '';
                        html += '<thread>\n' +
                            '                    <tr>\n' +
                            '<th width="200" align="center">参数名</th>\n' +
                            '<th width="200" align="center">参数值</th>\n' +
                            '                    </tr>\n' +
                            '                </thread>';
                        $.each(data.data, function (n, value) {
                            html += '<tr>';
                            html += '<td width="200" align="center">' + value.key + '</td>';
                            html += '<td width="200" align="center">' + value.value + '</td>';
                            html += '</tr>';
                        });

                        $('#tbody').html(html);
                        //绘制折线图
                        var times =[];
                        var profits = [];
                        $.each(data.profits, function (n, value) {
                            times.push(getFormatDateByLong(value.displayTime,"yyyy-MM-dd hh"));
                            profits.push(value.profit);
                        });
                        printProfitLine(times,profits);

                    }
                });

            } else {
                parent.$.messager.alert('提示', data.message, 'info');
            }

        }
    });


}


function printProfitLine(times,profits){
    var myChart = echarts.init(document.getElementById('main'));
    // 指定图表的配置项和数据
    var option={
        //标题
        title:{
            text:'盈亏统计'
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
            data:['盈亏值(usdt)']
        },
        //x轴
        xAxis:{
            data:times
        },
        //y轴没有显式设置，根据值自动生成y轴
        yAxis:{},
        //数据-data是最终要显示的数据
        series:[{
            name:'盈亏',
            type:'line',
            data:profits
        }]
    };
    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
}



//清除sessionStorage
// sessionStorage.removeItem("roleId");
