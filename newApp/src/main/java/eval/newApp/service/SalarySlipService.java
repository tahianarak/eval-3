package eval.newApp.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eval.newApp.modele.pdf.SalarySlip;
import eval.newApp.modele.pdf.Earning;
import eval.newApp.modele.pdf.Deduction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class SalarySlipService {

    @Value("${erpnext.url}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public List<SalarySlip> getSalarySlipsByMonth(String sid, String monthYear) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = baseUrl + "/api/resource/Salary Slip?fields=[\"name\",\"employee\",\"employee_name\",\"start_date\",\"end_date\",\"department\",\"gross_pay\",\"net_pay\"]"+"&limit_page_length=2500";



        if (monthYear != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth yearMonth = YearMonth.parse(monthYear, formatter);

            // Définir les dates de début et de fin du mois
            String startDate = yearMonth.atDay(1).toString(); // Premier jour du mois
            String endDate = yearMonth.atEndOfMonth().toString();
            String filters = String.format("[[\"Salary Slip\", \"start_date\", \">=\", \"%s\"], [\"Salary Slip\", \"end_date\", \"<=\", \"%s\"]]",
                    startDate, endDate);
            url += "&filters=" + filters;
        }

        System.out.println("url:"+url);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode dataArray = root.get("data");

            List<SalarySlip> salarySlips = new ArrayList<>();

            if (dataArray.isArray()) {
                for (JsonNode data : dataArray) {
                    // Ici on crée une fiche partielle, sans earnings et deductions
                    SalarySlip partialSlip = new SalarySlip();
                    partialSlip.setId(data.path("name").asText(null));
                    partialSlip.setEmployeeName(data.path("employee_name").asText(null));
                    partialSlip.setEmployeeId(data.path("employee").asText(null));
                    partialSlip.setPayPeriod(data.path("start_date").asText(null) + " - " + data.path("end_date").asText(null));
                    partialSlip.setStartDate(data.path("start_date").asText(null));
                    partialSlip.setEndDate(data.path("end_date").asText(null));
                    partialSlip.setDepartment(data.path("department").asText(null));
                    partialSlip.setGrossPay(data.path("gross_pay").asDouble(0.0));
                    partialSlip.setNetPay(data.path("net_pay").asDouble(0.0));


                    salarySlips.add(partialSlip);
                }
            }

            // Maintenant pour chaque fiche partielle on récupère les détails complets
            List<SalarySlip> fullSalarySlips = new ArrayList<>();
            for (SalarySlip slip : salarySlips) {
                SalarySlip fullSlip = getSalarySlipById(sid, slip.getId());
                fullSalarySlips.add(fullSlip);
            }


            fullSalarySlips.sort(Comparator.comparing(SalarySlip::getStartDate));

            return fullSalarySlips;
        } else {
            throw new Exception("Erreur récupération des Salary Slips : " + response.getStatusCode());
        }
    }


    public SalarySlip getSalarySlipById(String sid, String salarySlipId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construire l'URL de l'API ERPNext Salary Slip
        String url = baseUrl + "/api/resource/Salary Slip/" + salarySlipId;

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            SalarySlip salarySlip = new SalarySlip();
            salarySlip.setId(salarySlipId);
            salarySlip.setEmployeeName(data.path("employee_name").asText(null));
            salarySlip.setEmployeeId(data.path("employee").asText(null));
            salarySlip.setPayPeriod(data.path("start_date").asText(null) + " - " + data.path("end_date").asText(null));
            salarySlip.setDepartment(data.path("department").asText(null));
            salarySlip.setStartDate(data.path("start_date").asText(null));
            salarySlip.setEndDate(data.path("end_date").asText(null));

            salarySlip.setGrossPay(data.path("gross_pay").asDouble(0.0));
            salarySlip.setTotalDeductions(data.path("total_deductions").asDouble(0.0));
            salarySlip.setNetPay(data.path("net_pay").asDouble(0.0));

            // Earnings
            List<Earning> earnings = new ArrayList<>();
            JsonNode earningsArray = data.path("earnings");
            if (earningsArray.isArray()) {
                for (JsonNode earningNode : earningsArray) {
                    Earning earning = new Earning();
                    earning.setDescription(earningNode.path("salary_component").asText(null));
                    earning.setAmount(earningNode.path("amount").asDouble(0.0));
                    earnings.add(earning);
                }
            }
            salarySlip.setEarnings(earnings);

            // Deductions
            List<Deduction> deductions = new ArrayList<>();
            JsonNode deductionsArray = data.path("deductions");
            if (deductionsArray.isArray()) {
                for (JsonNode deductionNode : deductionsArray) {
                    Deduction deduction = new Deduction();
                    deduction.setDescription(deductionNode.path("salary_component").asText(null));
                    deduction.setAmount(deductionNode.path("amount").asDouble(0.0));
                    deductions.add(deduction);
                }
            }
            salarySlip.setDeductions(deductions);

            return salarySlip;
        } else {
            throw new Exception("Erreur récupération Salary Slip : " + response.getStatusCode());
        }
    }
}

