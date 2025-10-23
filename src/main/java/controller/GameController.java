package controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.NguoiChoiDAO;
import model.VatPhamDAO;
import model.KhachHangDAO;
import model.PlayerData;
import model.KhachHang;
import model.Item;
import util.DBConnection;

@WebServlet(name = "GameController", urlPatterns = {"/game"})
public class GameController extends HttpServlet {

   private NguoiChoiDAO nguoiChoiDAO;
    private VatPhamDAO vatPhamDAO;
    private KhachHangDAO khachHangDAO;

    private boolean databaseConnected = false;

    @Override
    public void init() throws ServletException {
        System.out.println("Đang khởi tạo GameController...");
        try {
            Connection testConn = DBConnection.getConnection();
            if (testConn != null) {
                System.out.println("Kết nối database thành công! ✅");
                testConn.close();
                this.databaseConnected = true;

                // Khởi tạo DAO chỉ khi kết nối thành công
                this.nguoiChoiDAO = new NguoiChoiDAO();
                this.vatPhamDAO = new VatPhamDAO();
                this.khachHangDAO = new KhachHangDAO();

                System.out.println("Khởi tạo GameController và các DAO thành công!");
            } else {
                System.out.println("LỖI KHỞI TẠO: Kết nối database thất bại! ❌");
                this.databaseConnected = false;
            }
        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng khi khởi tạo GameController: " + e.getMessage());
            e.printStackTrace();
            this.databaseConnected = false;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer playerId = (Integer) session.getAttribute("playerId");

        if (playerId == null) {
            playerId = 1;
            session.setAttribute("playerId", playerId);
        }

        String action = request.getParameter("action");
        
        // --- Xử lý Lỗi Kết nối DB ---
        if (!databaseConnected) {
            request.setAttribute("errorMessage", "LỖI KẾT NỐI DB: Dữ liệu game có thể bị thiếu hoặc sai lệch.");
        }

        PlayerData playerData = null;
        List<Item> availableItems = Collections.emptyList();
        List<KhachHang> customerProfiles = Collections.emptyList();
        KhachHang currentCustomer = null;

        try {
            if (databaseConnected) {
                // 1. TẢI DỮ LIỆU NGƯỜI CHƠI
                if ("new".equals(action)) {
                    nguoiChoiDAO.createNewPlayer(playerId, "Chủ Quán Mới");
                    playerData = nguoiChoiDAO.loadPlayerData(playerId);
                } else {
                    playerData = nguoiChoiDAO.loadPlayerData(playerId);

                    if (playerData == null) {
                        nguoiChoiDAO.createNewPlayer(playerId, "Player1");
                        playerData = nguoiChoiDAO.loadPlayerData(playerId);
                    }
                }
                
               // 2. Tải danh sách vật phẩm có sẵn (Giữ nguyên logic của bạn)
                availableItems = vatPhamDAO.getAllAvailableItems();
                
                // 3. Tải Hồ sơ Khách hàng Gốc (DEBUG HERE)
                System.out.println("🔎 DEBUG CONTROLLER: Bắt đầu gọi khachHangDAO.loadDailyCustomerProfiles()");
                customerProfiles = khachHangDAO.loadDailyCustomerProfiles(); 
                
                // 4. Gán Khách hàng hiện tại 
                if (customerProfiles != null && !customerProfiles.isEmpty()) {
                    
                    // DEBUG QUAN TRỌNG: Kiểm tra dữ liệu sau khi nhận từ DAO
                    System.out.println("✅ DEBUG CONTROLLER: Đã nhận danh sách khách hàng từ DAO. Số lượng: " + customerProfiles.size());
                    currentCustomer = customerProfiles.get(0); 
                    System.out.println("🔎 DEBUG CONTROLLER: Khách hàng hiện tại (đầu tiên): " + currentCustomer.toString());
                    
                } else {
                    System.out.println("⚠️ CẢNH BÁO: Danh sách hồ sơ khách hàng Gốc đang RỖNG. Không thể gán currentCustomer.");
                    currentCustomer = null;
                }
            } else {
                System.out.println("BỎ QUA: Bỏ qua tải dữ liệu game do lỗi kết nối DB.");
            }

            // 5. Set attributes cho JSP
            // *** ĐẢM BẢO TÊN BIẾN NÀY PHẢI KHỚP CHÍNH XÁC VỚI JSP: ${customerProfiles} ***
            request.setAttribute("customerProfiles", customerProfiles); 
            request.setAttribute("currentCustomer", currentCustomer); 
            request.setAttribute("playerData", playerData);
            request.setAttribute("availableItems", availableItems);
            
            // Kiểm tra Null cho Inventory
            if (playerData != null && playerData.inventory != null) {
                request.setAttribute("inventory", playerData.inventory);
            } else {
                request.setAttribute("inventory", Collections.emptyMap()); 
            }

            request.setAttribute("databaseConnected", databaseConnected);

            // LUÔN chuyển hướng đến game.jsp
            request.getRequestDispatcher("/game.jsp").forward(request, response);

        } catch (Exception e) {
            // Xử lý lỗi trong quá trình tải dữ liệu (SQLException)
            System.err.println("❌ Lỗi xử lý game khi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("errorMessage", "Lỗi dữ liệu game nghiêm trọng: " + e.getMessage());
            
            request.getRequestDispatcher("/game.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action != null) {
            System.out.println("Xử lý hành động game: " + action);
            // 🚨 LOGIC XỬ LÝ ACTIONS SẼ ĐƯỢC ĐẶT Ở ĐÂY
        }

        // Tải lại dữ liệu game sau khi xử lý POST
        doGet(request, response);
    }
}