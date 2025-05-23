package com.group8.alomilktea.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {
    @RequestMapping("/admin")
    public String dashboard(ModelMap modelMap){
        return "admin/index";
    }
}
