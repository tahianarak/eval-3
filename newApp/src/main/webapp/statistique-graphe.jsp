<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.util.stream.*, eval.newApp.modele.statistique.SalaryStatData" %>

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
    <title>Graphique Statistique Salaire</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
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

        h2 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: 30px;
        }

        canvas {
            width: 100% !important;
            height: auto !important;
        }
    </style>
</head>
<body>
<jsp:include page="sidebar.jsp" />
<div class="container">
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
        tension: 0.2
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
        tension: 0.2
    });
    <% } %>

    // TOTAL GROSS
    datasets.push({
        label: "Total Earnings",
        data: [<%= grossTotals.stream().map(String::valueOf).collect(Collectors.joining(",")) %>],
        borderColor: "blue",
        borderWidth: 2,
        fill: false,
        tension: 0.2
    });

    // TOTAL DEDUCTIONS
    datasets.push({
        label: "Total Deductions",
        data: [<%= deductionTotals.stream().map(String::valueOf).collect(Collectors.joining(",")) %>],
        borderColor: "black",
        borderDash: [3, 3],
        borderWidth: 2,
        fill: false,
        tension: 0.2
    });

    // NET PAY
    datasets.push({
        label: "Net Pay",
        data: [<%= netTotals.stream().map(String::valueOf).collect(Collectors.joining(",")) %>],
        borderColor: "#2c3e50",
        borderWidth: 2,
        fill: false,
        tension: 0.2
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
</body>
</html>
