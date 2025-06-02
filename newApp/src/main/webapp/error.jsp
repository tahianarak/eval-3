<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Erreur - ERPStyle</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
  <jsp:include page="sidebar.jsp" />

  <div class="error-box">
    <h2>Une erreur est survenue</h2>
    <p class="error-message">
      <%= request.getAttribute("error") %>
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

    .error-box {
      background: #fff;
      border: 1px solid #e1e5eb;
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
      border-radius: 6px;
      width: 100%;
      max-width: 600px;
      padding: 30px;
      box-sizing: border-box;
      text-align: center;
    }

    .error-box h2 {
      color: #e74c3c;
      margin-bottom: 20px;
      font-weight: 500;
    }

    .error-message {
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
