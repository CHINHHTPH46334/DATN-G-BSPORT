<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý hóa đơn</title>
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

        .table-custom th {
            background-color: #007bff;
            color: white;
        }

        .table-custom td {
            vertical-align: middle;
        }

        .table-custom tr:hover {
            background-color: #f1f1f1;
            transition: background-color 0.3s ease;
        }

        .filter-section {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
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

        .pagination-custom .page-link {
            color: #007bff;
            border: 1px solid #007bff;
        }

        .pagination-custom .page-link:hover {
            background-color: #007bff;
            color: white;
        }

        h2 {
            color: #343a40;
            font-weight: 600;
            margin-bottom: 20px;
        }

        .filter-form {
            padding: 10px;
            background: #f8f9fa;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .input-group {
            margin-bottom: 5px;
        }

        .form-control-sm {
            height: 30px;
            font-size: 14px;
            padding: 5px;
        }

        .btn-sm {
            padding: 5px 10px;
            font-size: 14px;
        }

        .form-label {
            font-size: 14px;
            margin-right: 10px;
            white-space: nowrap;
        }
    </style>
</head>
<body>
<div class="d-flex">
    <!-- Menu bên trái (giữ nguyên) -->
    <nav class="bg-dark-custom text-white p-3" style="width: 250px; height: 100vh;">
        <h4 class="text-center">G&B SPORTS</h4>
        <ul class="nav flex-column">
            <li class="nav-item"><a href="/hoa_don/danh_sach_hoa_don" class="nav-link text-white">🏠 Trang chủ</a></li>
            <li class="nav-item"><a href="#" class="nav-link text-white">📊 Thống kê</a></li>
            <li class="nav-item"><a href="#" class="nav-link text-white">📦 Sản phẩm</a></li>
            <li class="nav-item"><a href="#" class="nav-link text-white">🏷️ Danh mục</a></li>
            <li class="nav-item"><a href="/hoa_don/danh_sach_hoa_don" class="nav-link text-white">📜 Quản lý hóa đơn</a>
            </li>
            <li class="nav-item"><a href="#" class="nav-link text-white">🛒 Nhà cung cấp</a></li>
            <li class="nav-item"><a href="#" class="nav-link text-white">👤 Khách hàng</a></li>
            <li class="nav-item"><a href="#" class="nav-link text-white">🔑 Đăng xuất</a></li>
        </ul>
    </nav>

    <!-- Nội dung chính -->
    <div class="container mt-4 flex-grow-1">
        <h2 class="text-center">DANH SÁCH ĐƠN HÀNG</h2>

        <!-- Bộ lọc với thiết kế đẹp hơn -->
        <div class="filter-section">
            <div class="row g-3">
                <div class="col-md-3">
                    <form action="/hoa_don/loc_trang_thai_don_hang" method="get">
                        <select name="trangThai" class="form-select" onchange="this.form.submit()">
                            <option value="">Tất cả</option>
                            <option value="Đang xử lý" <c:if test="${trangThai == 'Đang xử lý'}">selected</c:if>>
                                Đang xử lý
                            </option>
                            <option value="Đang giao" <c:if test="${trangThai == 'Đang giao'}">selected</c:if>>
                                Đang giao
                            </option>
                            <option value="Đã giao" <c:if test="${trangThai == 'Đã giao'}">selected</c:if>>
                                Đã giao
                            </option>
                        </select>
                    </form>
                </div>
                <div class="col-md-4">
                    <form action="/hoa_don/loc_ngay" method="get" class="d-flex flex-column gap-1 filter-form">
                        <div class="input-group">
                            <label for="tuNgay" class="form-label me-2">Từ ngày:</label>
                            <input type="datetime-local" id="tuNgay" name="tuNgay"
                                   value="${not empty tuNgay ? tuNgay : ''}"
                                   required class="form-control form-control-sm">
                        </div>
                        <div class="input-group">
                            <label for="denNgay" class="form-label me-2">Đến ngày:</label>
                            <input type="datetime-local" id="denNgay" name="denNgay"
                                   value="${not empty denNgay ? denNgay : ''}"
                                   required class="form-control form-control-sm">
                        </div>
                        <button type="submit" class="btn btn-custom btn-sm mt-2">Lọc</button>
                    </form>
                </div>
                <div class="col-md-5">
                    <form action="/hoa_don/tim_kiem" method="GET" class="d-flex align-items-center">
                        <input type="text" class="form-control me-2" name="keyword"
                               value="${empty param.keyword ? '' : param.keyword}"
                               placeholder="Nhập mã HD, mã NV hoặc số ĐT người nhận">
                        <button type="submit" class="btn btn-custom">Tìm kiếm</button>
                    </form>
                </div>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger text-center">${error}</div>
        </c:if>

        <!-- Bảng danh sách đơn hàng -->
        <table class="table table-custom text-center">
            <thead>
            <tr>
                <th>STT</th>
                <th>Mã HD</th>
                <th>Ngày tạo</th>
                <th>Khách hàng</th>
                <th>Giảm giá (VNĐ)</th>
                <th>Tổng cuối (VNĐ)</th>
                <th>Trạng thái</th>
                <th>Thanh toán</th>
                <th>Hình thức</th>
                <th>Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="hoaDon" items="${hoaDons}" varStatus="loop">
                <tr>
                    <td>${loop.index + 1 + (currentPage * size)}</td>
                    <td>${hoaDon.ma_hoa_don}</td>
                        <%--                    <td><fmt:formatDate value="${hoaDon.ngay_tao}" pattern="yyyy-MM-dd HH:mm:ss"/></td>--%>
                    <td>${hoaDon.ngay_tao}</td>
                    <td>
                            ${hoaDon.ho_ten} <br>
                        ĐT: ${hoaDon.sdt_nguoi_nhan} <br>
                        ĐC: ${hoaDon.dia_chi}
                    </td>
                    <td>${not empty hoaDon.ma_voucher ? hoaDon.ma_voucher : 'Không có'}</td>
                        <%--                    <td><fmt:formatNumber value="${hoaDon.tong_tien_sau_giam}" type="currency" currencySymbol=" VNĐ" groupingUsed="true"/></td>--%>
                    <td>${hoaDon.tong_tien_sau_giam}</td>
                    <td>${hoaDon.trang_thai}</td>
                    <td>${hoaDon.hinh_thuc_thanh_toan}</td>
                    <td>${hoaDon.phuong_thuc_nhan_hang}</td>
                    <td>
                        <a href="/hoa_don/hdct?id=${hoaDon.ma_hoa_don}" class="btn btn-sm btn-custom">Xem</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <!-- Phân trang với thiết kế đẹp hơn -->
        <nav>
            <ul class="pagination pagination-custom justify-content-center">
                <li class="page-item <c:if test='${!hasPrevious}'>disabled</c:if>">
                    <a class="page-link" href="?page=${currentPage - 1}&size=${size}">Trước</a>
                </li>
                <li class="page-item">
                    <span class="page-link">Trang ${currentPage + 1} / ${totalPages + 1}</span>
                </li>
                <li class="page-item <c:if test='${!hasNext}'>disabled</c:if>">
                    <a class="page-link" href="?page=${currentPage + 1}&size=${size}">Sau</a>
                </li>
            </ul>
        </nav>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>