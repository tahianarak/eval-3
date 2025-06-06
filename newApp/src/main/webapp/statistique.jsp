<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="eval.newApp.modele.statistique.StatSalarySlip" %>
<%@ page import="eval.newApp.modele.pdf.Earning" %>
<%@ page import="eval.newApp.modele.pdf.Deduction" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Statistiques des Salaires</title>
</head>
<body>
<jsp:include page="sidebar.jsp" />

<%
    List<StatSalarySlip> statistiques = (List<StatSalarySlip>) request.getAttribute("statistiques");
%>

<div class="container" style="margin-left:250px; max-width: 1200px;">
    <h2>Statistiques Générales des Salaires</h2>

    <!-- Formulaire de filtre par année avec input number -->
    <form method="get" action="${pageContext.request.contextPath}/statistique-filtres" class="filter-form">
        <label for="annee">Filtrer par année :</label>
        <input type="number" name="annee" id="annee" required min="2000" max="2100" placeholder="ex: 2025" />
        <button type="submit" class="btn">Filtrer</button>
    </form>

    <table class="paie-table">
        <thead>
        <tr>
            <th>Mois</th>
            <th>Salaire Brut Total</th>
            <th>Déductions Totales</th>
            <th>Salaire Net Total</th>
            <th>Détails Éléments de Salaire</th>
            <th>Détails Déductions</th>
            <th>action</th>
        </tr>
        </thead>
        <tbody>
        <%
            if (statistiques != null && !statistiques.isEmpty()) {
                for (StatSalarySlip stat : statistiques) {
        %>
        <tr>
            <td><%= stat.getMonth() %></td>
            <td><%= String.format("%.2f Ar", stat.getGrossTotal()) %></td>
            <td><%= String.format("%.2f Ar", stat.getDeductionTotal()) %></td>
            <td><%= String.format("%.2f Ar", stat.getNetTotal()) %></td>
            <td>
                <ul>
                    <% for (Earning e : stat.getEarnings()) { %>
                        <li><%= e.getDescription() %> : <%= String.format("%.2f Ar", e.getAmount()) %></li>
                    <% } %>
                </ul>
            </td>
            <td>
                <ul>
                    <% for (Deduction d : stat.getDeductions()) { %>
                        <li><%= d.getDescription() %> : <%= String.format("%.2f Ar", d.getAmount()) %></li>
                    <% } %>
                </ul>
            </td>
            <td><a href="<%= request.getContextPath() %>/salaire-details?mois=<%=stat.getMonth() %>">voir details</a></td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="6" class="text-center">Aucune donnée disponible pour l'année sélectionnée.</td>
        </tr>
        <%
            }
        %>
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
    }

    h2 {
        text-align: center;
        color: #2c3e50;
        margin-bottom: 30px;
    }

    .filter-form {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 30px;
    }

    .filter-form input[type="number"] {
        padding: 8px;
        font-size: 14px;
        width: 100px;
    }

    .filter-form .btn {
        background-color: #3498db;
        color: white;
        padding: 8px 16px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
    }

    .filter-form .btn:hover {
        background-color: #2980b9;
    }

    .paie-table {
        width: 100%;
        border-collapse: collapse;
        font-size: 14px;
    }

    .paie-table th, .paie-table td {
        border: 1px solid #ddd;
        padding: 10px;
        vertical-align: top;
    }

    .paie-table th {
        background-color: #ecf0f1;
        text-align: left;
    }

    ul {
        padding-left: 16px;
        margin: 0;
    }

    .text-center {
        text-align: center;
    }
</style>

</body>
</html>
