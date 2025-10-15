package controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

    @WebServlet(name = "GameController", urlPatterns = {"/GameController"})
public class GameController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("new".equals(action)) {
            // 1. Logic: Tạo đối tượng Game mới, Player mới.
            // 2. Lưu vào Session hoặc Context.
            // request.setAttribute("playerData", new Player(...));

        } else if ("continue".equals(action)) {
            // 1. Logic: Tải dữ liệu Game/Player từ Database.
            // 2. Lưu vào Session hoặc Context.
            // request.setAttribute("playerData", loadedPlayer);
        }

        // Cuối cùng, điều hướng đến trang chơi game (index_game.html)
        request.getRequestDispatcher("/index_game.html").forward(request, response);
    }
}

