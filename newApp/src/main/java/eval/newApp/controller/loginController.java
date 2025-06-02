package eval.newApp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import eval.newApp.service.*;
import  eval.newApp.modele.login.*;


@RequestMapping("/login")
@Controller
public class loginController
{
    @Autowired
    ErpLoginService erpLoginService;



    @GetMapping("/formulaire")
    public ModelAndView getPageLogin()
    {
        ModelAndView mv=new ModelAndView("login");
        return mv;
    }

    @PostMapping("/verifyLogin")
    public ModelAndView verifyLogin(@RequestParam("usr") String usr, @RequestParam("pwd") String pwd, HttpSession session)
    {

        try
        {
            ModelAndView mv=new ModelAndView("accueil");
            LoginRequest loginRequest=new LoginRequest(usr,pwd);
            LoginResponseHeaders loginResponseHeaders=erpLoginService.loginToErp(loginRequest);
            session.setAttribute("headers",loginResponseHeaders);
            session.setAttribute("token",loginResponseHeaders.getSid());
            return mv;
        }
        catch (Exception e)
        {
            return  new ModelAndView("login");
        }
    }


}
