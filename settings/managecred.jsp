<%@ page import="model.Tenant,dao.TenantDao,java.util.List,dao.CredentialDao, model.Credential"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Credentials</title>
    <link rel="stylesheet" href="../css/style.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
    <script src="../js/managecred.js"></script>
</head>
<body>
    <div class="nav-bar flex" style="align-items: center;">
        <img alt="logo Image" src="../img/logo.png" style="height: 5rem;margin-left: 5px;">
        <div class="nav-title">EMail Traffic Generator</div>
        <div class="flex-right">
            <button class="nav-btn" onclick="location.href='/mailtraffic'">Home</button>
            <button class="nav-btn" onclick="location.href='../settings'">Back</button>
        </div>
    </div>
    <%
        TenantDao tdao = TenantDao.getInstance();
        List<Tenant> tenants = tdao.getAllTenants();
        CredentialDao cdao = CredentialDao.getInstance();
    %>
    <div class="form-box">
        <div class="container-wide">
            <div class="header-box">
                Manage Credentials
            </div>
            <form onsubmit="getCredentials(event)">
                <label for="tenant" class="form-label">Select Tenant  </label>
                <select name="tenant" id="tenant" class="form-input">
                    <option value="">Select Tenant</option>
                    <%for(Tenant t:tenants){%>
                        <option value=<%=t.getId()%>><%=t.getName()%></option>
                    <%}%>
                </select>
                <input type="submit" name="submitd=" class="form-submit-btn">
            </form>
            <div class="table-container hidden" id="table-container">
                <table>
                    <thead>
                        <th>Email ID</th>
                        <!-- <th>Password</th> -->
                        <th>Status</th>
                        <th>Update</th>
                        <th>Delete</th>
                    </thead>
                    <tbody id="table-body">
                    </tbody>
                </table>
                <div id="total-data"></div>
                <div id="pageinfo"></div>
                <div class="pagination-container">
                    <div class="pagination">
                        <button onclick="firstPage(event)" id="firstpage">&laquo;&laquo;</button>
                        <button onclick="prevPage(event)" id="prevbtn">&laquo;</button>
                        <div id="pagenum">1</div>
                        <button onclick="nextPage(event)" id="nextbtn">&raquo;</button>
                        <button onclick="lastPage(event)" id="lastpage">&raquo;&raquo;</button>
                    </div>
                </div>
            </div>
        </div>
        
    </div>
    
</body>
</html>
