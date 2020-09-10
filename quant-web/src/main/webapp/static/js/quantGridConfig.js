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
    return $.formatString('<a href="javascript:void(0)" data-options="plain:true,iconCls:\'fi-page-edit icon-blue\'" onclick="getGridStats(\'{0}\');" >统计详情</a>', row.id);
}

/**
 * 打开统计信息详情
 */
function getGridStats(id) {
    $('#tbody').text('');
    $.ajax({
        url: '/strategy/quantGridConfig/stats',
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
                            times.push(value.displayTime);
                            profits.push(value.profit);
                        });
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
                });

            } else {
                parent.$.messager.alert('提示', data.message, 'info');
            }

        }
    });


}

/**
 *
 */
function searchQuantGridConfig() {

    $('#quantGridConfigDataGrid').datagrid('reload', {
        // roleName:$('#s_roleName').val()
    });
}

var url;

/**
 * 打开新增角色窗口
 */
function openQuantGridConfigAddDialog() {
    $('#editQuantGridConfig').dialog({
        title: '添加配置',
        iconCls: 'add',
        closed: false,
        top: $(window).height() / 4,
        width: 450,
        height: 400,
        onClose: function () {
            // $('#name').val('');
            // $('#remarks').val('');
        }
    });

    url = "/strategy/quantGridConfig/save";
}

/**
 * 打开修改角色窗口
 */
function openQuantGridConfigModifyDialog() {
    var selections = $('#quantGridConfigDataGrid').datagrid('getSelections');
    if (selections.length < 1) {
        $.messager.alert({
            title: '系统提示',
            msg: '请选择一条您要修改的记录',
            icon: 'error',
            top: $(window).height() / 4
        });
        return;
    }
    //加载数据至表单
    $('#quantGridConfigForm').form('load', selections[0]);
    //设置窗口相关属性，并打开
    $('#editQuantGridConfig').dialog({
        title: '修改配置',
        iconCls: 'update',
        closed: false,
        top: $(window).height() / 4,
        width: 450,
        height: 400,
        onClose: function () {
            // $('#name').val('');
            // $('#remarks').val('');
        }
    });

    url = "/strategy/quantGridConfig/save?id=" + selections[0].id;
}

/**
 * 关闭窗口
 */
function closeDlg() {
    // $('#name').val('');
    // $('#remarks').val('');
    $('#editQuantGridConfig').dialog('close');
}

// $(function(){
// 	// 数据表格加载完毕后，绑定双击打开修改窗口事件
// 	$('#dg').datagrid({
// 		onDblClickRow: function(index, row) {
// 			// 加载数据至表单
// 			$('#fm').form('load', row);
// 			$('#dlg').dialog({
// 				title: '修改角色',
// 				iconCls: 'update',
// 				closed: false,
// 				top:$(window).height()/4,
// 				width: 450,
// 				height: 250,
// 				onClose: function() {
// 					$('#name').val('');
// 					$('#remarks').val('');
// 				}
// 			});
//
// 			url="/role/save?roleId=" + row.roleId;
// 		}
// 	})
// });

/**
 * 保存角色信息
 */
function saveData() {
    $('#quantGridConfigForm').form('submit', {
        url: url,
        onSubmit: function () {
            //提交前可进行参数检验
            return true;
        },
        success: function (result) {
            console.log(result);
            var resultJson = eval('(' + result + ')');
            if (resultJson.code === 100) {
                $.messager.alert({
                    title: '系统提示',
                    msg: '操作成功',
                    icon: 'info',
                    top: $(window).height() / 4
                });
                $('#editQuantGridConfig').dialog('close');
                $('#quantGridConfigDataGrid').datagrid('reload');
            } else {
                $.messager.alert({
                    title: '系统提示',
                    msg: resultJson.msg,
                    icon: 'error',
                    top: $(window).height() / 4
                });
            }
        }
    })
}

/**
 * 删除
 */
function deleteQuantGridConfig() {
    var selections = $('#quantGridConfigDataGrid').datagrid('getSelections');
    if (selections.length < 1) {
        $.messager.alert({
            title: '系统提示',
            msg: '请选择一条您要删除的记录',
            icon: 'error',
            top: $(window).height() / 4
        });
        return;
    }
    $.messager.confirm({
        title: '系统提示',
        msg: '您确定要删除这条记录吗？',
        top: $(window).height() / 4,
        fn: function (r) {
            if (r) {
                $.ajax({
                    url: '/strategy/quantGridConfig/delete',
                    dataType: 'json',
                    type: 'post',
                    data: {
                        'id': selections[0].id
                    },
                    success: function (result) {
                        if (result.code === 100) {
                            $.messager.alert({
                                title: '系统提示',
                                msg: '删除成功',
                                icon: 'info',
                                top: $(window).height() / 4
                            });
                            $('#quantGridConfigDataGrid').datagrid('reload');
                        } else {
                            $.messager.alert({
                                title: '系统提示',
                                msg: result.msg,
                                icon: 'error',
                                top: $(window).height() / 4
                            });
                        }
                    }
                });
            }
        }
    })
}


//清除sessionStorage
// sessionStorage.removeItem("roleId");
