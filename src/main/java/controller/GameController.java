package controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.QuanLyKhachHang;
import model.DatabaseStorage;
import model.PlayerData;
import model.KhachHang;
import model.Item;
import util.DBConnection;
import util.SimpleDataSource;

@WebServlet(name = "GameController", urlPatterns = {"/game"})
public class GameController extends HttpServlet {
    private QuanLyKhachHang quanLyKhachHang;
    private boolean databaseConnected = false;
    
    // Danh sách items mặc định
    private List<Item> availableItems;

    @Override
    public void init() throws ServletException {
        try {
            System.out.println("Đang khởi tạo GameController...");
            
            // Khởi tạo danh sách items mặc định
            initializeDefaultItems();
            
            // Test kết nối database
            Connection testConn = DBConnection.getConnection();
            if (testConn != null) {
                System.out.println("Kết nối database thành công!");
                testConn.close();
                
                // Tạo DataSource và khởi tạo QuanLyKhachHang
                SimpleDataSource dataSource = new SimpleDataSource();
                this.quanLyKhachHang = new QuanLyKhachHang(dataSource);
                this.databaseConnected = true;
                System.out.println("Khởi tạo GameController thành công!");
            } else {
                System.out.println("Kết nối database thất bại!");
                this.databaseConnected = false;
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo GameController: " + e.getMessage());
            e.printStackTrace();
            this.databaseConnected = false;
        }
    }

    private void initializeDefaultItems() {
        availableItems = new ArrayList<>();
        // Thêm các items mặc định
        availableItems.add(new Item("Bánh mì", 50, 0, 25, "Bánh mì thơm ngon", "food"));
        availableItems.add(new Item("Nước suối", 30, 0, 10, "Nước suối tinh khiết", "drink"));
        availableItems.add(new Item("Thuốc", 100, 500, 60, "Thuốc chữa bệnh", "medicine"));
        availableItems.add(new Item("Snack", 20, 300, 12, "Snack giòn tan", "food"));
        availableItems.add(new Item("Cà phê", 80, 400, 40, "Cà phê thơm", "drink"));
        availableItems.add(new Item("Bánh ngọt", 60, 350, 30, "Bánh ngọt hảo hạng", "food"));
        System.out.println("Đã khởi tạo " + availableItems.size() + " items mặc định");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer playerId = (Integer) session.getAttribute("playerId");
        
        // Nếu chưa có playerId, tạo mới với ID = 1
        if (playerId == null) {
            playerId = 1; // Sử dụng ID số theo database
            session.setAttribute("playerId", playerId);
        }
        
        String action = request.getParameter("action");
        
        try {
            // Kiểm tra kết nối database
            if (!databaseConnected || quanLyKhachHang == null) {
                handleOfflineMode(request, response);
                return;
            }
            
            // Tạo DataSource từ DBConnection
            SimpleDataSource dataSource = new SimpleDataSource();
            
            // Load player data từ database - chuyển ID sang String để phù hợp với method
            PlayerData playerData = DatabaseStorage.loadPlayerData(String.valueOf(playerId), dataSource);
            
            if ("new".equals(action)) {
                // Tạo game mới - tạo danh sách khách hàng mới
                quanLyKhachHang.layDanhSachKhachHangHomNay();
                playerData.currentDay = 1;
                playerData.money = 1000;
                playerData.mentalPoints = 100;
                // Lưu player data mới
                DatabaseStorage.savePlayerData(String.valueOf(playerId), playerData, dataSource);
            }
            
            // Lấy danh sách khách hàng hiện tại
            List<KhachHang> danhSachKhachHang = quanLyKhachHang.taiDanhSachKhachHang();
            
            // Set attributes cho JSP - QUAN TRỌNG: thêm availableItems
            request.setAttribute("danhSachKhachHang", danhSachKhachHang);
            request.setAttribute("playerData", playerData);
            request.setAttribute("soKhachVong", quanLyKhachHang.demSoKhachVong(danhSachKhachHang));
            request.setAttribute("soKhachThuong", quanLyKhachHang.demSoKhachThuong(danhSachKhachHang));
            request.setAttribute("availableItems", availableItems); // THÊM DÒNG NÀY
            request.setAttribute("inventory", playerData.inventory); // THÊM DÒNG NÀY
            request.setAttribute("databaseConnected", true);
            
            request.getRequestDispatcher("/game.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Lỗi xử lý game: " + e.getMessage());
            e.printStackTrace();
            handleOfflineMode(request, response);
        }
    }

    private void handleOfflineMode(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Tạo dữ liệu mẫu cho chế độ offline
        PlayerData playerData = new PlayerData();
        playerData.money = 1000;
        playerData.mentalPoints = 100;
        playerData.currentDay = 1;
        
        // Tạo inventory mẫu
        playerData.inventory.put("Bánh mì", 5);
        playerData.inventory.put("Nước suối", 3);
        
        // Tạo danh sách khách hàng mẫu
        List<KhachHang> danhSachKhachHang = createSampleCustomers();
        
        request.setAttribute("danhSachKhachHang", danhSachKhachHang);
        request.setAttribute("playerData", playerData);
        request.setAttribute("soKhachVong", 3);
        request.setAttribute("soKhachThuong", 5);
        request.setAttribute("availableItems", availableItems); // THÊM DÒNG NÀY
        request.setAttribute("inventory", playerData.inventory); // THÊM DÒNG NÀY
        request.setAttribute("databaseConnected", false);
        request.setAttribute("errorMessage", "Đang ở chế độ offline. Không thể kết nối database.");
        
        request.getRequestDispatcher("/game.jsp").forward(request, response);
    }
    
    private List<KhachHang> createSampleCustomers() {
        List<KhachHang> customers = new ArrayList<>();
        customers.add(new KhachHang("Liễu Như Yên", 25, "Nữ", "KH001", false));
        customers.add(new KhachHang("Tạ Minh Kha", 30, "Nam", "KH002", false));
        customers.add(new KhachHang("Tiểu Lạc", 18, "Nữ", "KH003", false));
        customers.add(new KhachHang("Shyn Mụi Mụi", 20, "Nữ", "KH004", true));
        customers.add(new KhachHang("Tăng Quốc Cường", 28, "Nam", "KH005", false));
        customers.add(new KhachHang("Huyền Thanh Tố Uyển", 19, "Nữ", "KH010", false));
        return customers;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}