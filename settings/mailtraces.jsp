<%@ page import="model.Tenant,dao.TenantDao,java.util.List,messagetrace.MessageTrace"%>
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
    <script src="../js/mailtraces.js"></script>
</head>
<body>
    <%
        MessageTrace mt = (MessageTrace)session.getAttribute("mailtrace");
    %>
    <div class="nav-bar flex" style="align-items: center;">
        <img alt="logo Image" src="../img/logo.png" style="height: 5rem;margin-left: 5px;">
        <div class="nav-title">EMail Traffic Generator</div>
        <div class="flex-right">
            <button class="nav-btn" onclick="location.href='tracehistory.jsp'">History</button>
            <button class="nav-btn" onclick="location.href='collecttrace.jsp'">Collect Message Trace</button>
            <%if(mt!=null){%>
                <button class="nav-btn" onclick="location.href='mailtracestatus.jsp'">Current Trace Status</button>
            <%}%>
            <button class="nav-btn" onclick="location.href='/mailtraffic'">Home</button>
            <button class="nav-btn" onclick="location.href='../settings'">Back</button>
        </div>
    </div>
    <%
        TenantDao tdao = TenantDao.getInstance();
        List<Tenant> tenants = tdao.getAllTenants();
        if(tenants.size()==0){
            response.sendRedirect("/mailtraffic/settings/tenconf.jsp");
        }
    %>
    <div class="form-box">
        <div class="container-wide" style="width: 980px;">
            <div class="header-box">
                Message Trace
            </div>
            <form onsubmit="getTraces(event)">
                <label for="tenant" class="form-label">Select Tenant  </label>
                <select name="tenant" id="tenant" class="form-input">
                    <option value="">Select Tenant</option>
                    <%for(Tenant t:tenants){%>
                        <option value=<%=t.getId()%>><%=t.getName()%></option>
                    <%}%>
                </select>
                <label for="count" class="form-label">Entry Per Page: </label>
                <select name="count" id="count" class="form-input">
                    <option value="25">25</option>
                    <option value="50">50</option>
                    <option value="100">100</option>
                    <option value="150">150</option>
                </select>
                <div class="flex-center">
                    <input type="submit" name="submit" class="form-submit-btn" value="Get data">
                </div>
            </form>
            <hr>
            <div class="hidden" style="text-align: center;margin-top: 8px;" id="loading">
                <img src="../img/loading.gif" style="height: 50px;width: 50px;border-radius: 50%;">
            </div>
            <div class="table-container hidden" id="table-container">
                <div class="table-container-inner" id="table">
                    <div class="total-data"></div>
                    <div class="pageinfo"></div>
                    <div class="pagination-container">
                        <div class="pagination">
                            <button onclick="firstPage(event)" class="firstpage">&laquo;&laquo;</button>
                            <button onclick="prevPage(event)" class="prevbtn">&laquo;</button>
                            <div class="pagenum">1</div>
                            <button onclick="nextPage(event)" class="nextbtn">&raquo;</button>
                            <button onclick="lastPage(event)" class="lastpage">&raquo;&raquo;</button>
                        </div>
                    </div>
                    <table style="table-layout: fixed; width: 100%;overflow-wrap: break-word;">
                        <thead>
                            <th>Sender</th>
                            <th>Receiver</th>
                            <th>Subject</th>
                            <th>Status</th>
                            <th>Received</th>
                        </thead>
                        <tbody id="table-body">
                        </tbody>
                    </table>
                    <div class="total-data"></div>
                    <div class="pageinfo"></div>
                    <div class="pagination-container">
                        <div class="pagination">
                            <button onclick="firstPage(event)" class="firstpage">&laquo;&laquo;</button>
                            <button onclick="prevPage(event)" class="prevbtn">&laquo;</button>
                            <div class="pagenum">1</div>
                            <button onclick="nextPage(event)" class="nextbtn">&raquo;</button>
                            <button onclick="lastPage(event)" class="lastpage">&raquo;&raquo;</button>
                        </div>
                    </div>
                </div>
                <div class="fs-2 hidden" id="nodata">No Data Found</div>
            </div>
        </div>
        
    </div>
</body>
</html>