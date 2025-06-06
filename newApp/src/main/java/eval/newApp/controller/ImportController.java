package eval.newApp.controller;

import eval.newApp.modele.employe.Employee;
import eval.newApp.modele.paie.SalarySlipDTO;
import eval.newApp.service.ImportService;
import jakarta.jws.WebParam;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@Controller
public class ImportController {



    @Autowired
    ImportService importService;




    @PostMapping("/import-data")
    public ModelAndView handleImport(
            @RequestParam("fichier1") MultipartFile fichier1,
            @RequestParam("fichier2") MultipartFile fichier2,
            @RequestParam("fichier3") MultipartFile fichier3,
            HttpSession session) {

        try {
            String sid=null;
            if(session.getAttribute("token")==null)
            {

                throw new Exception("pas de session valide");
            }
            sid=session.getAttribute("token").toString();
            // Traitement du fichier Utilisateurs
            if (!fichier1.isEmpty() && !fichier2.isEmpty() && !fichier3.isEmpty()) {
                String employeContent = new String(fichier1.getBytes());
                String salaryComponentandstructContent = new String(fichier2.getBytes());
                String employeeSalary = new String(fichier3.getBytes());
                importService.sendEmployeesToFrappe(sid,employeContent,salaryComponentandstructContent,employeeSalary);
            }
            ModelAndView mv=new ModelAndView("accueil");
            return mv;
        } catch (Exception e) {
            e.printStackTrace();
            ModelAndView mvi=new ModelAndView("error");
            mvi.addObject("error",e.getMessage());
            return mvi;
        }
    }
    @GetMapping("/import")
    public ModelAndView getFormulaireDImport(HttpSession session)
    {
        try {
            String sid=null;
            if(session.getAttribute("token")==null)
            {
                throw new Exception("pas de session valide");
            }

            ModelAndView mv=new ModelAndView("import");

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
