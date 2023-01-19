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
    <script src="../js/mailtraffic.js"></script>
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
                Daily Traffic Schedule
            </div>
            <form onsubmit="generateTraffic(event)">
                <label for="schedulename">Enter Yout Schedule Identifier:</label>
                <input type="text" name="schedulename" id="schedulename" class="form-input">
                <label for="time">Time:</label>
                <input type="time" name="time" id="time" class="form-input">
                <label for="tenant">Select Tenant:</label>
                <select name="tenant" id="tenant" class="form-input">
                    <option value="">Select One</option>
                    <%for(Tenant t: tenants){%>
                        <option value=<%=t.getId()%>><%=t.getName()%></option>
                    <%}%>
                </select>
                <label for="count">Enter Number of Mails:</label>
                <input type="number" name="count" id="count" class="form-input" onchange="observeCount()">
                <input type="submit" class="form-submit-btn">
            </form>
        </div>
    </div>
</body>
</html>