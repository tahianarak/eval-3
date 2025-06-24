package eval.newApp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eval.newApp.modele.SalaryComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServciceGeneralise
{
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${erpnext.url}")
    private String baseUrl; // injecté depuis application.properties




    public void cancelDoctype(String sid, String name,String doctypeName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Corps JSON avec le doctype et le nom du document à annuler
        String requestBody = "{" +
                "\"doctype\": \""+doctypeName+"\"," +
                "\"name\": \"" + name + "\"" +
                "}";

        String url = baseUrl + "/api/method/frappe.desk.form.save.cancel";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            System.out.println("Salary Slip annulé : " + response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertSalaryStructure(String sid, List<SalaryComponent> components, String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String earningsJson = SalaryComponent.getJsonEarning(components);
        String deductionsJson = SalaryComponent.getJsonDeduction(components);

        String data = "{" +
                "\"doctype\": \"Salary Structure\"," +
                "\"name\": \"" + name + "\"," +
                "\"salary_structure\": \"" + name + "\"," +
                "\"company\": \"My Company\"," +
                "\"currency\": \"EUR\"," +
                "\"payroll_frequency\": \"Monthly\"," +
                "\"is_active\": \"Yes\"," +
                "\"mode_of_payment\": \"cash\"," +
                "\"earnings\": " + earningsJson + "," +
                "\"deductions\": " + deductionsJson +
                "}";

        // 1. Insertion du Salary Structure
        HttpEntity<String> insertRequest = new HttpEntity<>(data, headers);
        String insertUrl = baseUrl + "/api/resource/Salary Structure";

        try {
            ResponseEntity<String> insertResponse = restTemplate.exchange(
                    insertUrl,
                    HttpMethod.POST,
                    insertRequest,
                    String.class
            );
            System.out.println("✔ Structure insérée : " + insertResponse.getBody());

            // 2. Récupération de l'objet complet à soumettre
            String getUrl = baseUrl + "/api/resource/Salary Structure/" + name;
            HttpEntity<String> getRequest = new HttpEntity<>(headers);

            ResponseEntity<String> getResponse = restTemplate.exchange(
                    getUrl,
                    HttpMethod.GET,
                    getRequest,
                    String.class
            );

            // 3. Construction du JSON de soumission
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode fullDoc = objectMapper.readTree(getResponse.getBody()).get("data");

            ObjectNode submitJson = objectMapper.createObjectNode();
            submitJson.put("doc", objectMapper.writeValueAsString(fullDoc));

            HttpEntity<String> submitRequest = new HttpEntity<>(submitJson.toString(), headers);

            // 4. Appel de frappe.client.submit
            String submitUrl = baseUrl + "/api/method/frappe.client.submit";
            ResponseEntity<String> submitResponse = restTemplate.exchange(
                    submitUrl,
                    HttpMethod.POST,
                    submitRequest,
                    String.class
            );

            System.out.println("✅ Structure soumise : " + submitResponse.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("❌ Erreur : " + e.getResponseBodyAsString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }




    public String insertSalaryStructureAssignment(String sid, String employee, String structure, String fromDate, double base) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String data = "{" +
                "\"doctype\": \"Salary Structure Assignment\"," +
                "\"employee\": \"" + employee + "\"," +
                "\"salary_structure\": \"" + structure + "\"," +
                "\"from_date\": \"" + fromDate + "\"," +
                "\"base\": " + base +
                "}";

        String url = baseUrl + "/api/resource/Salary Structure Assignment";
        HttpEntity<String> request = new HttpEntity<>(data, headers);

        try {
            // Créer le document
            ResponseEntity<String> createResponse = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (createResponse.getStatusCode() == HttpStatus.OK) {
                // Extraire l'ID du document créé
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(createResponse.getBody());
                String assignmentId = root.path("data").path("name").asText();

                // Soumettre le document
                String submitUrl = baseUrl + "/api/resource/Salary Structure Assignment/" + assignmentId;
                String submitData = "{\"docstatus\": 1}"; // 1 signifie "Submitted"
                HttpEntity<String> submitRequest = new HttpEntity<>(submitData, headers);

                ResponseEntity<String> submitResponse = restTemplate.exchange(
                        submitUrl,
                        HttpMethod.PUT,
                        submitRequest,
                        String.class
                );

                if (submitResponse.getStatusCode() == HttpStatus.OK) {
                    return assignmentId;
                } else {
                    throw new Exception("Erreur lors de la soumission de l'assignation : " + submitResponse.getStatusCode());
                }
            } else {
                throw new Exception("Erreur lors de la création de l'assignation : " + createResponse.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void insertSalarySlip(String sid, String employee, String structure, String startDate, String endDate,String ssa) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String data = "{" +
                "\"doctype\": \"Salary Slip\"," +
                "\"employee\": \"" + employee + "\"," +
                "\"salary_structure\": \"" + structure + "\"," +
                "\"start_date\": \"" + startDate + "\"," +
                "\"end_date\": \"" + endDate + "\"," +
                "\"docstatus\": \"" + "1" + "\"," +
                "\"salary_structure_assignment\": \"" + ssa + "\"," +
                "\"payroll_frequency\": \"Monthly\"" +
                "}";


        String url = baseUrl + "/api/resource/Salary Slip";
        HttpEntity<String> request = new HttpEntity<>(data, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void insertSalaryComponent(String sid, String componentName, String abbr, String ctype, String company, String abbrComp) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construction du JSON manuellement (tu peux aussi utiliser un objet + Jackson/Gson si tu préfères)
        String data = "{"
                + "\"doctype\": \"Salary Component\","
                + "\"salary_component\": \"" + componentName + "\","
                + "\"salary_component_abbr\": \"" + abbr + "\","
                + "\"type\": \"" + ctype.substring(0, 1).toUpperCase() + ctype.substring(1).toLowerCase() + "\","
                + "\"is_tax_applicable\": 0,"
                + "\"company\": \"" + company + "\","
                + "\"depends_on_payment_days\": 0,"
                + "\"accounts\": ["
                + "    {"
                + "        \"company\": \"" + company + "\","
                + "        \"account\": \"Cash - " + abbrComp + "\","
                + "        \"default_account\": 1"
                + "    }"
                + "]"
                + "}";

        String url = baseUrl + "/api/resource/Salary Component";
        HttpEntity<String> request = new HttpEntity<>(data, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            System.out.println("Response: " + response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public List<SalaryComponent> getAll(String sid) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        System.out.println("sid="+sid);
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // Récupérer la facture d'achat
        String url = baseUrl + "/api/resource/Salary Component?fields=[\"*\"]";

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");



            List<SalaryComponent> salaryComponents = new ArrayList<>();
            for (JsonNode salaryComponentStr : data) {
                SalaryComponent salaryComponent=new SalaryComponent();

                salaryComponent.setAbbr(salaryComponentStr.path("salary_component_abbr").asText());
                salaryComponent.setSalary_component(salaryComponentStr.path("salary_component").asText());
                salaryComponent.setName(salaryComponentStr.path("name").asText());
                salaryComponent.setType(salaryComponentStr.path("type").asText());

                salaryComponents.add(salaryComponent);

            }
            return salaryComponents;
        } else {
            throw new Exception("Échec de la récupération du Supplier Quotation : " + response.getStatusCode());
        }
    }
}
