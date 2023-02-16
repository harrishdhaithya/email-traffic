<%@ page import="model.Tenant,dao.TenantDao,java.util.List,messagetrace.MessageTrace"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>History</title>
    <link rel="stylesheet" href="../css/style.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <script src="../js/traffichist.js"></script>
</head>
<body>
    <div class="nav-bar flex" style="align-items: center;">
        <img src="../img/logo.png" style="height: 5rem;margin-left: 5px;">
        <div class="nav-title">EMail Traffic Generator</div>
        <div class="flex-right">
            <button class="nav-btn" onclick="location.href='/mailtraffic'">Home</button>
        </div>
        
    </div>
    <%
        TenantDao tdao = TenantDao.getInstance();
        List<Tenant> tenants = tdao.getAllTenants();
    %>
    <div class="form-box">
        <div class="container-wide">
            <div class="header-box">
                Traffic History
            </div>
            <form onsubmit="getHistory(event)">
                <label for="tenant" class="form-label">Choose Tenant</label>
                <select name="tenant" id="tenant" class="form-input">
                    <option value="">All Tenants</option>
                    <%for(Tenant t:tenants){%>
                        <option value=<%=t.getId()%>><%=t.getName()%></option>
                    <%}%>
                </select>
                <input type="submit" value="Get History" class="form-submit-btn">
            </form>
            <div class="table-container hidden" id="table-container">
                <table style=" width: 100%;overflow-wrap: break-word;">
                    <thead>
                        <th>Success Count</th>
                        <th>Failure Count</th>
                        <th>Total Count</th>
                        <th>Tenant ID</th>
                        <th>Start Time</th>
                        <th>End Time</th>
                    </thead>
                    <tbody id="table-body">

                    </tbody>
                </table>
                
            </div>
        </div>
        
    </div>
</body>
</html>