<!DOCTYPE html>
<%@ page language="java" contentType="text/html"%>
<%@ page import="model.Tenant,dao.TenantDao,singleton.Singleton,java.util.List, controller.MailTrafficGenerator"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EMail Traffic Generator</title>
    <link rel="stylesheet" href="css/style.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <script src="js/script.js"></script>
</head>
<body>
    <%
        MailTrafficGenerator mtg = (MailTrafficGenerator)session.getAttribute("generator");
    %>
    <div class="nav-bar flex" style="align-items: center;">
        <img src="img/logo.png" style="height: 5rem;margin-left: 5px;">
        <div class="nav-title">EMail Traffic Generator</div>
        <div class="flex-right">
            <button class="nav-btn" onclick="location.href='settings'" >Settings</button>
            <%if(mtg!=null){%>
                <button class="nav-btn" onclick="location.href='mailstatus.html'" >View Status</button>
            <%}%>
            <button class="nav-btn" onclick="location.href='help.jsp'" >Help</button>
        </div>
    </div>
    <%
        TenantDao tdao = Singleton.getTenantDao();
        List<Tenant> tenants = tdao.getAllTenants();
    %>
    <div class="container">
        <div class="form-box">
            <div class="header-box">
                Email Traffic Generator
            </div>
            <form onsubmit="generateTraffic(event);" style="position: relative;margin-left: auto;margin-right: auto;">
                <label for="datasource" class="form-label">Enter Your Data Source</label>
                <select name="datasource" id="datasource" class="form-input" onchange="changeOpt();">
                    <option value="" selected>Select</option>
                    <option value="csv">CSV File</option>
                    <option value="sequence">Sequence</option>
                    <option value="db">Database</option>
                </select>
                <div id="csv-opt" class="hidden">
                    <label for="file" class="form-label">Upload File:</label>
                    <input type="file" name="file" id="file" class="form-input" placeholder="Upload File">
                    
                </div>
                <div id="seq-opt" class="hidden">
                    <label for="prefix" class="form-label">Enter the prefix of the Sequence</label>
                    <input type="text" name="prefix" id="prefix" class="form-input" placeholder="Prefix">
                    <label for="suffix" class="form-label">Enter the Suffix of the Sequence(Optional):</label>
                    <input type="text" name="suffix" id="suffix" class="form-input" placeholder="Suffix">
                    <label for="seqstart" class="form-label">Enter the Starting Number of the Sequence</label>
                    <input type="text" name="seqstart" id="seqstart" class="form-input" placeholder="Sequence Start">
                    <label for="seqend" class="form-label">Enter the Ending Number of the Sequence</label>
                    <input type="text" name="seqend" id="seqend" class="form-input" placeholder="Sequence End">
                    <label for="tenant" class="form-label">Tenant: </label>
                    <input type="text" name="tenant" id="tenant" class="form-input" placeholder="Tenant">
                    <label for="password" class="form-label">Password: </label>
                    <input type="password" name="password" id="password" class="form-input" placeholder="Password">
                </div>
                <div id="db-opt" class="hidden">
                    <label for="tenantid" class="form-label">Tenant: </label>
                    <select name="tenantid" id="tenantid" class="form-input" onchange="changeTenant()">
                        <option value="" selected>Select</option>
                        <%for(Tenant t: tenants){%>
                            <option value=<%=t.getId()%>><%=t.getName()%></option>
                        <%}%>
                    </select>
                    <div id="senders"></div>
                    <div id="recievers"></div>
                </div>
                <div id="common" class="hidden">
                    <label for="count" class="form-label">Enter Number of Mails to be sent:</label>
                    <input type="number" name="count" id="count" class="form-input" placeholder="Enter Number of Mails...">
                    <input type="submit" class="form-submit-btn">
                </div>
            </form>
        </div>
        <div class="status-box hidden" id="status-box">
            <b>Mail Summary</b>
            <div id="success"></div>
            <div id="failed"></div>
        </div>
        <!-- <div class="mail-box hidden" id="mail-box">
           
        </div> -->
    </div>
</body>
</html>