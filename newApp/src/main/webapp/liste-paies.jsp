<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="eval.newApp.modele.paie.SalarySlipDTO" %>
<%@ page import="eval.newApp.modele.employe.Employee" %>
<%@ page import="eval.newApp.modele.login.LoginResponseHeaders" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Fiche Employé</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
<jsp:include page="sidebar.jsp" />

<%
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    LoginResponseHeaders headers = (LoginResponseHeaders) session.getAttribute("headers");
    String nomUtilisateur = headers != null ? headers.getFullName() : "Utilisateur";

    List<SalarySlipDTO> paies = (List<SalarySlipDTO>) request.getAttribute("paies");
    Employee emp = (Employee) request.getAttribute("emp");
%>

<div class="container">
    <h2>Fiche de l'Employé</h2>

    <div class="employee-details">
        <div><strong>ID :</strong> <%= emp.getId() %></div>
        <div><strong>Nom complet :</strong> <%= emp.getEmployeeName() %></div>
        <div><strong>Prénom :</strong> <%= emp.getFirstName() %></div>
        <div><strong>Nom :</strong> <%= emp.getLastName() %></div>
        <div><strong>Genre :</strong> <%= emp.getGender() %></div>
        <div><strong>Date de naissance :</strong> <%= sdf.format(emp.getDateOfBirth()) %></div>
        <div><strong>Date d'entrée :</strong> <%= sdf.format(emp.getDateOfJoining()) %></div>
        <div><strong>Département :</strong> <%= emp.getDepartment() %></div>
        <div><strong>Poste :</strong> <%= emp.getDesignation() %></div>
        <div><strong>Entreprise :</strong> <%= emp.getCompany() %></div>
        <div><strong>Email :</strong> <%= emp.getEmail() %></div>
        <div><strong>Statut :</strong> <%= emp.getStatus() %></div>
    </div>

    <h3>Historique des Fiches de Paie</h3>
    <table class="paie-table">
        <thead>
        <tr>
            <th>Mois</th>
            <th>Salaire Brut</th>
            <th>Salaire Net</th>
            <th>Exporter PDF</th>
        </tr>
        </thead>
        <tbody>
        <% if (paies != null && !paies.isEmpty()) {
            for (SalarySlipDTO slip : paies) { %>
                <tr>
                    <td><%= slip.getMois() %></td>
                    <td><%= slip.getGrossPay() %> Ar</td>
                    <td><%= slip.getNetPay() %> Ar</td>
                    <td><a href="<%= request.getContextPath() %>/paies-pdf?fiche=<%= slip.getId() %>">Télécharger PDF</a></td>
                </tr>
        <%  }
        } else { %>
            <tr><td colspan="4">Aucune fiche de paie disponible.</td></tr>
        <% } %>
        </tbody>
    </table>
</div>

<style>
    body {
        font-family: Arial, sans-serif;
        background-color: #f5f7fa;
        padding: 40px;
    }

    .container {
        background: white;
        padding: 30px;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        max-width: 1000px;
        margin: auto;
    }

    h2, h3 {
        text-align: center;
        color: #2c3e50;
    }

    .employee-details {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 15px;
        margin-bottom: 30px;
        font-size: 15px;
    }

    .paie-table {
        width: 100%;
        border-collapse: collapse;
    }

    .paie-table th, .paie-table td {
        border: 1px solid #ddd;
        padding: 10px;
        text-align: left;
    }

    .paie-table th {
        background-color: #ecf0f1;
    }
</style>
</body>
</html>
