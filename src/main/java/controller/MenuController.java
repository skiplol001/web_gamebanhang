package controller; 

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MenuController", urlPatterns = {"/Menu"}) 
public class MenuController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // SỬA: Dùng "game" thay vì "GameController"
        String redirectUrl = "game.jsp"; 
        
        if ("new".equals(action)) {
            redirectUrl += "?action=new";
        } else if ("continue".equals(action)) {
            redirectUrl += "?action=continue";
        }
        
        String fullRedirectPath = request.getContextPath() + "/" + redirectUrl;
        response.sendRedirect(fullRedirectPath);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}