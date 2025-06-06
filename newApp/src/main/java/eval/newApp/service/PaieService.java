package eval.newApp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eval.newApp.modele.paie.SalarySlipDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PaieService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${erpnext.url}")
    private String baseUrl; // injecté depuis application.properties
    public List<SalarySlipDTO> getSalarySlipsByEmployeeId(String sid, String employeeId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Filtres JSON pour filtrer par ID d'employé
        String filtersJson = String.format("[[\"employee\", \"=\", \"%s\"]]", employeeId);

        // URL avec filtre par employee
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/resource/Salary Slip")
                .queryParam("fields", "[\"name\",\"employee\",\"employee_name\",\"start_date\",\"gross_pay\",\"net_pay\"]")
                .queryParam("filters", filtersJson)
                .queryParam("limit_page_length","2500")
                .build(false)
                .toUriString();

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            List<SalarySlipDTO> salarySlips = new ArrayList<>();
            SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");

            for (JsonNode slipNode : data) {
                SalarySlipDTO slip = new SalarySlipDTO();
                slip.setId(slipNode.path("name").asText(null));
                slip.setEmployee(slipNode.path("employee").asText(null));
                slip.setEmployeeName(slipNode.path("employee_name").asText(null));

                Date startDate = objectMapper.treeToValue(slipNode.path("start_date"), Date.class);
                slip.setStartDate(startDate);
                slip.setMois(monthFormat.format(startDate));

                slip.setGrossPay(slipNode.path("gross_pay").asDouble(0));
                slip.setNetPay(slipNode.path("net_pay").asDouble(0));

                salarySlips.add(slip);
            }

            return salarySlips;
        } else {
            throw new Exception("Échec de la récupération des fiches de paie pour l'employé " + employeeId + " : " + response.getStatusCode());
        }
    }

}

