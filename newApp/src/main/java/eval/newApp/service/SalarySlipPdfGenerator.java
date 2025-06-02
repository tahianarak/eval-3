package eval.newApp.service;



import com.itextpdf.html2pdf.HtmlConverter;
import eval.newApp.modele.pdf.Deduction;
import eval.newApp.modele.pdf.Earning;
import eval.newApp.modele.pdf.SalarySlip;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class SalarySlipPdfGenerator {

    public  byte[] generatePdf(SalarySlip salarySlip) throws Exception {
        String htmlTemplate = """
            <!DOCTYPE html>
            <html lang="fr">
            <head>
                <meta charset="UTF-8" />
                <style>
                    body { font-family: Arial, sans-serif; font-size: 12px; margin: 20px; }
                    h1 { text-align: center; font-size: 20px; }
                    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                    th, td { border: 1px solid #000; padding: 8px; text-align: left; }
                    th { background-color: #f2f2f2; }
                    .summary-table td { border: none; }
                    .summary-table { margin-top: 20px; width: 50%; float: right; }
                </style>
            </head>
            <body>
                <h1>Fiche de Paie</h1>

                <table>
                    <tr><td><strong>Employé :</strong></td><td>{{employeeName}}</td></tr>
                    <tr><td><strong>ID Employé :</strong></td><td>{{employeeId}}</td></tr>
                    <tr><td><strong>Période de Paie :</strong></td><td>{{payPeriod}}</td></tr>
                    <tr><td><strong>Département :</strong></td><td>{{department}}</td></tr>
                </table>

                <h2>Gains</h2>
                <table>
                    <tr><th>Description</th><th>Montant</th></tr>
                    {{earningsRows}}
                </table>

                <h2>Déductions</h2>
                <table>
                    <tr><th>Description</th><th>Montant</th></tr>
                    {{deductionsRows}}
                </table>

                <table class="summary-table">
                    <tr><td><strong>Salaire Brut :</strong></td><td>{{grossPay}}</td></tr>
                    <tr><td><strong>Total Déductions :</strong></td><td>{{totalDeductions}}</td></tr>
                    <tr><td><strong>Salaire Net à Payer :</strong></td><td>{{netPay}}</td></tr>
                </table>
            </body>
            </html>
            """;

        // Construire les lignes de gains
        StringBuilder earningsRows = new StringBuilder();
        for (Earning earning : salarySlip.getEarnings()) {
            earningsRows.append("<tr>")
                    .append("<td>").append(escapeHtml(earning.getDescription())).append("</td>")
                    .append("<td>").append(String.format("%.2f", earning.getAmount())).append("</td>")
                    .append("</tr>");
        }

        // Construire les lignes de déductions
        StringBuilder deductionsRows = new StringBuilder();
        for (Deduction deduction : salarySlip.getDeductions()) {
            deductionsRows.append("<tr>")
                    .append("<td>").append(escapeHtml(deduction.getDescription())).append("</td>")
                    .append("<td>").append(String.format("%.2f", deduction.getAmount())).append("</td>")
                    .append("</tr>");
        }

        // Remplacement des variables dans le template
        String html = htmlTemplate
                .replace("{{employeeName}}", escapeHtml(salarySlip.getEmployeeName()))
                .replace("{{employeeId}}", escapeHtml(salarySlip.getEmployeeId()))
                .replace("{{payPeriod}}", escapeHtml(salarySlip.getPayPeriod()))
                .replace("{{department}}", escapeHtml(salarySlip.getDepartment()))
                .replace("{{earningsRows}}", earningsRows.toString())
                .replace("{{deductionsRows}}", deductionsRows.toString())
                .replace("{{grossPay}}", String.format("%.2f", salarySlip.getGrossPay()))
                .replace("{{totalDeductions}}", String.format("%.2f", salarySlip.getTotalDeductions()))
                .replace("{{netPay}}", String.format("%.2f", salarySlip.getNetPay()));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(html, outputStream);

        return outputStream.toByteArray();
    }

    // Petite méthode pour échapper les caractères HTML
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
