<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Connexion - ERPStyle</title>
</head>
<body>
  <div class="login-box">
    <h2>Connexion</h2>
    <form action="<%= request.getContextPath() %>/login/verifyLogin" method="POST">
      <div class="form-group">
        <label for="username">Nom d'utilisateur</label>
        <input type="text" id="username" name="usr" required>
      </div>
      <div class="form-group">
        <label for="password">Mot de passe</label>
        <input type="password" id="password" name="pwd" required>
      </div>
      <button type="submit" class="login-button">Se connecter</button>
    </form>
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

    .login-box {
      background: #fff;
      border: 1px solid #e1e5eb;
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
      border-radius: 6px;
      width: 100%;
      max-width: 360px;
      padding: 30px;
      box-sizing: border-box;
    }

    .login-box h2 {
      text-align: center;
      color: #4b6cb7;
      margin-bottom: 24px;
      font-weight: 500;
    }

    .form-group {
      margin-bottom: 20px;
    }

    .form-group label {
      display: block;
      margin-bottom: 6px;
      color: #333;
    }

    .form-group input {
      width: 100%;
      padding: 10px;
      border: 1px solid #dce1e7;
      border-radius: 4px;
      font-size: 14px;
    }

    .form-group input:focus {
      border-color: #4b6cb7;
      outline: none;
    }

    .login-button {
      width: 100%;
      background-color: #4b6cb7;
      color: #fff;
      border: none;
      padding: 12px;
      border-radius: 4px;
      cursor: pointer;
      font-size: 15px;
    }

    .login-button:hover {
      background-color: #3a57a5;
    }

    .footer {
      text-align: center;
      margin-top: 20px;
      font-size: 13px;
      color: #999;
    }
  </style>
</body>
</html>
