package controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "GameController", urlPatterns = {"/GameController"})
public class GameController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đã có Filter, nhưng đặt lại để đảm bảo 100% (an toàn và nên làm)
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");

        // Ví dụ giả lập logic
        if ("new".equals(action)) {
            request.setAttribute("playerName", "Người Chơi Mới");
        } else if ("continue".equals(action)) {
             request.setAttribute("playerName", "Quý Ông Bán Hàng");
        }
        
        // Truyền dữ liệu tĩnh/động cho JSP
        request.setAttribute("playerMoney", 5000); 
        
        // Điều hướng đến index_game.jsp
        request.getRequestDispatcher("/index_game.jsp").forward(request, response);
    }
}