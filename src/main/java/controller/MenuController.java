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
        
        // Lấy tham số 'action' từ nút bấm (new/continue)
        String action = request.getParameter("action");
        
        // Tên URL pattern của Servlet tiếp theo (giả định là /GameController)
        String redirectUrl = "GameController"; 
        
        if ("new".equals(action)) {
            // Chuyển hướng đến GameController với tham số action=new
            redirectUrl += "?action=new";
        } else if ("continue".equals(action)) {
            // Chuyển hướng đến GameController với tham số action=continue
            redirectUrl += "?action=continue";
        }
        
        // SỬ DỤNG ContextPath ĐỂ CHUYỂN HƯỚNG TUYỆT ĐỐI VÀ CHÍNH XÁC
        String fullRedirectPath = request.getContextPath() + "/" + redirectUrl;
        response.sendRedirect(fullRedirectPath);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Nên cân nhắc việc xử lý POST nếu các nút menu được gửi bằng form
        doGet(request, response);
    }
}