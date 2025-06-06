package eval.newApp.controller;

import eval.newApp.modele.employe.Employee;
import eval.newApp.modele.paie.SalarySlipDTO;
import eval.newApp.modele.pdf.SalarySlip;
import eval.newApp.service.EmployeService;
import eval.newApp.service.PaieService;
import eval.newApp.service.SalarySlipPdfGenerator;
import eval.newApp.service.SalarySlipService;
import jakarta.jws.WebParam;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class PaieController {

    @Autowired
    PaieService paieService;

    @Autowired
    SalarySlipService salarySlipService;

    @Autowired
    SalarySlipPdfGenerator salarySlipPdfGenerator;

    @Autowired
    EmployeService employeService;

    @GetMapping("/salaire-details-filtre")
    public ModelAndView getSalaireWithDetailsFiltre(HttpSession session)
    {
        try {
            String sid=null;
            if(session.getAttribute("token")==null)
            {
                throw new Exception("pas de session valide");
            }
            sid=session.getAttribute("token").toString();

            List<SalarySlip> salarySlips=salarySlipService.getSalarySlipsByMonth(sid,null);

            ModelAndView mv= new ModelAndView("liste-salaire-details");
            mv.addObject("SalarySlips",salarySlips);
            return mv;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ModelAndView mvi=new ModelAndView("error");
            mvi.addObject("error",e.getMessage());
            return mvi;
        }
    }


    @GetMapping("/salaire-details")
    public ModelAndView getSalaireWithDetails(@RequestParam("mois") String monthYear,HttpSession session)
    {
        try {
            String sid=null;
            if(session.getAttribute("token")==null)
            {
                throw new Exception("pas de session valide");
            }
            sid=session.getAttribute("token").toString();
            List<SalarySlip> salarySlips=salarySlipService.getSalarySlipsByMonth(sid,monthYear);

            ModelAndView mv= new ModelAndView("liste-salaire-details");
            mv.addObject("SalarySlips",salarySlips);
            return mv;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ModelAndView mvi=new ModelAndView("error");
            mvi.addObject("error",e.getMessage());
            return mvi;
        }
    }

    @GetMapping("/paies-pdf")
    public ResponseEntity<byte[]> genererPdf(@RequestParam("fiche") String fiche, HttpSession session)
    {
        try {
            String sid=null;
            if(session.getAttribute("token")==null)
            {
                throw new Exception("pas de session valide");
            }
            sid=session.getAttribute("token").toString();
            SalarySlip salarySlip=salarySlipService.getSalarySlipById(sid,fiche);
            byte[] pdf=salarySlipPdfGenerator.generatePdf(salarySlip) ;


            // Crée le nom du fichier
            String fileName = "fiche_" + fiche.replace('/','-') + ".pdf";


            // Création des en-têtes HTTP pour le téléchargement
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition
                    .builder("attachment")
                    .filename(fileName)
                    .build());
            headers.setContentLength(pdf.length);




            System.out.println(fileName);
            Path outputPath = Paths.get("D:\\tahiana\\s6\\evaluation n3\\pdf", fileName);

            // Écrit le fichier dans le système de fichiers
            try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
                fos.write(pdf);
            }
            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ModelAndView mvi=new ModelAndView("error");
            mvi.addObject("error",e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur : " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }
    @GetMapping("/paies-all")
    public ModelAndView getListePaie(HttpSession session,@RequestParam("emp") String idEmp){
        try {
            String sid=null;
            if(session.getAttribute("token")==null)
            {
                throw new Exception("pas de session valide");
            }
            sid=session.getAttribute("token").toString();
            List<SalarySlipDTO> paies=paieService.getSalarySlipsByEmployeeId(sid,idEmp);
            Employee employee=employeService.getEmployeeDetails(sid,idEmp);

            ModelAndView mv=new ModelAndView("liste-paies");

            mv.addObject("paies",paies);
            mv.addObject("emp",employee);


            return mv;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            ModelAndView mvi=new ModelAndView("error");
            mvi.addObject("error",e.getMessage());
            return mvi;
        }
    }
}
