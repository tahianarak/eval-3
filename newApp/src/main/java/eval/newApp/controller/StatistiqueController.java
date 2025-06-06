package eval.newApp.controller;

import eval.newApp.modele.statistique.StatSalarySlip;
import eval.newApp.service.SalarySlipStatisticsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class StatistiqueController
{
    @Autowired
    SalarySlipStatisticsService salarySlipStatisticsService;


    @GetMapping("/get-graph")
    public ModelAndView getGraph(HttpSession session)
    {
        try {
            String sid=null;
            if(session.getAttribute("token")==null)
            {
                throw new Exception("pas de session valide");
            }
            sid=session.getAttribute("token").toString();

            ModelAndView mv=new ModelAndView("statistique-graphe");
            List<StatSalarySlip> statSalarySlips=salarySlipStatisticsService.getStaticsData(sid);
            mv.addObject("statdata",salarySlipStatisticsService.buildStatData(statSalarySlips));
            return mv;
        } catch (Exception e) {
            e.printStackTrace();
            ModelAndView mvi=new ModelAndView("error");
            mvi.addObject("error",e.getMessage());
            return mvi;
        }
    }
    @GetMapping("/statistique-filtres")
    public ModelAndView getGeneralStaticFiltre(HttpSession session, @RequestParam("annee") int annee)
    {
        try {
            String sid=null;
            if(session.getAttribute("token")==null)
            {
                throw new Exception("pas de session valide");
            }
            sid=session.getAttribute("token").toString();
            List<StatSalarySlip> statSalarySlips=salarySlipStatisticsService.getStaticsDataFiltre(sid,annee);
            ModelAndView mv=new ModelAndView("statistique");
            mv.addObject("statistiques",statSalarySlips);
            return mv;
        } catch (Exception e) {
            e.printStackTrace();
            ModelAndView mvi=new ModelAndView("error");
            mvi.addObject("error",e.getMessage());
            return mvi;
        }
    }


    @GetMapping("/statistique")
    public ModelAndView getGeneralStatic(HttpSession session)
    {
        try {
            String sid=null;
            if(session.getAttribute("token")==null)
            {
                throw new Exception("pas de session valide");
            }
            sid=session.getAttribute("token").toString();
            List<StatSalarySlip> statSalarySlips=salarySlipStatisticsService.getStaticsData(sid);
            ModelAndView mv=new ModelAndView("statistique");
            mv.addObject("statistiques",statSalarySlips);
            return mv;
        } catch (Exception e) {
            e.printStackTrace();
            ModelAndView mvi=new ModelAndView("error");
            mvi.addObject("error",e.getMessage());
            return mvi;
        }
    }

}
