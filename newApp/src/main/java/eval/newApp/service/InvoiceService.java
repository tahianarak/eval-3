/*package eval.newApp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eval.newApp.modele.supplier.invoice.PurchaseInvoiceDTO;
import eval.newApp.modele.supplier.invoice.PurchaseInvoiceItemDTO;
import eval.newApp.modele.supplier.invoice.PurchaseInvoiceWithItemsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.List;

@Service
public class InvoiceService{
    private RestTemplate restTemplate=new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${erpnext.url}")
    String baseUrl;


    public PurchaseInvoiceWithItemsDTO getInvoiceWithItems(String sid, String invoiceId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        System.out.println("sid="+sid);
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // Récupérer la facture d'achat
        String invoiceUrl = baseUrl + "/api/resource/Purchase Invoice/" + invoiceId;
        ResponseEntity<String> invoiceResponse = restTemplate.exchange(invoiceUrl, HttpMethod.GET, request, String.class);

        if (invoiceResponse.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Erreur récupération facture : " + invoiceResponse.getStatusCode());
        }

        JsonNode invoiceData = objectMapper.readTree(invoiceResponse.getBody()).get("data");

        // Mappage manuel de la facture
        PurchaseInvoiceDTO invoice = new PurchaseInvoiceDTO();
        invoice.setName(invoiceData.path("name").asText(null));
        invoice.setStatus(invoiceData.path("status").asText(null));
        invoice.setOutstandingAmount(invoiceData.path("outstanding_amount").asDouble(0));
        invoice.setSupplier(invoiceData.path("supplier").asText(null));
        invoice.setPostingDate(objectMapper.treeToValue(invoiceData.path("posting_date"), Date.class));
        invoice.setGrandTotal(invoiceData.path("grand_total").asDouble(0));

        // Récupérer les items via l'API personnalisée
        String itemsUrl = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/method/ma_app.api.customer_api.get_invoice_items")
                .queryParam("invoice_id", invoiceId)
                .toUriString();

        ResponseEntity<String> itemsResponse = restTemplate.exchange(itemsUrl, HttpMethod.GET, request, String.class);

        if (itemsResponse.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Erreur récupération items : " + itemsResponse.getStatusCode());
        }

        // Récupérer et mapper les items
        JsonNode itemsData = objectMapper.readTree(itemsResponse.getBody()).get("message");
        List<PurchaseInvoiceItemDTO> items = new ArrayList<>();
        for (JsonNode itemNode : itemsData) {
            PurchaseInvoiceItemDTO item = new PurchaseInvoiceItemDTO();
            item.setItemCode(itemNode.path("item_code").asText(null));
            item.setItemName(itemNode.path("item_name").asText(null));
            item.setDescription(itemNode.path("description").asText(null));
            item.setQty(itemNode.path("qty").asDouble(0));
            item.setRate(itemNode.path("rate").asDouble(0));
            item.setAmount(itemNode.path("amount").asDouble(0));
            item.setUom(itemNode.path("uom").asText(null));
            item.setPurchaseOrder(itemNode.path("purchase_order").asText(null));
            item.setPurchaseReceipt(itemNode.path("purchase_receipt").asText(null));

            items.add(item);
        }

        // Regrouper dans un DTO enrichi
        PurchaseInvoiceWithItemsDTO fullInvoice = new PurchaseInvoiceWithItemsDTO();
        fullInvoice.setInvoice(invoice);
        fullInvoice.setItems(items);

        return fullInvoice;
    }




    public void payPurchaseInvoice(String sid, String purchaseInvoiceName) throws Exception {
        // Étape 1 : Récupérer les infos de la facture
        String invoiceUrl = baseUrl + "/api/resource/Purchase Invoice/" + purchaseInvoiceName;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> invoiceRequest = new HttpEntity<>(headers);
        ResponseEntity<String> invoiceResponse = restTemplate.exchange(invoiceUrl, HttpMethod.GET, invoiceRequest, String.class);

        if (invoiceResponse.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Erreur lors de la récupération de la facture : " + invoiceResponse.getStatusCode());
        }

        JsonNode invoiceData = objectMapper.readTree(invoiceResponse.getBody()).get("data");

        String supplier = invoiceData.get("supplier").asText();
        String postingDate = LocalDate.now().toString();
        double outstandingAmount = invoiceData.get("outstanding_amount").asDouble();
        double grandTotal = invoiceData.get("grand_total").asDouble();

        // Étape 2 : Construire le JSON de création du paiement
        ObjectNode paymentJson = objectMapper.createObjectNode();
        paymentJson.put("payment_type", "Pay");
        paymentJson.put("party_type", "Supplier");
        paymentJson.put("party", supplier);
        paymentJson.put("posting_date", postingDate);
        paymentJson.put("mode_of_payment", "Cash");
        paymentJson.put("paid_from", "Cash - H");
        paymentJson.put("paid_to", "Creditors - H");
        paymentJson.put("paid_amount", grandTotal);
        paymentJson.put("received_amount", grandTotal);
        paymentJson.put("reference_no", "AUTO-" + purchaseInvoiceName);
        paymentJson.put("reference_date", postingDate);
        paymentJson.put("remarks", "Paiement automatique de la facture " + purchaseInvoiceName);

        ArrayNode referencesArray = objectMapper.createArrayNode();
        ObjectNode ref = objectMapper.createObjectNode();
        ref.put("reference_doctype", "Purchase Invoice");
        ref.put("reference_name", purchaseInvoiceName);
        ref.put("total_amount", grandTotal);
        ref.put("outstanding_amount", outstandingAmount);
        ref.put("allocated_amount", outstandingAmount);
        referencesArray.add(ref);
        paymentJson.set("references", referencesArray);

        // Étape 3 : Créer la Payment Entry
        HttpEntity<String> createRequest = new HttpEntity<>(paymentJson.toString(), headers);
        ResponseEntity<String> paymentResponse = restTemplate.postForEntity(baseUrl + "/api/resource/Payment Entry", createRequest, String.class);

        if (paymentResponse.getStatusCode() != HttpStatus.OK && paymentResponse.getStatusCode() != HttpStatus.CREATED) {
            throw new Exception("Erreur lors de la création du paiement : " + paymentResponse.getStatusCode() + "\n" + paymentResponse.getBody());
        }

        JsonNode paymentData = objectMapper.readTree(paymentResponse.getBody()).get("data");
        String paymentEntryName = paymentData.get("name").asText();

        // Étape 4 : Récupérer l'objet complet pour soumission
        String paymentEntryUrl = baseUrl + "/api/resource/Payment Entry/" + paymentEntryName;
        ResponseEntity<String> getPaymentResponse = restTemplate.exchange(paymentEntryUrl, HttpMethod.GET, invoiceRequest, String.class);
        JsonNode fullPaymentDoc = objectMapper.readTree(getPaymentResponse.getBody()).get("data");

        // Étape 5 : Soumettre via frappe.client.submit
        String submitUrl = baseUrl + "/api/method/frappe.client.submit";
        ObjectNode submitJson = objectMapper.createObjectNode();
        submitJson.put("doc", objectMapper.writeValueAsString(fullPaymentDoc));

        HttpEntity<String> submitRequest = new HttpEntity<>(submitJson.toString(), headers);
        ResponseEntity<String> submitResponse = restTemplate.postForEntity(submitUrl, submitRequest, String.class);

        if (submitResponse.getStatusCode() == HttpStatus.OK) {
            System.out.println("Paiement soumis avec succès : " + paymentEntryName);
        } else {
            throw new Exception("Erreur lors de la soumission du paiement : " + submitResponse.getStatusCode() + "\n" + submitResponse.getBody());
        }
    }







    /*public void payPurchaseInvoice(String sid, String purchaseInvoiceName) throws Exception {
        // Étape 1 : Récupérer les infos de la facture
        String invoiceUrl = baseUrl + "/api/resource/Purchase Invoice/" + purchaseInvoiceName;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> invoiceRequest = new HttpEntity<>(headers);
        ResponseEntity<String> invoiceResponse = restTemplate.exchange(invoiceUrl, HttpMethod.GET, invoiceRequest, String.class);

        if (invoiceResponse.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Erreur lors de la récupération de la facture : " + invoiceResponse.getStatusCode());
        }

        JsonNode invoiceData = objectMapper.readTree(invoiceResponse.getBody()).get("data");

        String supplier = invoiceData.get("supplier").asText();
        String postingDate = LocalDate.now().toString();
        double outstandingAmount = invoiceData.get("outstanding_amount").asDouble();
        double grandTotal = invoiceData.get("grand_total").asDouble();

        // Étape 2 : Construire le JSON de paiement
        ObjectNode paymentJson = objectMapper.createObjectNode();
        paymentJson.put("payment_type", "Pay");
        paymentJson.put("party_type", "Supplier");
        paymentJson.put("party", supplier);
        paymentJson.put("posting_date", postingDate);
        paymentJson.put("mode_of_payment", "Cash");
        paymentJson.put("paid_from", "Cash - H");
        paymentJson.put("paid_to", "Creditors - H");
        paymentJson.put("paid_amount", grandTotal);
        paymentJson.put("received_amount", grandTotal);
        paymentJson.put("reference_no", "AUTO-" + purchaseInvoiceName);
        paymentJson.put("reference_date", postingDate);
        paymentJson.put("remarks", "Paiement automatique de la facture " + purchaseInvoiceName);

        ArrayNode referencesArray = objectMapper.createArrayNode();
        ObjectNode ref = objectMapper.createObjectNode();
        ref.put("reference_doctype", "Purchase Invoice");
        ref.put("reference_name", purchaseInvoiceName);
        ref.put("total_amount", grandTotal);
        ref.put("outstanding_amount", outstandingAmount);
        ref.put("allocated_amount", outstandingAmount);
        referencesArray.add(ref);
        paymentJson.set("references", referencesArray);


        HttpEntity<String> createRequest = new HttpEntity<>(paymentJson.toString(), headers);
        ResponseEntity<String> paymentResponse = restTemplate.postForEntity(baseUrl + "/api/resource/Payment Entry", createRequest, String.class);

        if (paymentResponse.getStatusCode() == HttpStatus.OK || paymentResponse.getStatusCode() == HttpStatus.CREATED) {
            System.out.println("Paiement effectué avec succès pour la facture : " + purchaseInvoiceName);
        } else {
            throw new Exception("Erreur lors de la création du paiement : " + paymentResponse.getStatusCode() + "\n" + paymentResponse.getBody());
        }
    }







    public List<PurchaseInvoiceDTO> getPaidPurchaseInvoices(String sid) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", "sid=" + sid);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String resource = "Purchase Invoice";
        /*String url = baseUrl + "/api/resource/" + resource
                + "?fields=[\"name\",\"status\",\"outstanding_amount\",\"supplier\",\"posting_date\",\"grand_total\"]"
                + "&filters=[[\"docstatus\",\"=\",\"1\"]]" // Factures validées
                + "&or=["
                + "{\"outstanding_amount\":\"=\",\"0\"}," // Factures payées
                + "{\"status\":\"=\",\"Received\"]"        // Factures reçues
                + "]";
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/api/resource/Purchase Invoice")
                .queryParam("fields", "[\"name\",\"status\",\"outstanding_amount\",\"supplier\",\"posting_date\",\"grand_total\"]")
         //       .queryParam("filters", "[[\"docstatus\",\"=\",\"1\"]]")
                .build(false)  // Désactive l'expansion automatique des {variables}
                .toUriString();
        System.out.println("URL: " + url);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.get("data");

            List<PurchaseInvoiceDTO> purchaseInvoices = new ArrayList<>();
            for (JsonNode invoiceNode : data) {
                PurchaseInvoiceDTO invoice = new PurchaseInvoiceDTO();
                invoice.setName(invoiceNode.path("name").asText(null));
                invoice.setStatus(invoiceNode.path("status").asText(null));
                invoice.setOutstandingAmount(invoiceNode.path("outstanding_amount").asDouble(0));
                invoice.setSupplier(invoiceNode.path("supplier").asText(null));
                invoice.setPostingDate(objectMapper.treeToValue(invoiceNode.path("posting_date"), Date.class));
                invoice.setGrandTotal(invoiceNode.path("grand_total").asDouble(0));

                purchaseInvoices.add(invoice);
            }

            return purchaseInvoices;
        } else {
            throw new Exception("Échec de la récupération des Purchase Invoices : " + response.getStatusCode());
        }
    }



}*/
