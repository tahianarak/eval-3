package eval.newApp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eval.newApp.modele.importer.ComponentAndStructureDTO;
import eval.newApp.modele.importer.EmployeeImportDTO;
import eval.newApp.modele.importer.EmployeeSalaryDTO;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImportService {
    @Value("${erpnext.url}")
    String baseUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate =new RestTemplate();

    char separator=',';

    public void sendEmployeesToFrappe(String sid, String employeeData, String componentData,String employeeSalaryData) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        System.out.println("sid:"+sid);
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();

        // Convertir les employés
        List<EmployeeImportDTO> employees = getAllEmployeForImport(employeeData);

        // Convertir les composants
        List<ComponentAndStructureDTO> components = getAllComponentsForImport(componentData);

        //convertir les salaires
        List<EmployeeSalaryDTO>  salaries=getAllEmployeeSalaryForImport(employeeSalaryData);

        // Créer une map contenant les deux
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("Employees", employees);
        payloadMap.put("Components", components);
        payloadMap.put("salaries",salaries);

        // Sérialiser en JSON
        String payload = mapper.writeValueAsString(payloadMap);

        String url = baseUrl + "/api/method/ma_app.api.rh_import_api.receiveData";

        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Envoi réussi !");
        } else {
            throw new Exception("Échec de l'envoi vers Frappe : " + response.getStatusCode());
        }
    }


    public List<EmployeeSalaryDTO> getAllEmployeeSalaryForImport(String employeeData) throws Exception {
        try {
            List<EmployeeSalaryDTO> result = new ArrayList<>();

            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            CSVReader reader = new CSVReaderBuilder(new StringReader(employeeData))
                    .withCSVParser(parser)
                    .build();

            List<String[]> allData = reader.readAll();
            reader.close();

            // Retirer l'entête
            if (!allData.isEmpty()) {
                allData.remove(0);
            }


            int counter=1;

            for (String[] line : allData) {
                if (line.length < 4) continue;

                String mois = line[0].trim();
                int refEmploye = Integer.parseInt(line[1].trim());
                double salaireBase = Double.parseDouble(line[2].trim());
                String salaire = line[3].trim();

                EmployeeSalaryDTO dto = new EmployeeSalaryDTO();
                dto.setLigne(counter);
                dto.setMois(mois);
                dto.setRefEmploye(refEmploye);
                dto.setSalaireBase(salaireBase);
                dto.setSalaire(salaire);



                result.add(dto);
                counter++;
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public List<ComponentAndStructureDTO> getAllComponentsForImport(String componentData) throws Exception {
        try {
            List<ComponentAndStructureDTO> ans = new ArrayList<>();
            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            CSVReader reader = new CSVReaderBuilder(new StringReader(componentData))
                    .withCSVParser(parser)
                    .build();

            List<String[]> allData = reader.readAll();
            reader.close();

            allData.remove(0); // Suppression de l'en-tête

            for (String[] line : allData) {
                ComponentAndStructureDTO component = new ComponentAndStructureDTO();
                component.setSalaryStructure(line[0]);
                component.setName(line[1]);
                component.setAbbr(line[2]);
                component.setType(line[3]);
                component.setValeur(line[4]);
                component.setCompany(line[5]);

                ans.add(component);
            }
            return ans;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<EmployeeImportDTO> getAllEmployeForImport(String employeeData)throws Exception
    {
        try
        {
            List<EmployeeImportDTO> ans=new ArrayList<>();
            CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
            CSVReader reader = new CSVReaderBuilder(new StringReader(employeeData))
                    .withCSVParser(parser)
                    .build();

            List<String[]> allData = reader.readAll();
            reader.close();

            allData.remove(0);

            int counter=1;

            for (String[] line : allData)
            {
                EmployeeImportDTO employee=new EmployeeImportDTO();
                employee.setLigne(counter);
                employee.setRef(line[0]);
                employee.setNom(line[1]);
                employee.setPrenom(line[2]);
                employee.setGenre(line[3]);
                employee.setDateEmbauche(line[4]);
                employee.setDateNaissance(line[5]);
                employee.setCompany(line[6]);



                ans.add(employee);
                counter++;
            }
            return ans;

        } catch (Exception e) {
            e.printStackTrace();
            throw  e;
        }
    }
}
