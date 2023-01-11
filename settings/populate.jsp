<!DOCTYPE html>
<%@ page language="java" contentType="text/html"%>
<%@ page import="model.Tenant,dao.TenantDao,java.util.List"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Setting Page</title>
    <link rel="stylesheet" href="../css/style.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <script src="../js/populate.js"></script>
</head>
<body>
    <%
        TenantDao tdao = TenantDao.getInstance();
        List<Tenant> tenants = tdao.getAllTenants();
    %>
    <div class="nav-bar flex" style="align-items: center;">
        <img src="../img/logo.png" style="height: 5rem;margin-left: 5px;">
        <div class="nav-title">EMail Traffic Generator</div>
        <div class="flex-right">
            <button class="nav-btn" onclick="location.href='/mailtraffic'">Home</button>
            <button class="nav-btn" onclick="location.href='../settings'">Settings</button>
        </div>
    </div>
    <div class="form-box">
        <div class="container">
            <div class="header-box">
                Populate Database
            </div>
            <form onsubmit="uploadData(event)">
                <label for="tenant">Tenant</label>
                <select name="tenant" id="tenant" class="form-input">
                    <option value="">Select One</option>
                    <%for(Tenant t: tenants){%>
                        <option value=<%=t.getId()%>><%=t.getName()%></option>
                    <%}%>
                </select>
                <label for="file">CSV File</label>
                <input type="file" id="file" name="file" class="form-input">
                <input type="submit" id="" class="form-submit-btn">
            </form>
        </div>
    </div>
    
</body>
</html>