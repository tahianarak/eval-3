package eval.newApp.controller;

import eval.newApp.service.MysqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MysqlController
{
    @Autowired
    MysqlService mysqlService;


    @GetMapping("/testMysql")
    public void showComapny() throws Exception {
        System.out.println(mysqlService.showConnection());
    }
}
