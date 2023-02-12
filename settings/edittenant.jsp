<%@ page import="model.Tenant,dao.TenantDao"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Setting Page</title>
    <link rel="stylesheet" href="../css/style.css">
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <script src="../js/edittenant.js"></script>
</head>
<body>
    <%
        TenantDao tdao = TenantDao.getInstance();
        String tenantid = request.getParameter("id");
        Tenant t = null;
        if(tenantid==null||(t=tdao.getTenant(Long.parseLong(tenantid)))==null){
            response.sendRedirect("/mailtraffic/settings/tenconf.jsp");
            return;
        }
    %>
    <div class="nav-bar flex" style="align-items: center;">
        <img src="../img/logo.png" style="height: 5rem;margin-left: 5px;">
        <div class="nav-title">EMail Traffic Generator</div>
        <div class="flex-right">
            <button class="nav-btn" onclick="location.href='/mailtraffic'">Home</button>
            <button class="nav-btn" onclick="location.href='../settings'">Settings</button>
            <button class="nav-btn" onclick="location.href='../settings/tenconf.jsp'">Back</button>
        </div>
    </div>
    <div class="form-box">
        <div class="container">
            <div class="header-box">
                Update Tenant
            </div>
            <form onsubmit="updateTenant(event)">
                <input type="hidden" id="id" name="id" value=<%=t.getId()%>>
                <label for="tenant" class="form-label">Enter Tenant Name: </label>
                <input type="text" name="tenant" id="tenant" class="form-input" placeholder="Tenant Name" value=<%=t.getName()%>>
                <label for="app_clientid" class="form-label">Enter Application Client ID: </label>
                <input type="text" name="app_clientid" id="app_clientid" class="form-input" placeholder="Client ID" value=<%=t.getAppClientId()%>>
                <label for="admin_email" class="form-label">Enter Admin Email:<i class="fa fa-info-circle admin-grp" aria-hidden="true"></i></label>
                <input type="text" name="admin_email" id="admin_email" class="form-input" placeholder="Admin Email" value=<%=t.getAdminEmail()%>>
                <label for="admin_email" class="form-label">Enter Admin Password:<i class="fa fa-info-circle admin-grp" aria-hidden="true"></i> </label>
                <input type="password" name="admin_password" id="admin_password" class="form-input" placeholder="Admin Password" value=<%=t.getAdminPassword()%>>
                <input type="checkbox" onclick="showpassword()">Show Password
                <input type="submit" name="" id="" class="form-submit-btn">
            </form>
        </div>
    </div>
    
</body>
</html>