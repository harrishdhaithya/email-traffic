<!DOCTYPE html>
<%@ page import="model.Tenant,dao.TenantDao,java.util.List,org.json.*,dao.CredentialDao"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tenant Configuration</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <link rel="stylesheet" href="../css/style.css">
    <script src="../js/tenconf.js"></script>
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
            <button class="nav-btn" onclick="location.href='../settings'">Back</button>
        </div>
    </div>
    <div class="container-wide">
        <div class="header-box">
            Available Tenants
        </div>
        <div class="table-container">
        <%if(tenants==null||tenants.size()==0){%>
            <div class="fs-2">No Tenants Found</div>
        <%}else{%>
            <table>
                <tr>
                    <th>Name</th>
                    <th>Sender Count</th>
                    <th>Receiver Count</th>
                    <th>Delete</th>
                    <th>Edit</th>
                </tr>
                <%for(Tenant t:tenants){%>
                    <%
                        CredentialDao cdao = CredentialDao.getInstance();
                        JSONObject jobj = cdao.getSenderAndRecCount(t.getId());
                    %>
                        <tr>
                            <td><%=t.getName()%></td>
                            <td><%=jobj.getInt("sender")%></td>
                            <td><%=jobj.getInt("receiver")%></td>
                            <td>
                                <button style="background-color: red;color: white;" value=<%=t.getId()%> onclick="deleteTen(event)">Delete</button>
                            </td>
                            <td>
                                <button style="background-color: blue;color: white;" value=<%=t.getId()%> onclick="editTen(event)">Edit</button>
                            </td>
                        </tr>
                    <%}%>
            <%}%>
            </table>
            <button class="fs-2" style="background-color: green; margin-top: 5px; color: white; border-radius: 5px;" onclick="location.href='createtenant.html'">Add Tenant</button>   
        </div>
    </div>
</body>
</html>