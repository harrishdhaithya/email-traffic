<%@ page import="model.Tenant,dao.TenantDao,java.util.List"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mail Traces</title>
    <link rel="stylesheet" href="../css/style.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
    <script src="../js/collecttraces.js"></script>
</head>
<body>
    <div class="nav-bar flex" style="align-items: center;">
        <img alt="logo Image" src="../img/logo.png" style="height: 5rem;margin-left: 5px;">
        <div class="nav-title">EMail Traffic Generator</div>
        <div class="flex-right">
            <button class="nav-btn" onclick="location.href='/mailtraffic'">Home</button>
            <button class="nav-btn" onclick="location.href='mailtraces.jsp'">Back</button>
        </div>
    </div>
    <%
        TenantDao tdao = TenantDao.getInstance();
        List<Tenant> tenants = tdao.getAllTenants();
    %>
    <div class="form-box">
        <div class="container-wide">
            <div class="header-box">
                Collect Traces
            </div>
            <form onsubmit="collectTraces(event)">
                <label for="tenant" class="form-label">Select Tenant  </label>
                <select name="tenant" id="tenant" class="form-input">
                    <option value="">Select Tenant</option>
                    <%for(Tenant t:tenants){%>
                        <option value=<%=t.getId()%>><%=t.getName()%></option>
                    <%}%>
                </select>
                <input type="submit" name="submitd=" class="form-submit-btn">
            </form>
        </div>
    </div>
</body>
</html>