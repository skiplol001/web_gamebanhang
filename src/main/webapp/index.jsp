<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Game Main Menu</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/index.css"> 
    
   <style>
       <%@include file="index.css" %>
    </style>
</head>
<body>
    <div id="main-menu">
        
        <h1 id="game-title">Mô phỏng bán hàng</h1>
        
        <div id="menu-container">
            <a href="${pageContext.request.contextPath}/Menu?action=new">
                <button class="menu-button" id="new-game">new game</button>
            </a>

            <a href="${pageContext.request.contextPath}/Menu?action=continue">
                <button class="menu-button" id="continue">continue</button>
            </a>
            
        </div>
    </div>
</body>
</html>