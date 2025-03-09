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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Roboto', sans-serif;
            margin: 0;
        }

        .bg-dark-custom {
            background-color: #343a40 !important;
        }

        .container-fluid {
            padding: 0;
        }

        .sidebar {
            width: 250px;
            height: 100vh;
            background-color: #343a40;
            color: white;
            padding: 20px;
            position: fixed;
            top: 0;
            left: 0;
        }

        .sidebar h4 {
            text-align: center;
            margin-bottom: 20px;
        }

        .sidebar .nav-link {
            color: white;
            padding: 10px;
            display: block;
        }

        .sidebar .nav-link:hover {
            background-color: #495057;
            text-decoration: none;
        }

        .main-content {
            margin-left: 250px;
            padding: 20px;
        }

        .order-header {
            background-color: #f8f9fa;
            padding: 10px;
            border-bottom: 1px solid #ddd;
            margin-bottom: 20px;
        }

        .order-header h2 {
            margin: 0 0 10px 0;
            font-weight: 600;
            color: #343a40;
        }

        .status-icons {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-bottom: 10px;
        }

        .status-icon {
            text-align: center;
            font-size: 14px;
        }

        .status-icon i {
            font-size: 24px;
            margin-bottom: 5px;
        }

        .status-icon small {
            display: block;
            font-size: 12px;
            color: #666;
        }

        .order-status {
            display: flex;
            gap: 10px;
            justify-content: flex-end;
            margin-bottom: 20px;
        }

        .order-status .btn-cancel:disabled {
            background-color: #dc3545;
            opacity: 0.65;
            cursor: not-allowed;
        }

        .info-box {
            background-color: #fff;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 15px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            position: relative;
        }

        .info-box .btn {
            position: absolute;
            top: 10px;
            right: 10px;
            padding: 5px 10px;
            font-size: 12px;
        }

        .info-box h5 {
            margin-top: 0;
            color: #343a40;
            font-weight: 500;
        }

        .info-box .row {
            display: flex;
            margin: 0 -15px;
        }

        .info-box .col {
            padding: 0 15px;
            flex: 1;
        }

        .table-custom {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .table-custom th, .table-custom td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
            vertical-align: middle;
        }

        .table-custom th {
            background-color: #f8f9fa;
            font-weight: 500;
        }

        .product-image {
            width: 50px;
            height: 50px;
            object-fit: cover;
        }

        .total-section {
            margin-top: 10px;
            text-align: right;
        }

        .notification {
            background-color: #ffebee;
            padding: 10px;
            border-radius: 5px;
            text-align: center;
            margin-top: 20px;
            color: #721c24;
            font-weight: 500;
        }

        textarea.form-control[readonly] {
            background-color: #fff;
            border: 1px solid #ddd;
            resize: none;
        }
    </style>
