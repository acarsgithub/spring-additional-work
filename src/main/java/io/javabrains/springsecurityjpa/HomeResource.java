package io.javabrains.springsecurityjpa;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.swing.plaf.nimbus.State;
import java.security.Principal;
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

    // ############################################################################################################

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

    // ############################################################################################################


    /*
        Method: transaction
        Purpose: This method will take two request parameters (id and amount) and will transfer the amount from
        the currently logged in user's bank account to the user with bank account id 'id'

        Request Parameter id - the id to transfer money to
        Request Parameter amount - the amount to transfer

     */
    @GetMapping("/transaction")
    @ResponseBody
    public String transaction(@RequestParam("id") int id, @RequestParam("amount") double amount, Principal principal){

        // Connection and Statement for SQL
        Connection conn = null;
        Statement stmt = null;

        // Obtain username of logged in user
        String loggedInUser = principal.getName();

        // Using prepared statements for SQL injection safety
        PreparedStatement userBankValue = null;
        PreparedStatement receiverBankValue = null;
        PreparedStatement receiverUpdate = null;
        PreparedStatement userUpdate = null;

        // Variables to store account values later
        double loggedInUserAccountValue = -1;
        double receiverAccountValue = -1;

        try {
            // Open connection and execute query
            conn = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/springsecurity?allowMultiQueries=true", "root", "");
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            // Obtains bank account value of logged in user
            String obtainBankValue = "SELECT bank_account_value FROM user WHERE user_name= ?";

            // Fighting against SQL Injections
            userBankValue = conn.prepareStatement(obtainBankValue);
            userBankValue.setString(1, loggedInUser);
            ResultSet userBankValueRS = userBankValue.executeQuery();

            // Pulling in user bank account value and subtracting amount from it
            if(userBankValueRS.next()){
                loggedInUserAccountValue = userBankValueRS.getDouble("bank_account_value");
                loggedInUserAccountValue = loggedInUserAccountValue - amount;
            }

            // Obtains bank account value of receiver
            String obtainBankValueOfReciever = "SELECT bank_account_value FROM user WHERE id = ?";

            // Fighting against SQL Injections
            receiverBankValue = conn.prepareStatement(obtainBankValueOfReciever);
            receiverBankValue.setInt(1, id);
            ResultSet receiverBankValueRS = receiverBankValue.executeQuery();

            // Pulling in receiver bank account value and adding amount to it
            if(receiverBankValueRS.next()){
                System.out.println(receiverBankValueRS.getDouble("bank_account_value"));
                receiverAccountValue = receiverBankValueRS.getDouble("bank_account_value");
                receiverAccountValue = receiverAccountValue + amount;
            }

            // Strings for updating values in SQL database
            String updateUser = "UPDATE user SET bank_account_value = ? WHERE user_name = ?";
            String updateReceiver = "UPDATE user SET bank_account_value = ? WHERE id = ?";

            // Using Prepared Statement for SQL Injection Prevention
            userUpdate = conn.prepareStatement(updateUser);
            userUpdate.setDouble(1, loggedInUserAccountValue);
            userUpdate.setString(2, loggedInUser);
            userUpdate.executeUpdate();
            System.out.println("User: " + loggedInUserAccountValue + ", Receiver: " + receiverAccountValue);
            receiverUpdate = conn.prepareStatement(updateReceiver);
            receiverUpdate.setDouble(1, receiverAccountValue);
            receiverUpdate.setInt(2, id);
            receiverUpdate.executeUpdate();


        } catch (Exception se) { se.printStackTrace(); }
        // Close Resources
        try {
            if (conn != null && stmt != null)
                conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return "<h2><center>Transaction successful!</center></h2>";
    }

    /*
        Method: csrf
        Purpose: This method has a link that uses the transaction method to transfer money from the logged in user's account
        to the malicious user's account
        Implementing Vulnerability Example: Just log in as the user that's going to get scammed and click the link!
     */
    @GetMapping("/csrf")
    @ResponseBody
    public String csrf(){
        return "<a href='http://localhost:8080/transaction?id=3&amount=1000'>Definitely Not Malicious CSRF Link (But it totally is)</a>";
    }


}


