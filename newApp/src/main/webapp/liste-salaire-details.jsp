<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="eval.newApp.modele.pdf.SalarySlip" %>
<%@ page import="eval.newApp.modele.pdf.Earning" %>
<%@ page import="eval.newApp.modele.pdf.Deduction" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Détails des Salaires</title>
</head>
<body>
<jsp:include page="sidebar.jsp" />

<%
    List<SalarySlip> slips = (List<SalarySlip>) request.getAttribute("SalarySlips");
%>

<div class="container" style="margin-left:250px; max-width: 1200px;">
    <h2>Détails des Salaires</h2>

    <form method="get" action="${pageContext.request.contextPath}/salaire-details" class="filter-form">
        <label for="mois">Filtrer par mois :</label>
        <input type="month" name="mois" id="mois" required />
        <button type="submit" class="btn">Rechercher</button>
    </form>

    <table class="paie-table">
        <thead>
        <tr>
            <th>Employé</th>
            <th>Département</th>
            <th>Période</th>
            <th>Éléments de Salaire</th>
            <th>Déductions</th>
            <th>Salaire Brut</th>
            <th>Déductions Totales</th>
            <th>Salaire Net</th>
        </tr>
        </thead>
        <tbody>
        <%
            if (slips != null && !slips.isEmpty()) {
                for (SalarySlip slip : slips) {
        %>
        <tr>
            <td><%= slip.getEmployeeName() %></td>
            <td><%= slip.getDepartment() %></td>
            <td><%= slip.getPayPeriod() %></td>
            <td>
                <ul>
                    <%
                        for (Earning e : slip.getEarnings()) {
                    %>
                    <li><%= e.getDescription() %> : <%= String.format("%.2f Ar", e.getAmount()) %></li>
                    <%
                        }
                    %>
                </ul>
            </td>
            <td>
                <ul>
                    <%
                        for (Deduction d : slip.getDeductions()) {
                    %>
                    <li><%= d.getDescription() %> : <%= String.format("%.2f Ar", d.getAmount()) %></li>
                    <%
                        }
                    %>
                </ul>
            </td>
            <td><%= String.format("%.2f Ar", slip.getGrossPay()) %></td>
            <td><%= String.format("%.2f Ar", slip.getTotalDeductions()) %></td>
            <td><%= String.format("%.2f Ar", slip.getNetPay()) %></td>
        </tr>
        <%
                }
            } else {
        %>
        <tr>
            <td colspan="8" class="text-center">Aucune fiche de paie pour le mois sélectionné.</td>
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
        /* max-width et margin-left sont en inline style */
    }

    h2 {
        text-align: center;
        color: #2c3e50;
        margin-bottom: 30px;
    }

    .filter-form {
        display: flex;
        gap: 15px;
        align-items: center;
        margin-bottom: 30px;
    }

    .filter-form input[type="month"] {
        padding: 8px;
        font-size: 14px;
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
        margin: auto;
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
