<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Khi b?n ?� th�m jstl-1.2.jar v�o WEB-INF/lib, h�y b? comment d�ng n�y: --%>
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> --%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"> 
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>C?a H�ng Hoang</title>
    <link rel="stylesheet" href="style_game.css"> 
</head>
<body>
    <div id="main-container"> 
        
        <div id="top-panel">
            <div class="info-group">
                <span class="label">T�n:</span>
                <span id="player-name">Player</span>
            </div>
            <div class="info-group">
                <span class="label">Th?i gian:</span>
                <span id="game-time">00:00</span>
            </div>
            <div class="info-group">
                <span class="label">Ti?n:</span>
                <span id="player-money">0000</span>
            </div>
            <button id="btn-setting">??</button>
        </div>

        <div id="content-panel">
            <div id="shop-button-container">
                <button id="btn-shop">C?A H�NG TI?N L?I</button>
            </div>
            <div id="game-background">
                <button id="action-button">
                    button n?i ng??i ch?i click v�o ?? game hi?u ng??i ch?i ?� ti?p nh?n v? kh�ch n�y (ch? c� th? ph?c v? m?i kh�ch tr�nh vi?c spam)
                </button>
            </div>
            <div id="bottom-panel">
                <button id="btn-ttkh" class="customer-info-button">
                    Th�ng tin KH
                </button>
                <div id="interaction-panel">
                    <div id="spirit-container">
                        <span id="spirit-label">?i?m Tinh Th?n Hi?n T?i</span>
                        <div id="spirit-circle" style="--progress: 100;">
                            <span id="spirit-value">100</span>
                        </div>
                    </div>
                    <div id="action-buttons">
                        <button id="btn-ban">B�N</button>
                        <button id="btn-khong">KH�NG</button>
                    </div>
                </div>
                <button id="btn-khtn" class="customer-info-button">
                    KH Trong Ng�y
                </button>
            </div>
        </div>
        
        <div id="dialogue-box">
            <div id="character-name-box">
                <span id="character-name">Nh�n V?t XYZ</span>
            </div>
            <p id="dialogue-text">?�y l� khu v?c hi?n th? h?i tho?i c?a nh�n v?t...</p>
        </div>

        
        <div id="shop-overlay" class="hidden">
            <div id="shop-panel">
                <div class="shop-header">
                    <h2>C?A H�NG TI?N L?I</h2>
                    <button id="btn-close-shop" class="close-button">?</button>
                </div>
                <div id="shop-content">
                    <div id="item-list-container">
                        <div class="shop-item-row">
                            <div class="item-info">
                                <span>B�nh m� - Gi�: 50$ - T?n kho: 100</span>
                            </div>
                            <button class="buy-button" data-item="banh_mi">Mua x1</button>
                        </div>
                        <div class="shop-item-row">
                            <div class="item-info">
                                <span>N??c su?i - Gi�: 100$ - T?n kho: 5</span>
                            </div>
                            <button class="buy-button low-stock" data-item="nuoc_suoi">Mua x1</button>
                        </div>
                    </div>
                </div>
                <div id="shop-footer">
                    <button id="btn-shop-macca">M?C C?</button>
                    <button id="btn-shop-tuido">T�I ?? (KHO H�NG)</button>
                    <button id="btn-shop-hanghoa">H�NG H�A</button>
                </div>
            </div>
        </div>
        
        
        <div id="inventory-overlay" class="hidden">
            <div id="inventory-panel">
                <div class="inventory-header">
                    <h2>KHO H�NG (T�I ??)</h2>
                    <button id="btn-close-inventory" class="close-button">?</button>
                </div>
                
                <div id="inventory-content">
                    <div id="inventory-list-container">
                        <div class="inventory-item-row" data-item-type="consume">
                            <div class="item-info">
                                <span>B�nh m� - S? l??ng: 30</span>
                                <p class="item-description">H�ng t?n kho hi?n t?i.</p>
                            </div>
                            <div class="inventory-actions">
                                <button class="action-button sell-button" data-action="sell">B�n L?i</button>
                                <button class="action-button inspect-button" data-action="inspect">Ki?m tra</button>
                            </div>
                        </div>
                        <div class="inventory-item-row" data-item-type="key-item">
                            <div class="item-info">
                                <span>Gi?y ph�p Kinh Doanh - S? l??ng: 1</span>
                                <p class="item-description">Ch?ng nh?n c?a h�ng ho?t ??ng h?p ph�p.</p>
                            </div>
                            <div class="inventory-actions">
                                <button class="action-button inspect-button" data-action="inspect">Ki?m tra</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div id="inventory-footer">
                    <p style="color: #4A148C; font-weight: bold; margin: 0;">Qu?n l� kho h�ng v� c�ng c?</p>
                </div>
            </div>
        </div>
        
        
        <div id="haggle-overlay" class="hidden">
            <div id="haggle-panel">
                <div class="haggle-header">
                    <h2>M?C C? V?I CH? C?A H�NG</h2>
                    <button id="btn-close-haggle" class="close-button">?</button>
                </div>
                
                <div id="haggle-content">
                    
                    <div id="haggle-selection-list">
                        <p class="selection-title">Ch?n v?t ph?m ?? m?c c?:</p>
                        <div id="haggle-item-selection-list">
                            <button class="haggle-select-item" data-item="banh_mi" data-price="50" data-max-discount-percent="50">
                                B�nh m� - Gi�: 50$
                            </button>
                            <button class="haggle-select-item" data-item="nuoc_suoi" data-price="30" data-max-discount-percent="33">
                                N??c su?i - Gi�: 30$
                            </button>
                            <button class="haggle-select-item" data-item="thuoc" data-price="100" data-max-discount-percent="25">
                                Thu?c - Gi�: 100$
                            </button>
                            <button class="haggle-select-item" data-item="snack" data-price="20" data-max-discount-percent="60">
                                Snack - Gi�: 20$
                            </button>
                        </div>
                    </div>
                    
                    <div id="haggle-detail-panel" class="hidden">
                        <div id="haggle-item-info">
                            <h3 id="current-haggle-item-name"></h3>
                            <p>Gi� g?c: <span id="original-price"></span> | Gi?m t?i ?a: <span id="max-discount-value"></span></p>
                        </div>

                        <div id="haggle-form">
                            <div class="input-group">
                                <label for="input-discount-money">S? ti?n gi?m ($):</label>
                                <input type="number" id="input-discount-money" value="0" min="0">
                            </div>
                            <div class="input-group slider-group">
                                <label>Ph?n tr?m gi?m:</label>
                                <input type="range" id="input-discount-percent-slider" min="0" max="100" value="0">
                                <span id="display-discount-percent">0%</span>
                            </div>
                        </div>

                        <div class="result-info">
                            <p>T? l? th�nh c�ng: <span id="success-rate">50%</span></p>
                            <p>Gi� m?i: <span id="new-price"></span></p>
                        </div>
                    </div>
                </div>

                <div id="haggle-footer">
                    <button id="btn-back-haggle" class="hidden">QUAY L?I</button>
                    <button id="btn-confirm-haggle">??NG � M?C C?</button>
                    <button id="btn-cancel-haggle">H?Y</button>
                </div>
            </div>
        </div>


        <div id="goods-overlay" class="hidden">
            <div id="goods-panel">
                <div class="goods-header">
                    <h2>QU?N L� H�NG H�A C?A H�NG</h2>
                    <button id="btn-close-goods" class="close-button">?</button>
                </div>
                
                <div class="tabs-container">
                    <button class="tab-button active" data-tab="kho-hang">Kho h�ng</button>
                    <button class="tab-button" data-tab="mo-khoa">M? kh�a v?t ph?m</button>
                </div>

                <div id="goods-content">
                    
                    <div id="tab-kho-hang" class="goods-tab active">
                        <div class="goods-table-header">
                            <span class="col-name">T�n h�ng h�a</span>
                            <span class="col-price">Gi� nh?p</span>
                            <span class="col-stock">S? l??ng hi?n c�</span>
                            <span class="col-input">S? l??ng nh?p/tr?</span>
                        </div>
                        <div id="kho-hang-list">
                            <div class="goods-item-row">
                                <span class="col-name">B�nh m�</span>
                                <span class="col-price">50$</span>
                                <span class="col-stock">10</span>
                                <input type="number" value="0" min="-10" class="col-input">
                            </div>
                            <div class="goods-item-row">
                                <span class="col-name">N??c su?i</span>
                                <span class="col-price">30$</span>
                                <span class="col-stock">10</span>
                                <input type="number" value="0" min="-10" class="col-input">
                            </div>
                            <div class="goods-item-row">
                                <span class="col-name">Thu?c</span>
                                <span class="col-price">100$</span>
                                <span class="col-stock">10</span>
                                <input type="number" value="0" min="-10" class="col-input">
                            </div>
                        </div>
                        <p class="tab-info">D�ng s? d??ng ?? nh?p th�m h�ng, s? �m ?? tr? l?i h�ng.</p>
                    </div>

                    <div id="tab-mo-khoa" class="goods-tab hidden">
                        <div id="mo-khoa-list">
                            <div class="unlock-item-row" data-item="snack">
                                <span class="unlock-item-name">Snack - Gi� m? kh�a: 300$</span>
                                <button class="btn-unlock">M? kh�a</button>
                            </div>
                             <div class="unlock-item-row" data-item="coffee">
                                <span class="unlock-item-name">C� ph� - Gi� m? kh�a: 400$</span>
                                <button class="btn-unlock">M? kh�a</button>
                            </div>
                            <div class="unlock-item-row" data-item="cake">
                                <span class="unlock-item-name">B�nh ng?t - Gi� m? kh�a: 350$</span>
                                <button class="btn-unlock">M? kh�a</button>
                            </div>
                        </div>
                        
                        <div class="unlock-info-box">
                            <p class="info-title">Th�ng tin m? kh�a:</p>
                            <ul>
                                <li>M? kh�a v?t ph?m m?i ?? b�n trong c?a h�ng.</li>
                                <li>M?i v?t ph?m c� chi ph� m? kh�a kh�c nhau.</li>
                                <li>Sau khi m? kh�a, b?n c� th? nh?p h�ng ?? b�n.</li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div class="goods-footer">
                    <button id="btn-apply-goods" class="footer-action-button primary">�p d?ng thay ??i</button>
                    <button id="btn-cancel-goods" class="footer-action-button secondary">H?y</button>
                </div>
            </div>
        </div>


        <div id="customer-info-overlay" class="hidden">
            <div id="customer-info-panel">
                <div class="info-header">
                    <h2>TH�NG TIN KH�CH H�NG HI?N T?I</h2>
                    <button id="btn-close-customer-info" class="close-button">?</button>
                </div>
                
                <div id="customer-info-content">
                    <div id="customer-avatar-box">
                        </div>

                    <div id="customer-details">
                        <div class="info-row large">
                            <span class="info-label">T�n kh�ch h�ng:</span>
                            <span id="display-customer-name" class="info-value primary-value">Ch?a c� kh�ch</span>
                        </div>
                        
                        <div class="info-row">
                            <span class="info-label">M� kh�ch h�ng:</span>
                            <span id="display-customer-id" class="info-value">#N/A</span>
                        </div>
                        
                        <div class="info-row half-width">
                            <span class="info-label">Gi?i t�nh:</span>
                            <span id="display-customer-gender" class="info-value">?</span>
                        </div>

                        <div class="info-row half-width">
                            <span class="info-label">Tu?i:</span>
                            <span id="display-customer-age" class="info-value">?</span>
                        </div>
                    </div>
                </div>

                <div class="info-footer">
                    <p>Th�ng tin n�y gi�p b?n ??a ra quy?t ??nh B�N ho?c KH�NG.</p>
                </div>
            </div>
        </div>
        
        
        <div id="daily-customers-overlay" class="hidden">
            <div id="daily-customers-panel">
                <div class="daily-header">
                    <h2>H? S? KH�CH H�NG TRONG NG�Y</h2>
                    <button id="btn-close-daily-customers" class="close-button">?</button>
                </div>
                
                <div id="daily-customers-content">
                    <div id="daily-customer-list">
                        
                        <div class="daily-customer-row clickable-customer" data-customer-id="KH003" data-customer-name="Ti?u L?c" data-customer-gender="N?" data-customer-age="22">
                            <span class="customer-name-label">1. Ti?u L?c | M�: KH003</span>
                        </div>
                        
                        <div class="daily-customer-row clickable-customer" data-customer-id="KH004" data-customer-name="Shyn M?i M?" data-customer-gender="N?" data-customer-age="25">
                            <span class="customer-name-label">2. Shyn M?i M? | M�: KH004</span>
                        </div>
                        
                        <div class="daily-customer-row clickable-customer" data-customer-id="KH010" data-customer-name="Huy?n Thanh T? Uy�n" data-customer-gender="N?" data-customer-age="19">
                            <span class="customer-name-label">3. Huy?n Thanh T? Uy�n | M�: KH10</span>
                        </div>

                        <div class="daily-customer-row clickable-customer" data-customer-id="KH012" data-customer-name="L? Kh�ch Ph??ng B?c" data-customer-gender="Nam" data-customer-age="45">
                            <span class="customer-name-label">4. L? Kh�ch Ph??ng B?c | M�: KH012</span>
                        </div>
                        
                        <div class="daily-customer-row clickable-customer" data-customer-id="KH013" data-customer-name="Vua ?n ??n" data-customer-gender="Nam" data-customer-age="38">
                            <span class="customer-name-label">5. Vua ?n ??n | M�: KH013</span>
                        </div>

                        <div class="daily-customer-row clickable-customer" data-customer-id="KH014" data-customer-name="S? Gi? Khe N?t" data-customer-gender="N?" data-customer-age="30">
                            <span class="customer-name-label">6. S? Gi? Khe N?t | M�: KH014</span>
                        </div>
                        
                    </div>
                </div>

                <div class="daily-footer">
                    <p>Nh?p v�o t�n kh�ch h�ng ?? xem th�ng tin chi ti?t.</p>
                </div>
            </div>
        </div>


    </div>
    
    <script>
        // Khai b�o c�c bi?n DOM cho Panels
        const shopOverlay = document.getElementById('shop-overlay');
        const inventoryOverlay = document.getElementById('inventory-overlay');
        const haggleOverlay = document.getElementById('haggle-overlay');
        const goodsOverlay = document.getElementById('goods-overlay');
        const customerInfoOverlay = document.getElementById('customer-info-overlay');
        const dailyCustomersOverlay = document.getElementById('daily-customers-overlay');
        
        // Khai b�o c�c bi?n DOM cho Haggle (M?c c?)
        const haggleSelectionList = document.getElementById('haggle-selection-list');
        const haggleDetailPanel = document.getElementById('haggle-detail-panel');
        const btnBackHaggle = document.getElementById('btn-back-haggle');


        // ------------------ H�M H? TR? ?�NG OVERLAYS (Ch? ?? ?�ng c�c overlay ph?) ------------------

        function hideAllOverlays() {
            // H?u �ch khi chuy?n t? tr?ng th�i game ch�nh sang Shop
            shopOverlay.classList.add('hidden');
            inventoryOverlay.classList.add('hidden');
            haggleOverlay.classList.add('hidden');
            goodsOverlay.classList.add('hidden');
            customerInfoOverlay.classList.add('hidden');
            dailyCustomersOverlay.classList.add('hidden');
        }

        // ------------------ LOGIC M?/?�NG PANELS CHUNG ------------------

        // M? Shop (T? n�t ch�nh)
        document.getElementById('btn-shop').addEventListener('click', function() { 
            hideAllOverlays(); 
            shopOverlay.classList.remove('hidden'); 
        });
        // ?�ng Shop (V?n ?�ng ho�n to�n, v� n� l� Panel ch�nh)
        document.getElementById('btn-close-shop').addEventListener('click', function() { shopOverlay.classList.add('hidden'); });

        // --- LOGIC CHUY?N T? SHOP SANG PANELS PH? & QUAY L?I ---

        // M? Inventory (T? Shop)
        document.getElementById('btn-shop-tuido').addEventListener('click', function() {
            shopOverlay.classList.add('hidden'); 
            inventoryOverlay.classList.remove('hidden'); 
        });
        // ?�ng Inventory -> Quay l?i Shop
        document.getElementById('btn-close-inventory').addEventListener('click', function() { 
            inventoryOverlay.classList.add('hidden');
            shopOverlay.classList.remove('hidden'); 
        });
        
        // M? Haggle Panel (T? Shop)
        document.getElementById('btn-shop-macca').addEventListener('click', function() {
            shopOverlay.classList.add('hidden'); 
            haggleOverlay.classList.remove('hidden'); 
            // Reset tr?ng th�i M?c c? v? b??c ch?n v?t ph?m
            haggleDetailPanel.classList.add('hidden'); 
            haggleSelectionList.classList.remove('hidden');
            btnBackHaggle.classList.add('hidden');
        });
        // ?�ng Haggle Panel -> Quay l?i Shop
        document.getElementById('btn-close-haggle').addEventListener('click', function() { 
            haggleOverlay.classList.add('hidden');
            shopOverlay.classList.remove('hidden'); 
        });
        document.getElementById('btn-cancel-haggle').addEventListener('click', function() { 
            haggleOverlay.classList.add('hidden');
            shopOverlay.classList.remove('hidden'); 
        });

        // M? Goods Panel (H�NG H�A)
        document.getElementById('btn-shop-hanghoa').addEventListener('click', function() {
            shopOverlay.classList.add('hidden'); 
            goodsOverlay.classList.remove('hidden'); 
        });
        // ?�ng Goods Panel -> Quay l?i Shop
        document.getElementById('btn-close-goods').addEventListener('click', function() { 
            goodsOverlay.classList.add('hidden');
            shopOverlay.classList.remove('hidden'); 
        });
        document.getElementById('btn-cancel-goods').addEventListener('click', function() { 
            goodsOverlay.classList.add('hidden');
            shopOverlay.classList.remove('hidden'); 
        });

        // --- LOGIC KH�CH H�NG (Kh�ng li�n quan ??n Shop) ---
        
        // M? Customer Info Panel (TH�NG TIN KH�CH H�NG)
        document.getElementById('btn-ttkh').addEventListener('click', function() {
            customerInfoOverlay.classList.remove('hidden'); 
        });
        // ?�ng Customer Info Panel
        document.getElementById('btn-close-customer-info').addEventListener('click', function() { customerInfoOverlay.classList.add('hidden'); });

        // M? Daily Customers Panel (KH�CH H�NG TRONG NG�Y)
        document.getElementById('btn-khtn').addEventListener('click', function() {
            dailyCustomersOverlay.classList.remove('hidden'); 
        });
        // ?�ng Daily Customers Panel
        document.getElementById('btn-close-daily-customers').addEventListener('click', function() { dailyCustomersOverlay.classList.add('hidden'); });

        // ------------------ LOGIC M?C C? (HAGGLE) ?� S?A ------------------

        const haggleSelectItems = document.querySelectorAll('#haggle-item-selection-list .haggle-select-item');

        // 1. Logic khi click ch?n v?t ph?m ?? m?c c?
        haggleSelectItems.forEach(itemButton => {
            itemButton.addEventListener('click', function() {
                // ?n danh s�ch ch?n v� hi?n th? panel chi ti?t
                haggleSelectionList.classList.add('hidden');
                haggleDetailPanel.classList.remove('hidden');
                btnBackHaggle.classList.remove('hidden'); // Hi?n th? n�t Quay l?i

                // C?p nh?t th�ng tin chi ti?t (l?y t? data-attributes)
                const itemName = this.textContent.split(' - ')[0];
                const itemPrice = parseFloat(this.getAttribute('data-price'));
                const maxDiscountPercent = parseFloat(this.getAttribute('data-max-discount-percent'));
                const maxDiscountValue = Math.floor((itemPrice * maxDiscountPercent) / 100);

                document.getElementById('current-haggle-item-name').textContent = itemName;
                document.getElementById('original-price').textContent = `${itemPrice}$`;
                document.getElementById('max-discount-value').textContent = `${maxDiscountValue}$ (${maxDiscountPercent}%)`;

                // Reset c�c tr??ng nh?p li?u
                document.getElementById('input-discount-money').value = 0;
                document.getElementById('input-discount-percent-slider').value = 0;
                document.getElementById('display-discount-percent').textContent = '0%';
                document.getElementById('new-price').textContent = `${itemPrice}$`;
                document.getElementById('success-rate').textContent = '50%';
                
            });
        });

        // 2. Logic khi click n�t QUAY L?I trong panel m?c c?
        btnBackHaggle.addEventListener('click', function() {
            haggleDetailPanel.classList.add('hidden');
            haggleSelectionList.classList.remove('hidden');
            this.classList.add('hidden');
        });

        // ------------------ LOGIC CHUY?N TAB (H�NG H�A) ------------------
        const tabButtons = document.querySelectorAll('#goods-panel .tab-button');
        const goodsTabs = document.querySelectorAll('#goods-content .goods-tab');

        tabButtons.forEach(button => {
            button.addEventListener('click', function() {
                const targetTabId = this.getAttribute('data-tab');

                goodsTabs.forEach(tab => tab.classList.add('hidden'));
                tabButtons.forEach(btn => btn.classList.remove('active'));

                document.getElementById('tab-' + targetTabId).classList.remove('hidden');
                this.classList.add('active');
            });
        });

        // ------------------ LOGIC M? TTKH T? DANH S�CH KH TRONG NG�Y ------------------
        const customerRows = document.querySelectorAll('#daily-customer-list .clickable-customer');
        
        customerRows.forEach(row => {
            row.addEventListener('click', function() {
                const name = this.getAttribute('data-customer-name');
                const id = this.getAttribute('data-customer-id');
                const gender = this.getAttribute('data-customer-gender');
                const age = this.getAttribute('data-customer-age');

                // C?p nh?t d? li?u v�o Panel Th�ng tin KH
                document.getElementById('display-customer-name').textContent = name;
                document.getElementById('display-customer-id').textContent = '#' + id;
                document.getElementById('display-customer-gender').textContent = gender;
                document.getElementById('display-customer-age').textContent = age;

                // ?�ng Panel Kh�ch h�ng Trong Ng�y v� m? Panel Th�ng tin KH
                dailyCustomersOverlay.classList.add('hidden');
                customerInfoOverlay.classList.remove('hidden');
            });
        });

    </script>
</body>
</html>