package io.javabrains.springsecurityjpa;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;

@Controller
public class HomeResource {

    /*
        Method: xssStringConcatenation
        Purpose: This method is a simple get request to demonstrate how an unchecked HTML String Concatenation could
        lead to an XSS Injection
        RequestParameter xss - this request parameter is where the vulnerability lies and injection occurs

        Implementing Vulnerability Example: injection script code for basic alert as 'xss' variable
        LINK EXAMPLE: http://localhost:8080/xss-concat?xss=%3Cscript%3Ealert(%27XSS!%27)%3C/script%3E
     */
    @GetMapping("/xss-concat")
    @ResponseBody
    public String xssStringConcatenation(@RequestParam("xss") String xss) {
        return ("<h2><center>HTML String Concatenation for XSS Injection.... " + xss + "!</center></h2>");
    }


    /*
        Method: xssThymeleaf
        Purpose: This method is a simple get request to demonstrate how an unchecked Request Parameter could
        lead to an XSS Injection in Thymeleaf through unescaped text
        RequestParameter xss - this request parameter is where the vulnerability lies and injection occurs

        Implementing Vulnerability Example: injection script code for basic alert as 'xss' variable
        LINK EXAMPLE: http://localhost:8080/xss-thyme?xss=%3Cscript%3Ealert(%27XSS!%27)%3C/script%3E
     */
    @GetMapping("/xss-thyme")
    public String xssThymeleaf(@RequestParam("xss") String xss, Model model){

        // Adding model and view attribute to pass variable to index.html file
        model.addAttribute("xss", xss);

        // calling index.html file in templates folder
        return("index");
    }




}


