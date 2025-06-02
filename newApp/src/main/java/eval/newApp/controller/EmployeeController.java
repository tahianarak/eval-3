package eval.newApp.controller;

import eval.newApp.modele.employe.EmployeeDTO;
import eval.newApp.service.EmployeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Date;
import java.util.List;

@Controller
public class EmployeeController
{

    @Autowired
    EmployeService employeService;

    @GetMapping("/getFiltreEmploye")
    public ModelAndView getFiltreEmploye(HttpSession session)
    {
        try {
            String sid=null;
            if(session.getAttribute("token")==null)
            {
                throw new Exception("pas de session valide");
            }
            sid=session.getAttribute("token").toString();
            ModelAndView mv=new ModelAndView("liste-employes");

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
    @GetMapping("/getEmployeFiltres")
    public ModelAndView getEmployeFiltres(HttpSession session
            ,@RequestParam("date_min") String dateMin
            ,@RequestParam("date_max") String dateMax
            ,@RequestParam("genre") String genre
    )throws Exception
    {
        try {
            String sid=null;
            if(session.getAttribute("token")==null)
            {
                throw new Exception("pas de session valide");
            }
            sid=session.getAttribute("token").toString();
            List<EmployeeDTO> employeeDTO=employeService.getFilteredEmployeesBetweenDates(sid,genre, Date.valueOf(dateMin),Date.valueOf(dateMax));
            ModelAndView mv=new ModelAndView("liste-employes");
            mv.addObject("employes",employeeDTO);
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
