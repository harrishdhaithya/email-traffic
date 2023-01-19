<!DOCTYPE html>
<%@ page import="model.Tenant,dao.TenantDao,java.util.List,dao.ScheduleDao,model.Schedule"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tenant Configuration</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <link rel="stylesheet" href="../css/style.css">
    <script src="../js/schedules.js"></script>
</head>
<body>
    <%
        ScheduleDao sdao = ScheduleDao.getInstance();
        List<Schedule> schedules = sdao.getSchedules();
        TenantDao tdao = TenantDao.getInstance();
    %>
    <div class="nav-bar flex" style="align-items: center;">
        <img src="../img/logo.png" style="height: 5rem;margin-left: 5px;">
        <div class="nav-title">EMail Traffic Generator</div>
        <div class="flex-right">
            <button class="nav-btn" onclick="location.href='/mailtraffic'">Home</button>
            <button class="nav-btn" onclick="location.href='../settings'">Settings</button>
        </div>
    </div>
    <div class="container-wide">
        <div class="header-box">
            Scheduled Tasks
        </div>
        <div class="table-container">
            <table>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Time</th>
                    <th>Tenant</th>
                    <th>Status</th>
                    <th>Count</th>
                    <th></th>
                </tr>
                <%for(Schedule s: schedules){%>
                    <%
                        Tenant t = tdao.getTenant(s.getTenantId());
                    %>
                    <tr>
                        <td><%=s.getId()%></td>
                        <td><%=s.getName()%></td>
                        <td><%=s.getTime()%></td>
                        <td><%=t.getName()%></td>
                        <td><%
                            if(s.getStatus()==3){
                                out.print("Enabled");
                            }else{
                                out.print("Disabled");
                            }
                            %></td>
                        <td><%=s.getCount()%></td>
                        <td>
                            <%if(s.getStatus()==3){%>
                                <button style="background-color: red;color: white;" value=<%=s.getId()%> onclick="disableTask(event)">Disable</button>
                            <%}else{%>
                                <button style="background-color: green;color: white;" value=<%=s.getId()%> onclick="enableTask(event)">Enable</button>
                            <%}%>
                        </td>
                    </tr>
                <%}%>
            </table>
            
            <button class="fs-2" style="background-color: green; margin-top: 5px; color: white; border-radius: 5px;" onclick="location.href='addschedule.jsp'">Add Schedule</button>   
        </div>
    </div>
</body>
</html>