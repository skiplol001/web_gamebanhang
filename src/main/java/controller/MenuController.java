package controller; 

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Ánh xạ URL: /Menu
@WebServlet(name = "MenuController", urlPatterns = {"/Menu"}) 
public class MenuController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        String redirectUrl = "GameController"; // URL cơ bản của game controller

        if ("new".equals(action)) {
            // Thêm logic khởi tạo game mới nếu cần
            redirectUrl += "?action=new";
        } else if ("continue".equals(action)) {
            // Thêm logic tải game cũ nếu cần
            redirectUrl += "?action=continue";
        }
        
        // Sử dụng sendRedirect để trình duyệt tự động chuyển URL và hiển thị GameController
        // Điều này sẽ thay đổi URL trên thanh địa chỉ của trình duyệt.
        response.sendRedirect(request.getContextPath() + "/" + redirectUrl);
    }
}