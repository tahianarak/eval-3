<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"  %>
<%@ page import="java.util.List" %>
<%@ page import="eval.newApp.modele.statistique.StatSalarySlip" %>
<%@ page import="eval.newApp.modele.pdf.Earning" %>
<%@ page import="eval.newApp.modele.pdf.Deduction" %>
<%@ page import="java.util.*, java.util.stream.*, eval.newApp.modele.statistique.SalaryStatData" %>
<%@ page import="eval.newApp.modele.utils.NumberFormatterUtil" %>

<%
    SalaryStatData data = (SalaryStatData) request.getAttribute("statdata");
    List<String> months = data.getMonths();
    Map<String, List<Double>> earningData = data.getEarningData();
    Map<String, List<Double>> deductionData = data.getDeductionData();
    List<Double> grossTotals = data.getGrossTotals();
    List<Double> deductionTotals = data.getDeductionTotals();
    List<Double> netTotals = data.getNetTotals();
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Statistiques des Salaires</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
     <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #f5f7fa;
                padding: 40px;
                margin: 0;
                display: flex; /* pour sidebar + contenu côte à côte */
            }

            /* --- Sidebar hypothétique --- */
            #sidebar {
                width: 200px;
                background-color: #fff;
                box-shadow: 2px 0 5px rgba(0,0,0,0.1);
                height: auto; /* pas de scroll */
                overflow: visible;
                padding: 20px;
                box-sizing: border-box;
            }

            /* Conteneur principal */
            .main-content {
                flex-grow: 1;
                padding-left: 60px; /* décalage plus important */
            }

            .container {
                background: white;
                padding: 30px;
                border-radius: 8px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                max-width: 1200px;
                margin: 40px auto;
            }

            .container2 {
                background: white;
                padding: 30px;
                border-radius: 8px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                max-width: 1300px;
                margin: 40px auto;
            }

            h2 {
                text-align: center;
                color: #2c3e50;
                margin-bottom: 30px;
            }

            canvas {
                width: 80% !important;
                height: auto !important;
                display: block;
                margin: 0 auto 40px auto;
            }

            .filter-form {
                display: flex;
                align-items: center;
                gap: 10px;
                margin-bottom: 30px;
                justify-content: center;
            }

            .filter-form label {
                font-weight: bold;
                color: #34495e;
            }

            .filter-form input[type="number"] {
                padding: 8px;
                font-size: 14px;
                width: 120px;
                border: 1px solid #ccc;
                border-radius: 4px;
            }

            .filter-form button {
                background-color: #3498db;
                color: white;
                padding: 8px 16px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-weight: bold;
                transition: background-color 0.3s ease;
            }

            .filter-form button:hover {
                background-color: #2980b9;
            }

            .paie-table {
                width: 98%; /* presque pleine largeur */
                max-width: 1200px; /* élargie */
                margin-left: auto; /* aligne à droite */
                margin-right: 0;
                border-collapse: collapse;
                font-size: 15px;
                table-layout: auto;
                word-wrap: break-word;
            }


            .paie-table th, .paie-table td {
                border: 1px solid #ddd;
                padding: 12px 15px;
                vertical-align: top;
            }

            .paie-table th {
                background-color: #ecf0f1;
                text-align: left;
                color: #2c3e50;
            }

            .paie-table tbody tr:hover {
                background-color: #f1f8ff;
            }

            ul {
                padding-left: 16px;
                margin: 0;
                max-height: none !important;
                overflow-y: visible !important;
            }

            a {
                color: #3498db;
                text-decoration: none;
                font-weight: bold;
            }

            a:hover {
                text-decoration: underline;
            }

            .text-center {
                text-align: center;
                color: #888;
            }
            .p-info {
                    color: #4b6cb7;        /* Bleu ERPStyle */
                    font-weight: bold;
                    margin: 10px 0;
                }


                .p-success {
                    color: #4a9079;        /* Vert foncé élégant */
                    font-weight: bold;
                    margin: 10px 0;
                }
     </style>
