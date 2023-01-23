<!DOCTYPE html>
<%@ page language="java" contentType="text/html"%>
<%@ page import="model.Config,dao.ConfigDao"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Setting Page</title>
    <link rel="stylesheet" href="../css/style.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
    <script src="../js/setting.js"></script>
</head>
<body>
    <%
        ConfigDao cdao = ConfigDao.getInstance();
        Config conf = cdao.getConfig("poolsize");
    %>
    <div class="nav-bar flex" style="align-items: center;">
        <img src="../img/logo.png" style="height: 5rem;margin-left: 5px;">
        <div class="nav-title">EMail Traffic Generator</div>
        <div class="flex-right">
            <button class="nav-btn" onclick="location.href='/mailtraffic'">Home</button>
            <button class="nav-btn" onclick="location.href='../settings'">Back</button>
        </div>
       
    </div>
    <div class="form-box">
        
        <div class="container">
            <div class="header-box">
                Threadpool Configuration
            </div>
            <form onsubmit="updatePoolSize(event)">
                <label for="poolsize" class="form-label">Thread Pool Size</label>
                <input type="text" name="poolsize" id="poolsize" class="form-input" placeholder="Pool Size" value=<%=conf.getPropValue()%>>
                <input type="submit" name="" id="" class="form-submit-btn">
            </form>
        </div>
    </div>
    
</body>
</html>