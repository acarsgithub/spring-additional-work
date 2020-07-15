package io.javabrains.springsecurityjpa;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
public class HomeResource {


    /*
        Method: xssStringConcatenation
        Purpose: This method is a simple get request to demonstrate how an unchecked HTML String Concatenation could
        lead to an XSS Injection
        RequestParameter xss - this request parameter is where the vulnerability lies and injection occurs

        Implementing Vulnerability Example: injection script code fro basic alert as 'xss' variable
        LINK EXAMPLE: http://localhost:8080/xss-concat?xss=%3Cscript%3Ealert(%27XSS!%27)%3C/script%3E
     */
    @GetMapping("/xss-concat")
    public String xssStringConcatenation(@RequestParam("xss") String xss) {
        return ("<h2><center>HTML String Concatenation for XSS Injection.... " + xss + "!</center></h2>");
    }

}


