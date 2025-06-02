<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="eval.newApp.modele.login.LoginResponseHeaders" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Bienvenue - ERPStyle</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
<jsp:include page="sidebar.jsp" />

  <div class="welcome-box">
    <h2>Bienvenue</h2>
    <p class="welcome-message">
      Bonjour <strong><%= ((LoginResponseHeaders) session.getAttribute("headers")).getFullName() %></strong>,
      vous êtes connecté à l'espace ERP.
    </p>

    <div class="footer">
      © 2025 MonEntreprise
    </div>
  </div>

  <style>
    body {
      margin: 0;
      font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
      background-color: #f7f9fc;
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100vh;
    }

    .welcome-box {
      background: #fff;
      border: 1px solid #e1e5eb;
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
      border-radius: 6px;
      width: 100%;
      max-width: 360px;
      padding: 30px;
      box-sizing: border-box;
      text-align: center;
    }

    .welcome-box h2 {
      color: #4b6cb7;
      margin-bottom: 20px;
      font-weight: 500;
    }

    .welcome-message {
      font-size: 16px;
      color: #333;
      margin-bottom: 20px;
    }

    .footer {
      text-align: center;
      font-size: 13px;
      color: #999;
      margin-top: 20px;
    }
  </style>
</body>
</html>
