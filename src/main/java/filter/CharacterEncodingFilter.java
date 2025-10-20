package filter; // Đặt tên package của bạn

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

// Áp dụng bộ lọc cho mọi URL ("/*")
@WebFilter("/*")
public class CharacterEncodingFilter implements Filter {

    // Bộ lọc thực hiện trước khi request đến Servlet
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // 1. Đặt mã hóa cho REQUEST (dữ liệu gửi đến Server)
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8");
        }
        
        // 2. Đặt mã hóa cho RESPONSE (dữ liệu gửi về Browser)
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // Tiếp tục chuỗi xử lý (đến Servlet hoặc JSP)
        chain.doFilter(request, response);
    }
    
    // Các phương thức bắt buộc của interface Filter
    public void init(FilterConfig fConfig) throws ServletException {}
    public void destroy() {}
}