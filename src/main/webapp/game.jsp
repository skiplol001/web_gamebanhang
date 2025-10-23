<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8"> 
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Cửa Hàng</title>
        <style>
            <%@include file="style_game.css" %>
        </style>
    </head>
    <body>
        <div id="main-container"> 

            <div id="top-panel">
                <div class="info-group">
                    <span class="label">Tên:</span>
                    <span id="player-name">${player.tenNguoiChoi}</span>
                </div>
                <div class="info-group">
                    <span class="label">Thời gian:</span>
                    <span id="game-time">00:00</span>
                </div>
                <div class="info-group">
                    <span class="label">Tiền:</span>
                    <span id="player-money">${player.tien}</span>
                </div>
                <button id="btn-setting">CÀI ĐẶT</button>
            </div>

            <div id="content-panel">
                <div id="shop-button-container">
                    <button id="btn-shop">CỬA HÀNG TIỆN LỢI</button>
                </div>
                <div id="game-background">
                    <button id="action-button">
                        <c:choose>
                            <c:when test="${empty currentCustomer}">
                                Nút hành động: Người chơi click vào để tiếp nhận vị khách này (chỉ có thể phục vụ mỗi khách một lần để tránh spam)
                            </c:when>
                            <c:otherwise>
                                Đang phục vụ: ${currentCustomer.ten}
                            </c:otherwise>
                        </c:choose>
                    </button>
                </div>
                <div id="bottom-panel">
                    <button id="btn-ttkh" class="customer-info-button">
                        Thông tin KH
                    </button>
                    <div id="interaction-panel">
                        <div id="spirit-container">
                            <span id="spirit-label">Điểm Tinh Thần Hiện Tại</span>
                            <div id="spirit-circle" style="--progress: ${player.diemTinhThan};">
                                <span id="spirit-value">${player.diemTinhThan}</span>
                            </div>
                        </div>
                        <div id="action-buttons">
                            <button id="btn-ban">BÁN</button>
                            <button id="btn-khong">KHÔNG</button>
                        </div>
                    </div>
                    <button id="btn-khtn" class="customer-info-button">
                        KH Trong Ngày
                    </button>
                </div>
            </div>

            <div id="dialogue-box">
                <div id="character-name-box">
                    <span id="character-name">
                        <c:choose>
                            <c:when test="${not empty currentCustomer}">
                                ${currentCustomer.ten}
                            </c:when>
                            <c:otherwise>
                                Nhân Vật XYZ
                            </c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <p id="dialogue-text">
                    <c:choose>
                        <c:when test="${not empty currentCustomer}">
                            Xin chào! Tôi muốn mua: 
                            <c:forEach items="${currentCustomer.vatPhamYeuCau}" var="item" varStatus="status">
                                ${item.key}<c:if test="${not status.last}">, </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            Đây là khu vực hiển thị hội thoại của nhân vật...
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>


            <div id="shop-overlay" class="hidden">
                <div id="shop-panel">
                    <div class="shop-header">
                        <h2>CỬA HÀNG TIỆN LỢI</h2>
                        <button id="btn-close-shop" class="close-button">×</button>
                    </div>
                    <div id="shop-content">
                        <div id="item-list-container">
                            <c:forEach items="${availableItems}" var="item">
                                <div class="shop-item-row">
                                    <div class="item-info">
                                        <span>${item.tenSP} - Giá: ${item.giaBan}$ - Tồn kho: 
                                            <c:set var="itemStock" value="${inventory[item.tenSP]}" />
                                            <c:out value="${empty itemStock ? 0 : itemStock}" />
                                        </span>
                                    </div>
                                    <button class="buy-button" data-item="${item.tenSP}">Mua x1</button>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                    <div id="shop-footer">
                        <button id="btn-shop-macca">MẶC CẢ</button>
                        <button id="btn-shop-tuido">TÚI ĐỒ (KHO HÀNG)</button>
                        <button id="btn-shop-hanghoa">HÀNG HÓA</button>
                    </div>
                </div>
            </div>


            <div id="inventory-overlay" class="hidden">
                <div id="inventory-panel">
                    <div class="inventory-header">
                        <h2>KHO HÀNG (TÚI ĐỒ)</h2>
                        <button id="btn-close-inventory" class="close-button">×</button>
                    </div>

                    <div id="inventory-content">
                        <div id="inventory-list-container">
                            <c:forEach items="${inventory}" var="item">
                                <c:if test="${item.value > 0}">
                                    <div class="inventory-item-row" data-item-type="consume">
                                        <div class="item-info">
                                            <span>${item.key} - Số lượng: ${item.value}</span>
                                            <p class="item-description">Hàng tồn kho hiện tại.</p>
                                        </div>
                                        <div class="inventory-actions">
                                            <button class="action-button sell-button" data-action="sell">Bán Lại</button>
                                            <button class="action-button inspect-button" data-action="inspect">Kiểm tra</button>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>

                            <div class="inventory-item-row" data-item-type="key-item">
                                <div class="item-info">
                                    <span>Giấy phép kinh doanh - Số lượng: 1</span>
                                    <p class="item-description">Chứng nhận cửa hàng hoạt động hợp pháp.</p>
                                </div>
                                <div class="inventory-actions">
                                    <button class="action-button inspect-button" data-action="inspect">Kiểm tra</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="inventory-footer">
                        <p style="color: #4A148C; font-weight: bold; margin: 0;">Quản lý kho hàng và công cụ</p>
                    </div>
                </div>
            </div>


            <div id="haggle-overlay" class="hidden">
                <div id="haggle-panel">
                    <div class="haggle-header">
                        <h2>MẶC CẢ VỚI CHỦ CỬA HÀNG</h2>
                        <button id="btn-close-haggle" class="close-button">×</button>
                    </div>

                    <div id="haggle-content">

                        <div id="haggle-selection-list">
                            <p class="selection-title">Chọn vật phẩm để mặc cả:</p>
                            <div id="haggle-item-selection-list">
                                <c:forEach items="${availableItems}" var="item">
                                    <button class="haggle-select-item" data-item="${item.tenSP}" data-price="${item.giaBan}" data-max-discount-percent="50">
                                        ${item.tenSP} - Giá: ${item.giaBan}$
                                    </button>
                                </c:forEach>
                            </div>
                        </div>

                        <div id="haggle-detail-panel" class="hidden">
                            <div id="haggle-item-info">
                                <h3 id="current-haggle-item-name"></h3>
                                <p>Giá gốc: <span id="original-price"></span> | Giảm tối đa: <span id="max-discount-value"></span></p>
                            </div>

                            <div id="haggle-form">
                                <div class="input-group">
                                    <label for="input-discount-money">Số tiền giảm ($):</label>
                                    <input type="number" id="input-discount-money" value="0" min="0">
                                </div>
                                <div class="input-group slider-group">
                                    <label>Phần trăm giảm:</label>
                                    <input type="range" id="input-discount-percent-slider" min="0" max="100" value="0">
                                    <span id="display-discount-percent">0%</span>
                                </div>
                            </div>

                            <div class="result-info">
                                <p>Tỷ lệ thành công: <span id="success-rate">50%</span></p>
                                <p>Giá mới: <span id="new-price"></span></p>
                            </div>
                        </div>
                    </div>

                    <div id="haggle-footer">
                        <button id="btn-back-haggle" class="hidden">QUAY LẠI</button>
                        <button id="btn-confirm-haggle">ĐỒNG Ý MẶC CẢ</button>
                        <button id="btn-cancel-haggle">HỦY</button>
                    </div>
                </div>
            </div>


            <div id="goods-overlay" class="hidden">
                <div id="goods-panel">
                    <div class="goods-header">
                        <h2>QUẢN LÝ HÀNG HÓA CỬA HÀNG</h2>
                        <button id="btn-close-goods" class="close-button">×</button>
                    </div>

                    <div class="tabs-container">
                        <button class="tab-button active" data-tab="kho-hang">Kho hàng</button>
                        <button class="tab-button" data-tab="mo-khoa">Mở khóa vật phẩm</button>
                    </div>

                    <div id="goods-content">

                        <div id="tab-kho-hang" class="goods-tab active">
                            <div class="goods-table-header">
                                <span class="col-name">Tên hàng hóa</span>
                                <span class="col-price">Giá nhập</span>
                                <span class="col-stock">Số lượng hiện có</span>
                                <span class="col-input">Số lượng nhập/trả</span>
                            </div>
                            <div id="kho-hang-list">
                                <c:forEach items="${availableItems}" var="item">
                                    <div class="goods-item-row">
                                        <span class="col-name">${item.tenSP}</span>
                                        <span class="col-price">${item.giaNhap}$</span>
                                        <span class="col-stock">
                                            <c:set var="itemStock" value="${inventory[item.tenSP]}" />
                                            <c:out value="${empty itemStock ? 0 : itemStock}" />
                                        </span>
                                        <input type="number" value="0" min="-10" class="col-input">
                                    </div>
                                </c:forEach>
                            </div>
                            <p class="tab-info">Dùng số dương để nhập thêm hàng, số âm để trả lại hàng.</p>
                        </div>

                        <div id="tab-mo-khoa" class="goods-tab hidden">
                            <div id="mo-khoa-list">
                                <div class="unlock-item-row" data-item="snack">
                                    <span class="unlock-item-name">Snack - Giá mở khóa: 300$</span>
                                    <button class="btn-unlock">Mở khóa</button>
                                </div>
                                <div class="unlock-item-row" data-item="coffee">
                                    <span class="unlock-item-name">Cà phê - Giá mở khóa: 400$</span>
                                    <button class="btn-unlock">Mở khóa</button>
                                </div>
                                <div class="unlock-item-row" data-item="cake">
                                    <span class="unlock-item-name">Bánh ngọt - Giá mở khóa: 350$</span>
                                    <button class="btn-unlock">Mở khóa</button>
                                </div>
                            </div>

                            <div class="unlock-info-box">
                                <p class="info-title">Thông tin mở khóa:</p>
                                <ul>
                                    <li>Mở khóa vật phẩm mới để bán trong cửa hàng.</li>
                                    <li>Mỗi vật phẩm có chi phí mở khóa khác nhau.</li>
                                    <li>Sau khi mở khóa, bạn có thể nhập hàng để bán.</li>
                                </ul>
                            </div>
                        </div>
                    </div>

                    <div class="goods-footer">
                        <button id="btn-apply-goods" class="footer-action-button primary">Áp dụng thay đổi</button>
                        <button id="btn-cancel-goods" class="footer-action-button secondary">Hủy</button>
                    </div>
                </div>
            </div>


            <div id="customer-info-overlay" class="hidden">
                <div id="customer-info-panel">
                    <div class="info-header">
                        <h2>THÔNG TIN KHÁCH HÀNG HIỆN TẠI</h2>
                        <button id="btn-close-customer-info" class="close-button">×</button>
                    </div>

                    <div id="customer-info-content">
                        <div id="customer-avatar-box">
                            </div>

                        <div id="customer-details">
                            <div class="info-row large">
                                <span class="info-label">Tên khách hàng:</span>
                                <span id="display-customer-name" class="info-value primary-value">
                                    <c:choose>
                                        <c:when test="${not empty currentCustomer}">
                                            ${currentCustomer.ten}
                                        </c:when>
                                        <c:otherwise>
                                            Chưa có khách
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>

                            <div class="info-row">
                                <span class="info-label">Mã khách hàng:</span>
                                <span id="display-customer-id" class="info-value">
                                    <c:if test="${not empty currentCustomer}">
                                        ${currentCustomer.maKH}
                                    </c:if>
                                </span>
                            </div>

                            <div class="info-row half-width">
                                <span class="info-label">Giới tính:</span>
                                <span id="display-customer-gender" class="info-value">
                                    <c:if test="${not empty currentCustomer}">
                                        ${currentCustomer.gioiTinh}
                                    </c:if>
                                </span>
                            </div>

                            <div class="info-row half-width">
                                <span class="info-label">Tuổi:</span>
                                <span id="display-customer-age" class="info-value">
                                    <c:if test="${not empty currentCustomer}">
                                        ${currentCustomer.tuoi}
                                    </c:if>
                                </span>
                            </div>
                        </div>
                    </div>

                    <div class="info-footer">
                        <p>Thông tin này giúp bạn đưa ra quyết định BÁN hoặc KHÔNG.</p>
                    </div>
                </div>
            </div>


            <div id="daily-customers-overlay" class="overlay hidden">
    <div id="daily-customers-panel" class="overlay-panel">
        <div class="daily-header">
            <h2>HỒ SƠ KHÁCH HÀNG TRONG NGÀY</h2>
            <button id="btn-close-daily-customers" class="close-button">×</button>
        </div>
        
        <div id="daily-customers-content">
            <div id="daily-customer-list">
                
                <c:choose>
                    <%-- Kiểm tra biến customerProfiles đã được đặt trong Controller --%>
                    <c:when test="${not empty customerProfiles}">
                        <c:forEach items="${customerProfiles}" var="customer" varStatus="status">
                            <div class="daily-customer-row clickable-customer" 
                                 data-customer-id="${customer.maKH}" 
                                 data-customer-name="${customer.ten}" 
                                 data-customer-gender="${customer.gioiTinh}" 
                                 data-customer-age="${customer.tuoi}">
                                <span class="customer-name-label">
                                    ${status.index + 1}. **${customer.ten}** (${customer.gioiTinh} | ${customer.tuoi} tuổi)
                                </span>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="daily-customer-row" style="text-align: center; color: red; font-style: italic;">
                            ⚠️ Không tải được dữ liệu khách hàng. (Danh sách rỗng)
                        </div>
                    </c:otherwise>
                </c:choose>
                </div>
                    <div class="daily-footer">
                        <p>Nhấp vào tên khách hàng để xem thông tin chi tiết.</p>
                    </div>
                </div>
            </div>

        </div>

        <form id="action-form" method="post" action="game" style="display: none;">
            <input type="hidden" name="action" id="form-action">
            <input type="hidden" name="itemName" id="form-item">
        </form>

        <script>
            // ==================== FORM SUBMISSION (CHỈ cho nút cần thiết) ====================
            function submitAction(action, itemName = '') {
                document.getElementById('form-action').value = action;
                document.getElementById('form-item').value = itemName;
                document.getElementById('action-form').submit();
            }

            // ==================== ACTION BUTTON (CHỈ HIỂN THỊ - KHÔNG GỬI FORM) ====================
            document.getElementById('action-button').addEventListener('click', function () {
                // CHỈ để thông báo người chơi đã nhận khách
                // KHÔNG gửi form, KHÔNG chuyển trang
                console.log("Người chơi đã nhấn nút hành động - đã nhận thông tin khách hàng");

                // Có thể thêm hiệu ứng visual nếu muốn
                this.style.backgroundColor = '#4CAF50';
                setTimeout(() => {
                    this.style.backgroundColor = '';
                }, 300);
            });

            // ==================== CÁC NÚT THỰC SỰ CẦN GỬI ACTION ====================
            document.getElementById('btn-ban').addEventListener('click', function () {
                submitAction('sell_item');
            });

            document.getElementById('btn-khong').addEventListener('click', function () {
                submitAction('reject_customer');
            });

            // ==================== BUY BUTTONS ====================
            document.querySelectorAll('.buy-button').forEach(btn => {
                btn.addEventListener('click', function () {
                    const itemName = this.getAttribute('data-item');
                    submitAction('buy_item', itemName);
                });
            });

            // ==================== SELL BUTTONS ====================
            document.querySelectorAll('.sell-button').forEach(btn => {
                btn.addEventListener('click', function () {
                    const itemName = this.closest('.inventory-item-row').querySelector('.item-info span').textContent.split(' - ')[0];
                    submitAction('sell_item_from_inventory', itemName); // Sửa action để tránh nhầm lẫn với nút BÁN chính
                });
            });

            // ==================== UNLOCK BUTTONS ====================
            document.querySelectorAll('.btn-unlock').forEach(btn => {
                btn.addEventListener('click', function () {
                    const itemName = this.closest('.unlock-item-row').getAttribute('data-item');
                    submitAction('unlock_item', itemName);
                });
            });
            
            // ==================== APPLY GOODS BUTTON ====================
            document.getElementById('btn-apply-goods').addEventListener('click', function () {
                 // Logic phức tạp hơn, có thể cần lấy tất cả giá trị input
                 // Để đơn giản, giả định gửi action chung, Controller sẽ tự đọc form
                submitAction('apply_goods_changes');
            });
            
            // ==================== CONFIRM HAGGLE BUTTON ====================
            document.getElementById('btn-confirm-haggle').addEventListener('click', function () {
                 // Logic phức tạp hơn, cần đọc giá trị mặc cả
                 // Giả định gửi action chung, Controller sẽ tự đọc form
                submitAction('confirm_haggle');
            });

            // ==================== JAVASCRIPT ORIGINAL (GIỮ NGUYÊN) ====================
            // Khai báo các biến DOM cho Panels
            const shopOverlay = document.getElementById('shop-overlay');
            const inventoryOverlay = document.getElementById('inventory-overlay');
            const haggleOverlay = document.getElementById('haggle-overlay');
            const goodsOverlay = document.getElementById('goods-overlay');
            const customerInfoOverlay = document.getElementById('customer-info-overlay');
            const dailyCustomersOverlay = document.getElementById('daily-customers-overlay');

            // Khai báo các biến DOM cho Haggle (Mặc cả)
            const haggleSelectionList = document.getElementById('haggle-selection-list');
            const haggleDetailPanel = document.getElementById('haggle-detail-panel');
            const btnBackHaggle = document.getElementById('btn-back-haggle');

            // ------------------ HÀM HỖ TRỢ ĐÓNG OVERLAYS ------------------
            function hideAllOverlays() {
                shopOverlay.classList.add('hidden');
                inventoryOverlay.classList.add('hidden');
                haggleOverlay.classList.add('hidden');
                goodsOverlay.classList.add('hidden');
                customerInfoOverlay.classList.add('hidden');
                dailyCustomersOverlay.classList.add('hidden');
            }

            // ------------------ LOGIC MỞ/ĐÓNG PANELS CHUNG ------------------
            document.getElementById('btn-shop').addEventListener('click', function () {
                hideAllOverlays();
                shopOverlay.classList.remove('hidden');
            });

            document.getElementById('btn-close-shop').addEventListener('click', function () {
                shopOverlay.classList.add('hidden');
            });

            // --- LOGIC CHUYỂN TỪ SHOP SANG PANELS PHỤ & QUAY LẠI ---
            document.getElementById('btn-shop-tuido').addEventListener('click', function () {
                shopOverlay.classList.add('hidden');
                inventoryOverlay.classList.remove('hidden');
            });

            document.getElementById('btn-close-inventory').addEventListener('click', function () {
                inventoryOverlay.classList.add('hidden');
                shopOverlay.classList.remove('hidden');
            });

            document.getElementById('btn-shop-macca').addEventListener('click', function () {
                shopOverlay.classList.add('hidden');
                haggleOverlay.classList.remove('hidden');
                haggleDetailPanel.classList.add('hidden');
                haggleSelectionList.classList.remove('hidden');
                btnBackHaggle.classList.add('hidden');
            });

            document.getElementById('btn-close-haggle').addEventListener('click', function () {
                haggleOverlay.classList.add('hidden');
                shopOverlay.classList.remove('hidden');
            });

            document.getElementById('btn-cancel-haggle').addEventListener('click', function () {
                haggleOverlay.classList.add('hidden');
                shopOverlay.classList.remove('hidden');
            });

            document.getElementById('btn-shop-hanghoa').addEventListener('click', function () {
                shopOverlay.classList.add('hidden');
                goodsOverlay.classList.remove('hidden');
            });

            document.getElementById('btn-close-goods').addEventListener('click', function () {
                goodsOverlay.classList.add('hidden');
                shopOverlay.classList.remove('hidden');
            });

            document.getElementById('btn-cancel-goods').addEventListener('click', function () {
                goodsOverlay.classList.add('hidden');
                shopOverlay.classList.remove('hidden');
            });

            // --- LOGIC KHÁCH HÀNG ---
            document.getElementById('btn-ttkh').addEventListener('click', function () {
                customerInfoOverlay.classList.remove('hidden');
            });

            document.getElementById('btn-close-customer-info').addEventListener('click', function () {
                customerInfoOverlay.classList.add('hidden');
            });

            document.getElementById('btn-khtn').addEventListener('click', function () {
                dailyCustomersOverlay.classList.remove('hidden');
            });

            document.getElementById('btn-close-daily-customers').addEventListener('click', function () {
                dailyCustomersOverlay.classList.add('hidden');
            });

            // ------------------ LOGIC MẶC CẢ ------------------
            const haggleSelectItems = document.querySelectorAll('#haggle-item-selection-list .haggle-select-item');

            haggleSelectItems.forEach(itemButton => {
                itemButton.addEventListener('click', function () {
                    haggleSelectionList.classList.add('hidden');
                    haggleDetailPanel.classList.remove('hidden');
                    btnBackHaggle.classList.remove('hidden');

                    const itemName = this.textContent.split(' - ')[0];
                    const itemPrice = parseFloat(this.getAttribute('data-price'));
                    const maxDiscountPercent = parseFloat(this.getAttribute('data-max-discount-percent'));
                    const maxDiscountValue = Math.floor((itemPrice * maxDiscountPercent) / 100);

                    document.getElementById('current-haggle-item-name').textContent = itemName;
                    document.getElementById('original-price').textContent = `${itemPrice}$`;
                    document.getElementById('max-discount-value').textContent = `${maxDiscountValue}$ (${maxDiscountPercent}%)`;

                    document.getElementById('input-discount-money').value = 0;
                    document.getElementById('input-discount-percent-slider').value = 0;
                    document.getElementById('display-discount-percent').textContent = '0%';
                    document.getElementById('new-price').textContent = `${itemPrice}$`;
                    document.getElementById('success-rate').textContent = '50%';
                });
            });

            btnBackHaggle.addEventListener('click', function () {
                haggleDetailPanel.classList.add('hidden');
                haggleSelectionList.classList.remove('hidden');
                this.classList.add('hidden');
            });
            
            // Thêm logic cập nhật giá và tỷ lệ thành công khi thay đổi input/slider
            // *LƯU Ý: Đây là logic phía Client-Side. Logic tính toán thực tế (tỷ lệ thành công) NÊN được xử lý ở Controller/Server.
            function updateHaggleResults() {
                const itemPriceElement = document.getElementById('original-price');
                const itemPriceText = itemPriceElement.textContent.replace('$', '');
                if (!itemPriceText) return;

                const originalPrice = parseFloat(itemPriceText);
                const maxDiscountElement = document.getElementById('max-discount-value');
                const maxDiscountText = maxDiscountElement.textContent.match(/\(([^%]+)%\)/);
                const maxDiscountPercent = maxDiscountText ? parseFloat(maxDiscountText[1]) : 50; 

                const moneyDiscount = parseFloat(document.getElementById('input-discount-money').value) || 0;
                const percentDiscount = parseFloat(document.getElementById('input-discount-percent-slider').value) || 0;
                
                // Tính toán tổng số tiền giảm từ tiền mặt và phần trăm
                let totalDiscount = moneyDiscount;
                totalDiscount += Math.floor((originalPrice * percentDiscount) / 100);
                
                // Giới hạn giảm giá (ví dụ: không quá 50% giá gốc, cần lấy giá trị thực tế từ data-max-discount-percent)
                const maxDiscountValue = Math.floor((originalPrice * maxDiscountPercent) / 100);
                totalDiscount = Math.min(totalDiscount, maxDiscountValue);
                
                const newPrice = originalPrice - totalDiscount;
                
                // Cập nhật hiển thị
                document.getElementById('new-price').textContent = `${newPrice}$`;
                document.getElementById('display-discount-percent').textContent = `${percentDiscount}%`;
                
                // Tính toán Tỷ lệ thành công (ví dụ đơn giản: tỷ lệ nghịch với phần trăm mặc cả, cần logic phức tạp hơn ở Server)
                // Giả định: 50% là cơ bản, giảm 1% giá là giảm 1% tỷ lệ thành công.
                const totalDiscountPercent = Math.floor((totalDiscount / originalPrice) * 100);
                let successRate = Math.max(10, 50 - totalDiscountPercent); // Tối thiểu 10%
                document.getElementById('success-rate').textContent = `${successRate}%`;
            }

            document.getElementById('input-discount-money').addEventListener('input', updateHaggleResults);
            document.getElementById('input-discount-percent-slider').addEventListener('input', updateHaggleResults);

            // ------------------ LOGIC CHUYỂN TAB ------------------
            const tabButtons = document.querySelectorAll('#goods-panel .tab-button');
            const goodsTabs = document.querySelectorAll('#goods-content .goods-tab');

            tabButtons.forEach(button => {
                button.addEventListener('click', function () {
                    const targetTabId = this.getAttribute('data-tab');

                    goodsTabs.forEach(tab => tab.classList.add('hidden'));
                    tabButtons.forEach(btn => btn.classList.remove('active'));

                    document.getElementById('tab-' + targetTabId).classList.remove('hidden');
                    this.classList.add('active');
                });
            });

            // ------------------ LOGIC MỞ TTKH TỪ DANH SÁCH KH TRONG NGÀY ------------------
            const customerRows = document.querySelectorAll('#daily-customer-list .clickable-customer');

            customerRows.forEach(row => {
                row.addEventListener('click', function () {
                    const name = this.getAttribute('data-customer-name');
                    const id = this.getAttribute('data-customer-id');
                    const gender = this.getAttribute('data-customer-gender');
                    const age = this.getAttribute('data-customer-age');

                    document.getElementById('display-customer-name').textContent = name;
                    document.getElementById('display-customer-id').textContent = '#' + id;
                    document.getElementById('display-customer-gender').textContent = gender;
                    document.getElementById('display-customer-age').textContent = age;

                    dailyCustomersOverlay.classList.add('hidden');
                    customerInfoOverlay.classList.remove('hidden');
                });
            });

        </script>
    </body>
</html>