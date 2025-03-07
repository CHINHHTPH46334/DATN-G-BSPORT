<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết đơn hàng</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Roboto', sans-serif;
        }

        .bg-dark-custom {
            background-color: #343a40 !important;
        }

        .table-custom {
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .table-custom th, .table-custom td {
            vertical-align: middle;
        }

        .table-custom tr:hover {
            background-color: #f1f1f1;
            transition: background-color 0.3s ease;
        }

        .info-box {
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            padding: 15px;
            margin-bottom: 20px;
        }

        .btn-custom {
            background-color: #007bff;
            border: none;
            border-radius: 5px;
            padding: 8px 16px;
            transition: background-color 0.3s ease;
        }

        .btn-custom:hover {
            background-color: #0056b3;
        }

        h2 {
            color: #343a40;
            font-weight: 600;
            margin-bottom: 20px;
        }

        .alert-danger-custom {
            background-color: #f8d7da;
            color: #721c24;
            border-radius: 5px;
            padding: 10px;
            margin-bottom: 15px;
        }

        .invoice-total {
            font-weight: bold;
            color: #dc3545;
            margin-top: 10px;
        }
    </style>
</head>
<body>
<div class="d-flex">
    <!-- Menu bên trái (giữ nguyên từ hoa_don.jsp) -->
    <nav class="bg-dark-custom text-white p-3" style="width: 250px; height: 100vh;">
        <h4 class="text-center">G&B SPORTS</h4>
        <ul class="nav flex-column">
            <li class="nav-item"><a href="/hoa_don/danh_sach_hoa_don" class="nav-link text-white">🏠 Trang chủ</a></li>
            <li class="nav-item"><a href="#" class="nav-link text-white">📊 Thống kê</a></li>
            <li class="nav-item"><a href="#" class="nav-link text-white">📦 Sản phẩm</a></li>
            <li class="nav-item"><a href="#" class="nav-link text-white">🏷️ Danh mục</a></li>
            <li class="nav-item"><a href="/hoa_don/danh_sach_hoa_don" class="nav-link text-white">📜 Quản lý hóa đơn</a></li>
            <li class="nav-item"><a href="#" class="nav-link text-white">🛒 Nhà cung cấp</a></li>
            <li class="nav-item"><a href="#" class="nav-link text-white">👤 Khách hàng</a></li>
            <li class="nav-item"><a href="#" class="nav-link text-white">🔑 Đăng xuất</a></li>
        </ul>
    </nav>

    <!-- Nội dung chính (chi tiết hóa đơn) -->
    <div class="container mt-4 flex-grow-1">
        <h2 class="text-center">Quản lý thông tin chi tiết đơn hàng</h2>

        <!-- Hiển thị thông báo lỗi nếu có -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger-custom text-center">${error}</div>
        </c:if>

        <div class="row">
            <!-- Chi tiết đơn hàng -->
            <div class="col-md-8">
                <div class="info-box">
                    <h5>Chi tiết đơn hàng #${hoaDon.ma_hoa_don}</h5>
                    <table class="table table-custom">
                        <thead>
                        <tr>
                            <th>Sản phẩm</th>
                            <th>Đơn giá</th>
                            <th>Số lượng</th>
                            <th>Thành tiền</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="chiTiet" items="${chiTietHoaDons}" varStatus="loop">
                            <tr>
                                <td>
                                    <img src="${chiTiet.hinh_anh != null ? chiTiet.hinh_anh : '/images/default.jpg'}"
                                         alt="Hình sản phẩm" style="width: 50px; height: 50px; object-fit: cover;">
                                        ${chiTiet.ten_san_pham} <br>
                                    Mã: ${chiTiet.ma_san_pham}, Kích thước: ${chiTiet.kich_thuoc},
                                    Màu: ${chiTiet.ten_mau_sac}
                                </td>
                                <td><fmt:formatNumber value="${chiTiet.don_gia}" pattern="#,###"/> VNĐ</td>
                                <td>${chiTiet.so_luong}</td>
                                <td><fmt:formatNumber value="${chiTiet.don_gia * chiTiet.so_luong}" pattern="#,###"/> VNĐ</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>

                    <!-- Tổng tiền, giảm giá, phí vận chuyển, tổng cuối -->
                    <div class="text-end">
                        <p>Tổng tạm: <fmt:formatNumber value="${hoaDon.tong_tien_truoc_giam}" pattern="#,###"/> VNĐ</p>
                        <p>Giảm giá: <fmt:formatNumber value="${hoaDon.tong_tien_truoc_giam - hoaDon.tong_tien_sau_giam}" pattern="#,###"/> VNĐ</p>
                        <p>Phí vận chuyển: <fmt:formatNumber value="${hoaDon.phi_van_chuyen}" pattern="#,###"/> VNĐ</p>
                        <p class="invoice-total">Tổng cuối: <fmt:formatNumber value="${hoaDon.tong_tien_sau_giam}" pattern="#,###"/> VNĐ</p>
                    </div>
                </div>
            </div>

            <!-- Thông tin khách hàng và ghi chú -->
            <div class="col-md-4">
                <div class="info-box">
                    <h5>Thông tin khách hàng</h5>
                    <p>N: ${hoaDon.ho_ten}</p>
                    <p>E: ${hoaDon.email}</p>
                    <p>P: ${hoaDon.sdt_nguoi_nhan}</p>
                    <p>A: ${hoaDon.dia_chi}</p>
                </div>

                <div class="info-box mt-3">
                    <h5>Ghi chú</h5>
                    <textarea class="form-control" rows="3" readonly>Giao hàng trực tiếp tại nhà nhé!!</textarea>
                </div>
            </div>
        </div>

        <!-- Nút hành động -->
        <div class="text-center mt-3">
            <button class="btn btn-custom" onclick="window.location.href='/hoa_don/danh_sach_hoa_don'">Quay lại danh sách</button>
            <button class="btn btn-custom">Xác nhận</button>
        </div>

        <!-- Hiển thị thông báo tổng tiền nếu có -->
        <div class="text-center mt-2 alert-danger-custom">
            <c:if test="${not empty hoaDon.tong_tien_sau_giam}">
                ĐANG CHỜ XÁC NHẬN ĐƠN HÀNG <fmt:formatNumber value="${hoaDon.tong_tien_sau_giam}" pattern="#,###" type="currency" currencySymbol=" VNĐ" groupingUsed="true"/>
            </c:if>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>