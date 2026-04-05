<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>企业员工管理系统 - 首页</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <script src="https://cdn.jsdelivr.net/npm/jquery@3.6.0/dist/jquery.min.js"></script>
    <style>
        body { background: #f8f9fa; }
        .sidebar { min-height: 100vh; background: #f4f6fa; }
        .nav-link { color: #333; cursor: pointer; }
        .nav-link.active, .nav-link:hover { background: #e9ecef; color: #2196f3; }
        .topbar { background: #2196f3; color: #fff; padding: 12px 20px; }
        .avatar { width:32px; height:32px; border-radius:50%; }
        .main-content { padding: 0; height: calc(100vh - 56px); }
        iframe { width: 100%; height: 100%; min-height: 600px; background: #fff; border: none; }
    </style>
</head>
<body>
<div class="container-fluid p-0">
    <div class="row topbar align-items-center">
        <div class="col d-flex align-items-center">
            <button id="toggleSidebarMain" class="btn btn-sm btn-light mr-3" style="border-radius:4px;">
                ☰
            </button>
            企业员工管理系统
        </div>
        <div class="col text-right">
            <span id="userInfo" class="mr-3"></span>
            <a href="${pageContext.request.contextPath}/logout" class="text-white">退出登录</a>
        </div>
    </div>
    <div class="row no-gutters">
        <div class="col-2 sidebar">
            <ul class="nav flex-column mt-3" id="menuList">
                <li class="nav-item"><a class="nav-link active" href="#" onclick="loadPage('welcome.jsp', this)">首页</a></li>
                <!-- 管理员菜单 -->
                <li class="nav-item admin-menu"><a class="nav-link" href="#" onclick="loadPage('${pageContext.request.contextPath}/department/list', this)">部门管理</a></li>
                <li class="nav-item admin-menu"><a class="nav-link" href="#" onclick="loadPage('${pageContext.request.contextPath}/postlevel/list', this)">岗位管理</a></li>
                <li class="nav-item admin-menu"><a class="nav-link" href="#" onclick="loadPage('${pageContext.request.contextPath}/employee/page', this)">员工管理</a></li>
                <li class="nav-item admin-menu"><a class="nav-link" href="#" onclick="loadPage('${pageContext.request.contextPath}/attendance/list', this)">考勤管理</a></li>
                <li class="nav-item admin-menu"><a class="nav-link" href="#" onclick="loadPage('${pageContext.request.contextPath}/salary/list', this)">薪资管理</a></li>
                <li class="nav-item admin-menu"><a class="nav-link" href="#" onclick="loadPage('${pageContext.request.contextPath}/leave/page', this)">请假审批</a></li>
                <!-- 普通用户菜单 -->
                <li class="nav-item user-menu"><a class="nav-link" href="#" onclick="loadPage('${pageContext.request.contextPath}/attendance/list', this)">我的考勤</a></li>
                <li class="nav-item user-menu"><a class="nav-link" href="#" onclick="loadPage('${pageContext.request.contextPath}/leave/add', this)">请假申请</a></li>
                <li class="nav-item user-menu"><a class="nav-link" href="#" onclick="loadPage('${pageContext.request.contextPath}/leave/page', this)">我的请假</a></li>
            </ul>
        </div>
        <div class="col-10 main-content">
            <iframe id="mainFrame" src="welcome.jsp"></iframe>
        </div>
    </div>
</div>
<script>
function loadPage(url, el) {
    document.getElementById('mainFrame').src = url;
    $('.nav-link').removeClass('active');
    $(el).addClass('active');
}

$(document).ready(function() {
    // 侧边栏折叠/展开
    $('#toggleSidebarMain').on('click', function () {
        $('.sidebar').toggleClass('d-none');
        $('.main-content').toggleClass('col-10 col-12');
    });

    // 获取当前用户信息并根据权限显示菜单
    $.ajax({
        url: '${pageContext.request.contextPath}/getCurrentUser',
        type: 'GET',
        success: function(response) {
            if (response.code === 200) {
                var user = response.data.user;
                var isAdmin = response.data.isAdmin;
                
                // 显示用户信息
                $('#userInfo').html('欢迎，<strong>' + user.name + '</strong>' + 
                                   '<span class="badge badge-' + (isAdmin ? 'danger' : 'info') + ' ml-2">' + 
                                   (isAdmin ? '管理员' : '普通用户') + '</span>');
                
                // 根据权限显示菜单
                if (isAdmin) {
                    $('.admin-menu').show();
                    $('.user-menu').hide();
                } else {
                    $('.admin-menu').hide();
                    $('.user-menu').show();
                }
            } else {
                alert('获取用户信息失败：' + response.msg);
                window.location.href = '${pageContext.request.contextPath}/login';
            }
        },
        error: function() {
            alert('网络错误');
            window.location.href = '${pageContext.request.contextPath}/login';
        }
    });
});
</script>
</body>
</html> 