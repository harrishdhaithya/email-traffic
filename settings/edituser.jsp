<%@ page import="model.Credential,dao.CredentialDao,java.util.List,model.Tenant,dao.TenantDao"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit User</title>
    <link rel="stylesheet" href="../css/style.css">
    <script src="../js/edituser.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
</head>
<body>
    <%
        CredentialDao cdao = CredentialDao.getInstance();
        String id = request.getParameter("id");
        if(id==null){
            response.sendRedirect("/mailtraffic/settings/viewcreds.jsp");
        }
        Credential cred = cdao.getCredential(Long.parseLong(id));
        TenantDao tdao = TenantDao.getInstance();
        List<Tenant> tenants = tdao.getAllTenants();
        if(tenants.size()==0){
            response.sendRedirect("/mailtraffic/settings/tenconf.jsp");
        }
    %>
    <div class="nav-bar flex" style="align-items: center;">
        <img src="../img/logo.png" style="height: 5rem;margin-left: 5px;">
        <div class="nav-title">EMail Traffic Generator</div>
        <div class="flex-right">
            <button class="nav-btn" onclick="location.href='/mailtraffic'">Home</button>
            <button class="nav-btn" onclick="location.href='../settings'">Settings</button>
            <button class="nav-btn" onclick="location.href='../settings/managecred.jsp'">Cancel</button>
        </div>
    </div>
    <div class="container">
        <div class="header-box">
            Update User
        </div>
        <form onsubmit="updateUser(event)">
            <input type="hidden" id="id" value=<%=cred.getId()%>>
            <label for="email" class="form-label">Email</label>
            <input type="email" name="email" id="email" class="form-input" value=<%=cred.getEmail()%> style="background-color:white" disabled>
            <label for="password" class="form-label">Password</label>
            <input type="password" name="password" id="password" class="form-input" value=<%=cred.getPassword()%>>
            <input type="checkbox" onclick="showpassword()">Show Password
            <div class="flex-center">
                <input type="submit" class="form-submit-btn" value="Update">
            </div>
        </form>
    </div>
</body>
</html>