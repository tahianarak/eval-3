<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="eval.newApp.modele.employe.EmployeeDTO" %>
<%@ page import="eval.newApp.modele.login.LoginResponseHeaders" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des Employés - ERPStyle</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
<jsp:include page="sidebar.jsp" />

<%
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    LoginResponseHeaders headers = (LoginResponseHeaders) session.getAttribute("headers");
    String nomUtilisateur = headers != null ? headers.getFullName() : "Utilisateur";
    List<EmployeeDTO> employes = (List<EmployeeDTO>) request.getAttribute("employes");
%>

<div class="welcome-box">
    <h2>Liste des Employés</h2>
    <p class="welcome-message">
        Bonjour <strong><%= nomUtilisateur %></strong>,
        utilisez les filtres ci-dessous pour rechercher des employés.
    </p>

    <!-- Formulaire de filtre -->
    <form method="get" action="getEmployeFiltres" class="filter-form">
        <label for="date_min">Date d'embauche (min):</label>
        <input type="date" id="date_min" name="date_min" required>

        <label for="date_max">Date d'embauche (max):</label>
        <input type="date" id="date_max" name="date_max" required>

        <label for="genre">Genre:</label>
        <select id="genre" name="genre" required>
            <option value="Male">Homme</option>
            <option value="Female">Femme</option>
            <option value="Other">Autre</option>
        </select>

        <button type="submit">Filtrer</button>
    </form>

    <!-- Tableau des employés -->
    <table class="employee-table">
        <thead>
        <tr>
            <th>Réf</th>
            <th>Nom</th>
            <th>Prénom</th>
            <th>Genre</th>
            <th>Date Embauche</th>
            <th>Date Naissance</th>
            <th>voir fiche employe</th>
        </tr>
        </thead>
        <tbody>
        <% if (employes != null && !employes.isEmpty()) {
            for (EmployeeDTO emp : employes) { %>
                <tr>
                    <td><%= emp.getEmployeeNumber() %></td>
                    <td><%= emp.getEmployeeName() %></td>
                    <td><%= emp.getFirstName() %></td>
                    <td><%= emp.getGender() %></td>
                    <td><%= sdf.format(emp.getDateOfJoining()) %></td>
                    <td><%= sdf.format(emp.getDateOfBirth()) %></td>
                    <td><button><a href="<%= request.getContextPath() %>/paies-all?emp=<%=emp.getEmployeeNumber()%>">voir fiche</a></button></td>
                </tr>
        <%  }
        } else { %>
            <tr>
                <td colspan="6">Aucun employé trouvé pour les critères sélectionnés.</td>
            </tr>
        <% } %>
        </tbody>
    </table>

    <div class="footer">
        © 2025 MonEntreprise
    </div>
</div>

<!-- Styles -->
<style>
    body {
        margin: 0;
        font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
        background-color: #f7f9fc;
        display: flex;
        align-items: flex-start;
        justify-content: center;
        padding: 40px;
    }

    .welcome-box {
        background: #fff;
        border: 1px solid #e1e5eb;
        box-shadow: 0 2px 8px rgba(0,0,0,0.05);
        border-radius: 6px;
        width: 100%;
        max-width: 1000px;
        padding: 30px;
        box-sizing: border-box;
    }

    .welcome-box h2 {
        color: #4b6cb7;
        margin-bottom: 20px;
        font-weight: 500;
        text-align: center;
    }

    .welcome-message {
        font-size: 16px;
        color: #333;
        margin-bottom: 20px;
        text-align: center;
    }

    .filter-form {
        display: flex;
        flex-wrap: wrap;
        gap: 15px;
        margin-bottom: 25px;
        align-items: center;
        justify-content: center;
    }

    .filter-form label {
        font-weight: bold;
        margin-right: 5px;
    }

    .filter-form input, .filter-form select {
        padding: 6px 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
    }

    .filter-form button {
        padding: 8px 16px;
        background-color: #4b6cb7;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
    }

    .filter-form button:hover {
        background-color: #3a569d;
    }

    .employee-table {
        width: 100%;
        border-collapse: collapse;
    }

    .employee-table th, .employee-table td {
        padding: 12px 15px;
        border: 1px solid #e0e0e0;
        text-align: left;
        font-size: 14px;
    }

    .employee-table th {
        background-color: #f0f4f8;
        color: #333;
    }

    .footer {
        text-align: center;
        font-size: 13px;
        color: #999;
        margin-top: 30px;
    }
</style>
</body>
</html>
