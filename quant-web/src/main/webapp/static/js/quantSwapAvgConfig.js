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



/**
 *
 */
function searchQuantSwapAvgConfig() {

    $('#quantSwapAvgConfigDataGrid').datagrid('reload', {
        // roleName:$('#s_roleName').val()
    });
}

var url;

/**
 * 打开新增角色窗口
 */
function openQuantSwapAvgConfigAddDialog() {
    $('#editQuantSwapAvgConfig').dialog({
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

    url = "/strategy/quantSwapAvgConfig/save";
}

/**
 * 打开修改角色窗口
 */
function openQuantSwapAvgConfigModifyDialog() {
    var selections = $('#quantSwapAvgConfigDataGrid').datagrid('getSelections');
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
    $('#quantSwapAvgConfigForm').form('load', selections[0]);
    //设置窗口相关属性，并打开
    $('#editQuantSwapAvgConfig').dialog({
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

    url = "/strategy/quantSwapAvgConfig/save?id=" + selections[0].id;
}

/**
 * 关闭窗口
 */
function closeDlg() {
    // $('#name').val('');
    // $('#remarks').val('');
    $('#editQuantSwapAvgConfig').dialog('close');
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
    $('#quantSwapAvgConfigForm').form('submit', {
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
                $('#editQuantSwapAvgConfig').dialog('close');
                $('#quantSwapAvgConfigDataGrid').datagrid('reload');
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
function deleteQuantSwapAvgConfig() {
    var selections = $('#quantSwapAvgConfigDataGrid').datagrid('getSelections');
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
                    url: '/strategy/quantSwapAvgConfig/delete',
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
                            $('#quantSwapAvgConfigDataGrid').datagrid('reload');
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
