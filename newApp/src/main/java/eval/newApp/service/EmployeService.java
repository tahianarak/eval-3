package eval.newApp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eval.newApp.modele.employe.Employee;
import eval.newApp.modele.employe.EmployeeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.text.SimpleDateFormat;
@Service
public class EmployeService
{
    @Value("${erpnext.url}")
    String baseUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate =new RestTemplate();


    public Employee getEmployeeDetails(String sid, String employeeId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = baseUrl + "/api/resource/Employee/" + employeeId;

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            Employee employee = new Employee();
            employee.setId(data.path("name").asText(null));
            employee.setEmployeeName(data.path("employee_name").asText(null));
            employee.setFirstName(data.path("first_name").asText(null));
            employee.setLastName(data.path("last_name").asText(null));
            employee.setGender(data.path("gender").asText(null));
            employee.setDateOfJoining(objectMapper.treeToValue(data.path("date_of_joining"), java.util.Date.class));
            employee.setDateOfBirth(objectMapper.treeToValue(data.path("date_of_birth"), java.util.Date.class));
            employee.setDepartment(data.path("department").asText(null));
            employee.setDesignation(data.path("designation").asText(null));
            employee.setCompany(data.path("company").asText(null));
            employee.setStatus(data.path("status").asText(null));
            employee.setEmail(data.path("preferred_email").asText(null));

            return employee;
        } else {
            throw new Exception("Erreur récupération de l'employé " + employeeId + " : " + response.getStatusCode());
        }
    }

    public List<EmployeeDTO> getFilteredEmployeesBetweenDates(String sid, String gender, Date startDate, Date endDate) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Formateur de date pour les filtres (format ISO 8601 attendu par ERPNext)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr = sdf.format(startDate);
        String endDateStr = sdf.format(endDate);

        // Prépare les filtres ERPNext
        String filtersJson = String.format(
                "[[\"gender\", \"=\", \"%s\"], [\"date_of_joining\", \">=\", \"%s\"], [\"date_of_joining\", \"<=\", \"%s\"]]",
                gender, startDateStr, endDateStr
        );

        // Construction de l'URL avec les champs souhaités et les filtres
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/resource/Employee")
                .queryParam("fields", "[\"name\",\"employee_name\",\"first_name\",\"gender\",\"date_of_joining\",\"date_of_birth\"]")
                .queryParam("filters", filtersJson)
                .queryParam("limit_page_length","2500")
                .build(false)
                .toUriString();

        System.out.println("URL: " + url);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            List<EmployeeDTO> employees = new ArrayList<>();
            for (JsonNode employeeNode : data) {
                EmployeeDTO employee = new EmployeeDTO();
                employee.setEmployeeNumber(employeeNode.path("name").asText(null));
                employee.setEmployeeName(employeeNode.path("employee_name").asText(null));
                employee.setFirstName(employeeNode.path("first_name").asText(null));
                employee.setGender(employeeNode.path("gender").asText(null));
                employee.setDateOfJoining(objectMapper.treeToValue(employeeNode.path("date_of_joining"), Date.class));
                employee.setDateOfBirth(objectMapper.treeToValue(employeeNode.path("date_of_birth"), Date.class));

                employees.add(employee);
            }

            return employees;
        } else {
            throw new Exception("Échec de la récupération des employés : " + response.getStatusCode());
        }
    }



    public List<EmployeeDTO> getEmployees(String sid) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);


        // Construction de l'URL avec les champs souhaités et les filtres
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/resource/Employee")
                .queryParam("fields", "[\"name\",\"employee_name\",\"first_name\",\"gender\",\"date_of_joining\",\"date_of_birth\"]")
                .queryParam("limit_page_length","2500")
                .build(false)
                .toUriString();

        System.out.println("URL: " + url);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            List<EmployeeDTO> employees = new ArrayList<>();
            for (JsonNode employeeNode : data) {
                EmployeeDTO employee = new EmployeeDTO();
                employee.setEmployeeNumber(employeeNode.path("name").asText(null));
                employee.setEmployeeName(employeeNode.path("employee_name").asText(null));
                employee.setFirstName(employeeNode.path("first_name").asText(null));
                employee.setGender(employeeNode.path("gender").asText(null));
                employee.setDateOfJoining(objectMapper.treeToValue(employeeNode.path("date_of_joining"), Date.class));
                employee.setDateOfBirth(objectMapper.treeToValue(employeeNode.path("date_of_birth"), Date.class));

                employees.add(employee);
            }

            return employees;
        } else {
            throw new Exception("Échec de la récupération des employés : " + response.getStatusCode());
        }
    }

}
