package eval.newApp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eval.newApp.modele.FormatUtil;
import eval.newApp.modele.paie.SalarySlipDTO;
import eval.newApp.modele.pdf.Deduction;
import eval.newApp.modele.pdf.Earning;
import eval.newApp.modele.pdf.SalarySlip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class PaieService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${erpnext.url}")
    private String baseUrl; // injecté depuis application.properties

    @Autowired
    SalarySlipService salarySlipService;

    public void updateFiltered(String sid,String composant,double minComposant,double maxComposant,double minBase,double MaxBase,double pourcentage) throws Exception {
        List<SalarySlip> salarySlips=getsalaryFiltre(sid,composant,minComposant,maxComposant,null);
        salarySlips=getsalaryFiltre(sid,"Salaire Base",minBase,MaxBase,salarySlips);

        for(SalarySlip salarySlip:salarySlips)
        {
            cancelSalarySlip(sid,salarySlip.getId());
            String ssa=getClosestSSAId(sid,salarySlip.getEmployeeId(),salarySlip.getDateStart());
            cancelSalaryStructureAssignment(sid,ssa);
            double base=0;
            for(Earning earning:salarySlip.getEarnings())
            {
                if(earning.getDescription().equals("Salaire Base"))
                {
                    base=earning.getAmount();
                    System.out.println("base:"+base);
                }
            }

            ssa=insertSalaryStructureAssignment(sid,salarySlip.getEmployeeId(),salarySlip.getStructure(),salarySlip.getDateStart(),base+(base*(pourcentage/100)));

            insertSalarySlip(sid,salarySlip.getEmployeeId(),salarySlip.getStructure(),salarySlip.getDateStart(),salarySlip.getDateEnd(),ssa);
        }

    }


    public void cancelSalarySlip(String sid, String salarySlipName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Corps JSON avec le doctype et le nom du document à annuler
        String requestBody = "{" +
                "\"doctype\": \"Salary Slip\"," +
                "\"name\": \"" + salarySlipName + "\"" +
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


    public void cancelSalaryStructureAssignment(String sid, String ssaName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{" +
                "\"doctype\": \"Salary Structure Assignment\"," +
                "\"name\": \"" + ssaName + "\"" +
                "}";

        String url = baseUrl + "/api/method/frappe.desk.form.save.cancel";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            System.out.println("SSA annulé : " + response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<SalarySlip> getsalaryFiltre(String sid,String composant,double minComposant,double maxComposant,List<SalarySlip> salarySlips)
            throws Exception {

        List<SalarySlip> salarySlipsFiltered=new ArrayList<>();
        if(salarySlips==null) {
            salarySlips = salarySlipService.getSalarySlipsByMonth(sid, null);
        }

        for (SalarySlip salarySlip:salarySlips)
        {
            for(Deduction deduction:salarySlip.getDeductions())
            {
                if(deduction.getDescription().equals(composant) && deduction.getAmount()>minComposant && deduction.getAmount()<maxComposant)
                {
                    salarySlipsFiltered.add(salarySlip);
                }
            }
            for(Earning earning:salarySlip.getEarnings())
            {
                if(earning.getDescription().equals(composant) && earning.getAmount()>minComposant && earning.getAmount()<maxComposant)
                {
                    salarySlipsFiltered.add(salarySlip);
                }
            }
        }
        return salarySlipsFiltered;
    }
    public void insertMultipe(String sid,String employee,String startMonth,String endMonth,String salaireBase) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(startMonth, formatter);

        // Définir les dates de début et de fin du mois
        LocalDate startDate = yearMonth.atDay(1); // Premier jour du mois
        String endDate = yearMonth.atEndOfMonth().toString();

        String ssa=getClosestSSAId(sid,employee,startDate.toString());

        YearMonth yearMonth2 = YearMonth.parse(endMonth, formatter);

        // Définir les dates de début et de fin du mois
        LocalDate startDate2 = yearMonth2.atDay(1); // Premier jour du mois
        String endDate2 = yearMonth2.atEndOfMonth().toString();

        List<LocalDate> monthsBetween= FormatUtil.getIntermediateMonths(startDate,startDate2);

        List<SalarySlip> salarySlips=salarySlipService.getSalarySlipsByMonth2(sid,startMonth);
        SalarySlip trueSalary=null;
        for(SalarySlip salarySlip:salarySlips)
        {
            System.out.println(salarySlip.getEmployeeId()+" -"+employee);
            if(salarySlip.getEmployeeId().equals(employee))
            {
                trueSalary=salarySlip;
                break;
            }
        }
        if(trueSalary==null)
        {
            throw  new Exception("pas de donnees avant la date de debut ");
        }
        if(!salaireBase.equals(""))
        {
            Double base =Double.valueOf(salaireBase);
            System.out.println(ssa);
            ssa=insertSalaryStructureAssignment(sid,employee,trueSalary.getStructure(),startDate.toString(),base);
        }

        insertSalarySlip(sid,employee,trueSalary.getStructure(),startDate.toString(),endDate,ssa);

        for (LocalDate month:monthsBetween){
            String tire="-0";
            if(month.getMonthValue()>=10){
                tire="-";
            }
            if(!salaireBase.equals(""))
            {
                Double base =Double.valueOf(salaireBase);
                ssa=insertSalaryStructureAssignment(sid,employee,trueSalary.getStructure(),month.toString(),base);
            }
            YearMonth yearMonthTemp = YearMonth.parse(month.getYear()+tire+month.getMonthValue(), formatter);
            insertSalarySlip(sid,employee,trueSalary.getStructure(),month.toString(),yearMonthTemp.atEndOfMonth().toString(),ssa);
        }
        if(!startDate2.equals(startDate)) {
            if(!salaireBase.equals(""))
            {
                Double base =Double.valueOf(salaireBase);
                ssa=insertSalaryStructureAssignment(sid,employee,trueSalary.getStructure(),startDate2.toString(),base);
            }
            insertSalarySlip(sid, employee, trueSalary.getStructure(), startDate2.toString(), endDate2,ssa);
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

    public String insertSalaryStructureAssignment(String sid, String employee, String structure, String fromDate, double base)
    {
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


    public String getClosestSSAId(String sid, String employeeId, String targetDate) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Filtres pour SSA valides avant ou à targetDate
        String filtersJson = String.format("[[\"employee\", \"=\", \"%s\"], [\"from_date\", \"<=\", \"%s\"]]", employeeId, targetDate);

        // Construction de l'URL avec tri par from_date décroissant pour avoir le plus proche d'abord
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/resource/Salary Structure Assignment")
                .queryParam("fields", "[\"name\", \"from_date\"]")
                .queryParam("filters", filtersJson)
                .queryParam("limit_page_length", "1000")
                .queryParam("order_by", "from_date desc")
                .build(false)
                .toUriString();

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            if (data.isArray() && data.size() > 0) {
                JsonNode firstMatch = data.get(0);
                return firstMatch.path("name").asText(null);
            } else {
                System.out.println("Aucun Salary Structure Assignment trouvé pour l'employé " + employeeId + " avant la date " + targetDate);
                return null;
            }
        } else {
            throw new Exception("Échec de la récupération du Salary Structure Assignment : " + response.getStatusCode());
        }
    }



    public List<SalarySlipDTO> getSalarySlipsByEmployeeId(String sid, String employeeId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Filtres JSON pour filtrer par ID d'employé
        String filtersJson = String.format("[[\"employee\", \"=\", \"%s\"]]", employeeId);

        // URL avec filtre par employee
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/resource/Salary Slip")
                .queryParam("fields", "[\"name\",\"employee\",\"employee_name\",\"start_date\",\"gross_pay\",\"net_pay\",\"docstatus\"]")
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
                if(slipNode.path("docstatus").asInt()!=2) {
                    System.out.println("docstatus-em:"+data.path("docstatus"));
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
            }

            return salarySlips;
        } else {
            throw new Exception("Échec de la récupération des fiches de paie pour l'employé " + employeeId + " : " + response.getStatusCode());
        }
    }

}

