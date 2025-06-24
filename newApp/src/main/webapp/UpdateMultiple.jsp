<%@ page import="java.util.List" %>
<%@ page import="eval.newApp.modele.*" %>



<%
    List<SalaryComponent> components=(List<SalaryComponent>)request.getAttribute("components");

%>

<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Importation de Données - ERPStyle</title>
</head>
<body>
<jsp:include page="sidebar.jsp" />
  <div class="import-box">
    <h2>Update de Données multiple</h2>

    <form action="<%= request.getContextPath() %>/update-data" method="POST" >

      <div class="form-group">
          <label for="username">choix de composant salaire</label>
       <select name="composant">
                  <% for(SalaryComponent composant:components){%>
            <option value="<%= composant.getName()%>"><%=composant.getName()%></option>
                  <%}%>
       </select>
      </div>


      <div class="form-group">
        <label for="usersFile">min composant</label>
        <input type="number" id="usersFile" name="min_composant"  >
      </div>

      <div class="form-group">
        <label for="productsFile">max composant</label>
        <input type="number" id="productsFile" name="max_composant"  >
      </div>

      <div class="form-group">
        <label for="ordersFile">max salaire</label>
        <input type="number" id="ordersFile" name="max_salaire"  >
      </div>

      <div class="form-group">
              <label for="ordersFile">min salaire</label>
              <input type="number" id="ordersFile" name="min_salaire" >
      </div>

      <div class="form-group">
              <label for="ordersFile">pourcentage</label>
              <input type="number" id="ordersFile" name="pourcentage" >
      </div>

      <button type="submit" class="import-button">updater les donnees </button>
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
      padding: 20px;
      box-sizing: border-box;
    }

    .import-box {
      background: #fff;
      border: 1px solid #e1e5eb;
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
      border-radius: 6px;
      width: 100%;
      max-width: 400px;
      padding: 30px;
      box-sizing: border-box;
    }

    .import-box h2 {
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

    .form-group input[type="file"] {
      width: 100%;
      font-size: 14px;
    }

    .import-button {
      width: 100%;
      background-color: #4b6cb7;
      color: #fff;
      border: none;
      padding: 12px;
      border-radius: 4px;
      cursor: pointer;
      font-size: 15px;
    }

    .import-button:hover {
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
