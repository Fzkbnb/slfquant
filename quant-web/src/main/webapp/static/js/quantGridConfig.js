


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
