<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>企业员工管理系统</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0;
            font-family: 'Microsoft YaHei', Arial, sans-serif;
        }
        .welcome-card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
            padding: 40px;
            text-align: center;
            max-width: 400px;
        }
        .logo {
            font-size: 2.5em;
            color: #667eea;
            margin-bottom: 20px;
        }
        .loading {
            display: inline-block;
            width: 20px;
            height: 20px;
            border: 3px solid #f3f3f3;
            border-top: 3px solid #667eea;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    </style>
</head>
<body>
    <div class="welcome-card">
        <div class="logo">🏢</div>
        <h1 style="color: #333; margin-bottom: 10px;">企业员工管理系统</h1>
        <p style="color: #666; margin-bottom: 30px;">正在初始化系统，请稍候...</p>
        <div class="loading"></div>
    </div>
    
    <div style="margin-top:25px;">
        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary" style="padding: 8px 24px; background:#667eea;border:none;border-radius:6px; color:#fff; text-decoration:none;">进入登录页</a>
    </div>
</body>
</html>