</head>
<body>
<div class="d-flex">
    <nav class="sidebar">
        <h4>G&B SPORTS</h4>
        <ul class="nav flex-column">
            <li class="nav-item"><a href="/hoa_don/danh_sach_hoa_don" class="nav-link">🏠 Trang chủ</a></li>
            <li class="nav-item"><a href="#" class="nav-link">📊 Thống kê</a></li>
            <li class="nav-item"><a href="#" class="nav-link">📦 Sản phẩm</a></li>
            <li class="nav-item"><a href="#" class="nav-link">🏷️ Danh mục</a></li>
            <li class="nav-item"><a href="/hoa_don/danh_sach_hoa_don" class="nav-link">📜 Quản lý hóa đơn</a></li>
            <li class="nav-item"><a href="#" class="nav-link">🛒 Nhà cung cấp</a></li>
            <li class="nav-item"><a href="#" class="nav-link">👤 Khách hàng</a></li>
            <li class="nav-item"><a href="#" class="nav-link">🔑 Đăng xuất</a></li>
        </ul>
    </nav>

    <div class="container-fluid main-content">
        <div class="order-header">
            <div class="row">
                <div class="col">
                    <h2>Thông tin chi tiết đơn hàng #${hoaDon.ma_hoa_don}</h2>
                </div>
                <div class="col">
                    <button class="btn btn-primary" onclick="window.location.href='/hoa_don/danh_sach_hoa_don'"
                            style="margin-left: 12cm">Quay lại
                    </button>
                </div>
            </div>
            <hr>
            <div class="status-icons">
                <div class="col">
                    <div class="status-icon ${hoaDon.trang_thai == 'Chờ xác nhận' || hoaDon.trang_thai == 'Đã xác nhận' || hoaDon.trang_thai == 'Đang giao' || hoaDon.trang_thai == 'Hoàn thành' ? 'text-success' : hoaDon.trang_thai == 'Đã hủy' ? '' : ''}">
                        <i class="fas fa-file-alt"></i>
                        <p>Đơn hàng đã đặt</p>
                        <c:forEach var="status" items="${trangThaiHistory}">
                            <c:if test="${status.trang_thai == 'Chờ xác nhận'}">
                                <c:if test="${not empty status.ngay_chuyen}">
                                    <small>${status.ngay_chuyen_formatted}</small>
                                </c:if>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
                <div class="col">
                    <div class="status-icon ${hoaDon.trang_thai == 'Đã xác nhận' || hoaDon.trang_thai == 'Đang giao' || hoaDon.trang_thai == 'Hoàn thành' ? 'text-success' : hoaDon.trang_thai == 'Đã hủy' ? '' : ''}">
                        <i class="fas fa-check"></i>
                        <p>Đã xác nhận</p>
                        <c:forEach var="status" items="${trangThaiHistory}">
                            <c:if test="${status.trang_thai == 'Đã xác nhận'}">
                                <c:if test="${not empty status.ngay_chuyen}">
                                    <small>${status.ngay_chuyen_formatted}</small>
                                </c:if>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
                <div class="col">
                    <div class="status-icon ${hoaDon.trang_thai == 'Đang giao' || hoaDon.trang_thai == 'Hoàn thành' ? 'text-success' : hoaDon.trang_thai == 'Đã hủy' ? '' : ''}">
                        <i class="fas fa-truck"></i>
                        <p>Đã giao cho ĐVVC</p>
                        <c:forEach var="status" items="${trangThaiHistory}">
                            <c:if test="${status.trang_thai == 'Đang giao'}">
                                <c:if test="${not empty status.ngay_chuyen}">
                                    <small>${status.ngay_chuyen_formatted}</small>
                                </c:if>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
                <div class="col">
                    <div class="status-icon ${hoaDon.trang_thai == 'Hoàn thành' ? 'text-success' : hoaDon.trang_thai == 'Đã hủy' ? '' : ''}">
                        <i class="fas fa-box"></i>
                        <p>Đã nhận hàng</p>
                        <c:forEach var="status" items="${trangThaiHistory}">
                            <c:if test="${status.trang_thai == 'Hoàn thành'}">
                                <c:if test="${not empty status.ngay_chuyen}">
                                    <small>${status.ngay_chuyen_formatted}</small>
                                </c:if>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
                <div class="col">
                    <div class="status-icon ${hoaDon.trang_thai == 'Hoàn thành' ? 'text-success' : hoaDon.trang_thai == 'Đã hủy' ? '' : ''}">
                        <i class="fas fa-check-circle"></i>
                        <p>Hoàn thành</p>
                        <c:forEach var="status" items="${trangThaiHistory}">
                            <c:if test="${status.trang_thai == 'Hoàn thành'}">
                                <c:if test="${not empty status.ngay_chuyen}">
                                    <small>${status.ngay_chuyen_formatted}</small>
                                </c:if>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
            <hr>
            <div class="order-status">
                <%--                <p>Trạng thái: ${hoaDon.trang_thai != null ? hoaDon.trang_thai : 'Chưa xử lý'}</p>--%>
                <form action="/hoa_don/chuyen-trang-thai" method="post">
                    <input type="hidden" name="maHoaDon"
                           value="${not empty hoaDon.ma_hoa_don ? hoaDon.ma_hoa_don : ''}"/>
                    <input type="hidden" name="newTrangThai"
                           value="${hoaDon.trang_thai == 'Chờ xác nhận' ? 'Đã xác nhận' :
                       hoaDon.trang_thai == 'Đã xác nhận' ? 'Đang giao' :
                       hoaDon.trang_thai == 'Đang giao' ? 'Hoàn thành' : ''}"/>
                    <button type="submit"
                            class="btn btn-success ${hoaDon.trang_thai == 'Hoàn thành' || hoaDon.trang_thai == 'Đã hủy' ? 'disabled' : ''}"
                    ${hoaDon.trang_thai == 'Hoàn thành' || hoaDon.trang_thai == 'Đã hủy' ? 'disabled' : ''}>
                        ${hoaDon.trang_thai == 'Chờ xác nhận' ? 'Xác nhận đơn hàng' :
                                hoaDon.trang_thai == 'Đã xác nhận' ? 'Giao cho ĐVVC' :
                                        hoaDon.trang_thai == 'Đang giao' ? 'Hoàn thành' : 'Hoàn thành'}
                    </button>
                </form>
                <form action="/hoa_don/cancel-order" method="post">
                    <input type="hidden" name="maHoaDon"
                           value="${not empty hoaDon.ma_hoa_don ? hoaDon.ma_hoa_don : ''}"/>
                    <button type="submit"
                            class="btn btn-danger ${hoaDon.trang_thai == 'Đang giao' || hoaDon.trang_thai == 'Hoàn thành' || hoaDon.trang_thai == 'Đã hủy' ? 'disabled' : ''}"
                    ${hoaDon.trang_thai == 'Đang giao' || hoaDon.trang_thai == 'Hoàn thành' || hoaDon.trang_thai == 'Đã hủy' ? 'disabled' : ''}>
                        Hủy đơn
                    </button>
                </form>
            </div>
            <c:if test="${not empty message}">
                <div class="alert alert-info">
                        ${message}
                </div>
            </c:if>
        </div>

        <div class="row">
            <div class="col-md-12">
                <div class="info-box">
                    <h5>Thông tin đơn hàng</h5>
                    <div class="row">
                        <div class="col">
                            <p>Mã hóa đơn: ${hoaDon.ma_hoa_don}</p>
                            <p>Trạng thái: ${hoaDon.trang_thai}</p>
                            <p>Phương thức thanh toán: ${hoaDon.hinh_thuc_thanh_toan}</p>
                        </div>
                        <div class="col">
                            <p>Ngày tạo: ${hoaDon.ngay_tao}</p>
                            <p>Nhân viên tiếp nhận: AAAAAAAAAAAAA</p>
                            <p>Hình thức nhận hàng: ${hoaDon.phuong_thuc_nhan_hang}</p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-8">
                    <div class="info-box">
                        <h5>Thông tin sản phẩm</h5>
                        <button class="btn btn-primary ${hoaDon.trang_thai == 'Đang giao' || hoaDon.trang_thai == 'Hoàn thành' || hoaDon.trang_thai == 'Đã hủy' ? 'disabled' : ''}"
                        ${hoaDon.trang_thai == 'Đang giao' || hoaDon.trang_thai == 'Hoàn thành' || hoaDon.trang_thai == 'Đã hủy' ? 'disabled' : ''}>
                            Thêm sản phẩm
                        </button>
                        <table class="table-custom">
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
                                             alt="Hình sản phẩm" class="product-image">
                                            ${chiTiet.ten_san_pham} <br>
                                        Mã: ${chiTiet.ma_san_pham}, Kích thước: ${chiTiet.kich_thuoc},
                                        Màu: ${chiTiet.ten_mau_sac}
                                    </td>
                                    <td><fmt:formatNumber value="${chiTiet.don_gia}" pattern="#,###"/> VNĐ</td>
                                    <td>${chiTiet.so_luong}</td>
                                    <td><fmt:formatNumber value="${chiTiet.don_gia * chiTiet.so_luong}"
                                                          pattern="#,###"/> VNĐ
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        <div class="total-section">
                            <p>Tổng tạm: <fmt:formatNumber value="${hoaDon.tong_tien_truoc_giam}" pattern="#,###"/>
                                VNĐ</p>
                            <p>Giảm giá: <fmt:formatNumber
                                    value="${hoaDon.tong_tien_truoc_giam - hoaDon.tong_tien_sau_giam}" pattern="#,###"/>
                                VNĐ</p>
                            <p>Phí vận chuyển: <fmt:formatNumber value="${hoaDon.phi_van_chuyen}" pattern="#,###"/>
                                VNĐ</p>
                            <p>Tổng cuối: <fmt:formatNumber value="${hoaDon.tong_tien_sau_giam}" pattern="#,###"/>
                                VNĐ</p>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="info-box">
                        <h5>Thông tin khách hàng</h5>
                        <button class="btn btn-primary ${hoaDon.trang_thai == 'Đang giao' || hoaDon.trang_thai == 'Hoàn thành' || hoaDon.trang_thai == 'Đã hủy' ? 'disabled' : ''}"
                        ${hoaDon.trang_thai == 'Đang giao' || hoaDon.trang_thai == 'Hoàn thành' || hoaDon.trang_thai == 'Đã hủy' ? 'disabled' : ''}>
                            Sửa
                        </button>
                        <p>Tên: ${hoaDon.ho_ten}</p>
                        <p>Email: ${hoaDon.email}</p>
                        <p>Phone: ${hoaDon.sdt_nguoi_nhan}</p>
                        <p>Địa chỉ: ${hoaDon.dia_chi}</p>
                    </div>
                    <div class="info-box">
                        <h5>Ghi chú</h5>
                        <button class="btn btn-primary ${hoaDon.trang_thai == 'Đang giao' || hoaDon.trang_thai == 'Hoàn thành' || hoaDon.trang_thai == 'Đã hủy' ? 'disabled' : ''}"
                        ${hoaDon.trang_thai == 'Đang giao' || hoaDon.trang_thai == 'Hoàn thành' || hoaDon.trang_thai == 'Đã hủy' ? 'disabled' : ''}>
                            Sửa
                        </button>
                        <textarea class="form-control" rows="2" readonly>Giao hàng trực tiếp tại nhà nhé!!</textarea>
                    </div>
                </div>
            </div>
        </div>
        <div class="notification">
            ĐANG CHỜ XÁC NHẬN ĐƠN HÀNG <fmt:formatNumber value="${hoaDon.tong_tien_sau_giam}" pattern="#,###"/> VNĐ
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function cancelOrder() {
        if (confirm("Bạn có chắc muốn hủy đơn hàng này?")) {
            // Gọi API hoặc servlet để hủy đơn hàng
            alert("Đơn hàng đã được hủy!");
            // Có thể thêm logic redirect hoặc AJAX call tại đây
        }
    }
</script>
</body>
</html>