</head>
<body>
    <jsp:include page="sidebar.jsp" />

    <div class="main-content">
        <div class="container2">
            <h2>Statistiques des Salaires (Graphique)</h2>
            <canvas id="salaryChart" height="500"></canvas>
        </div>

        <script>
            const labels = [<%= months.stream().map(m -> "\"" + m + "\"").collect(Collectors.joining(",")) %>];
            const datasets = [];

            // EARNINGS
            <% for (Map.Entry<String, List<Double>> entry : earningData.entrySet()) {
                String label = entry.getKey();
                List<Double> values = entry.getValue();
                String color = String.format("#%06x", new java.util.Random().nextInt(0xFFFFFF));
            %>
            datasets.push({
                label: "<%= label %>",
                data: [<%= values.stream().map(String::valueOf).collect(Collectors.joining(",")) %>],
                borderColor: "<%= color %>",
                fill: false,
                tension: 0
            });
            <% } %>

            // DEDUCTIONS
            <% for (Map.Entry<String, List<Double>> entry : deductionData.entrySet()) {
                String label = entry.getKey();
                List<Double> values = entry.getValue();
            %>
            datasets.push({
                label: "<%= label %>",
                data: [<%= values.stream().map(String::valueOf).collect(Collectors.joining(",")) %>],
                borderColor: "rgba(255,0,0,0.6)",
                borderDash: [5, 5],
                fill: false,
                tension: 0
            });
            <% } %>

            // TOTAL GROSS
            datasets.push({
                label: "Total Earnings",
                data: [<%= grossTotals.stream().map(String::valueOf).collect(Collectors.joining(",")) %>],
                borderColor: "blue",
                borderWidth: 2,
                fill: false,
                tension: 0
            });

            // TOTAL DEDUCTIONS
            datasets.push({
                label: "Total Deductions",
                data: [<%= deductionTotals.stream().map(String::valueOf).collect(Collectors.joining(",")) %>],
                borderColor: "black",
                borderDash: [3, 3],
                borderWidth: 2,
                fill: false,
                tension: 0
            });

            // NET PAY
            datasets.push({
                label: "Net Pay",
                data: [<%= netTotals.stream().map(String::valueOf).collect(Collectors.joining(",")) %>],
                borderColor: "#2c3e50",
                borderWidth: 2,
                fill: false,
                tension: 0
            });

            const ctx = document.getElementById('salaryChart').getContext('2d');
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: datasets
                },
                options: {
                    responsive: true,
                    plugins: {
                        title: {
                            display: true,
                            text: 'Évolution mensuelle des composantes de salaire'
                        },
                        tooltip: {
                            mode: 'index',
                            intersect: false
                        },
                        legend: {
                            position: 'bottom'
                        }
                    },
                    interaction: {
                        mode: 'index',
                        intersect: false
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: 'Montant (Ar)'
                            }
                        },
                        x: {
                            title: {
                                display: true,
                                text: 'Mois'
                            }
                        }
                    }
                }
            });
        </script>

        <%
            List<StatSalarySlip> statistiques = (List<StatSalarySlip>) request.getAttribute("statistiques");
        %>

        <div class="container2">
            <h2>Statistiques Générales des Salaires</h2>

            <!-- Formulaire de filtre par année avec input number -->
            <form method="get" action="<%= request.getContextPath() %>/statistique-filtres" class="filter-form">
                <label for="annee">Filtrer par année :</label>
                <input type="number" name="annee" id="annee" required min="2000" max="2100" placeholder="ex: 2025" />
                <button type="submit">Filtrer</button>
            </form>

            <table class="paie-table">
                <thead>
                    <tr>
                        <th>Mois</th>
                        <th>Détails Éléments de Salaire</th>
                        <th>Détails Déductions</th>
                        <th>Salaire Brut Total</th>
                        <th>Déductions Totales</th>
                        <th>Salaire Net Total</th>

                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    if (statistiques != null && !statistiques.isEmpty()) {
                        for (StatSalarySlip stat : statistiques) {
                %>
                    <tr>
                        <td><%= stat.getMonth() %></td>
                        <td>
                                                    <ul>
                                                        <% for (Earning e : stat.getEarnings()) { %>
                                                            <li><p class="p-info"><%= e.getDescription() %> :</p> <%=  NumberFormatterUtil.formatAmount(  e.getAmount()) %></li>
                                                        <% } %>
                                                    </ul>
                                                </td>
                                                <td>
                                                    <ul>
                                                        <% for (Deduction d : stat.getDeductions()) { %>
                                                            <li><p class="p-success"><%= d.getDescription() %> :</p> <%=  NumberFormatterUtil.formatAmount(  d.getAmount()) %></li>
                                                        <% } %>
                                                    </ul>
                                                </td>
                        <td><%=  NumberFormatterUtil.formatAmount( stat.getGrossTotal()) %></td>
                        <td><%=  NumberFormatterUtil.formatAmount(  stat.getDeductionTotal()) %></td>
                        <td><%=  NumberFormatterUtil.formatAmount(  stat.getNetTotal()) %></td>

                        <td><a href="<%= request.getContextPath() %>/salaire-details?mois=<%= stat.getMonth() %>">Voir détails</a></td>
                    </tr>
                <%
                        }
                    } else {
                %>
                    <tr>
                        <td colspan="7" class="text-center">Aucune donnée disponible pour l'année sélectionnée.</td>
                    </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>
</body>

</html>
