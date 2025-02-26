<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý hóa đơn</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
<div class="d-flex">
    <!-- Menu bên trái -->
    <nav class="bg-dark text-white p-3" style="width: 250px; height: 100vh;">
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
    <div class="container mt-4">
        <h2 class="text-center">DANH SÁCH ĐƠN HÀNG</h2>

        <!-- Bộ lọc -->
        <div class="row mb-3">
            <div class="col-md-1"></div>
            <div class="col-md-3">
                <form action="/hoa_don/loc_trang_thai_don_hang" method="get">
                    <select name="trangThai" class="form-control" onchange="this.form.submit()">
                        <option value="">Chọn trạng thái giao hàng</option>
                        <option value="Đang xử lý" <%= request.getParameter("trangThai") != null && request.getParameter("trangThai").equals("Đang xử lý") ? "selected" : "" %>>
                            Đang xử lý
                        </option>
                        <option value="Đang giao" <%= request.getParameter("trangThai") != null && request.getParameter("trangThai").equals("Đang giao") ? "selected" : "" %>>
                            Đang giao
                        </option>
                        <option value="Đã giao" <%= request.getParameter("trangThai") != null && request.getParameter("trangThai").equals("Đã giao") ? "selected" : "" %>>
                            Đã giao
                        </option>
                    </select>
                </form>
            </div>
            <div class="col-md-3">
                <form action="/hoa_don/loc_ngay" method="get">
                    <label for="tuNgay">Từ ngày:</label>
                    <input type="date" id="tuNgay" name="tuNgay"
                           value="<%= request.getParameter("tuNgay") != null ? request.getParameter("tuNgay") : "" %>"
                           required><br>

                    <label for="denNgay">Đến ngày:</label>
                    <input type="date" id="denNgay" name="denNgay"
                           value="<%= request.getParameter("denNgay") != null ? request.getParameter("denNgay") : "" %>"
                           required>

                    <button type="submit">Lọc</button>
                </form>
            </div>
            <div class="col-md-5">
                <form action="/hoa_don/tim_kiem" method="GET" class="d-flex">
                    <input type="text" class="form-control me-2" name="keyword"
                           placeholder="Nhập maHD, maNV hoặc sdtNguoiNhan muốn tìm">
                    <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                </form>
            </div>
        </div>

        <!-- Bảng danh sách đơn hàng -->
        <table class="table table-bordered text-center">
            <thead class="table-dark">
            <tr>
                <th>STT</th>
                <th>Mã HD</th>
                <th>Ngày tạo</th>
                <th>Khách hàng</th>
                <th>Giảm giá (VNĐ)</th>
                <th>Tổng cuối (VNĐ)</th>
                <th>Trạng thái</th>
                <th>Thanh toán</th>
                <%--            <th>Giao hàng</th>--%>
                <th>Hình thức</th>
                <th>Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="hoaDon" items="${hd}" varStatus="loop">
                <tr>
                    <td>${loop.index + 1}</td>
                    <td>${hoaDon.ma_hoa_don}</td>
                    <td>${hoaDon.ngay_tao}</td>
                    <td>${hoaDon.ho_ten} <br> ĐT: ${hoaDon.sdt_nguoi_nhan} <br> ĐC: ${hoaDon.dia_chi}</td>
                    <td>ahihi</td>
                    <td>${hoaDon.tong_tien_sau_giam}</td>
                    <td>${hoaDon.trang_thai}</td>
                    <td>${hoaDon.hinh_thuc_thanh_toan}</td>
                        <%--                <td>${hoaDon.giaoHang}</td>--%>
                    <td>${hoaDon.phuong_thuc_nhan_hang}</td>
                    <td>
                            <%--                    <a href="hoa-don-chi-tiet?id=${hoaDon.maHD}" class="btn btn-primary btn-sm">Xem</a>--%>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <!-- Phân trang -->
        <nav>
            <ul class="pagination justify-content-center">
                <li class="page-item"><a class="page-link" href="/hoa_don/phan_trang?page=${pagetrai}&size=3">«</a></li>
                <li class="page-item"><a class="page-link" href="/hoa_don/phan_trang?page=0&size=3">1</a></li>
                <li class="page-item"><a class="page-link" href="/hoa_don/phan_trang?page=1&size=3">2</a></li>
                <li class="page-item"><a class="page-link" href="/hoa_don/phan_trang?page=2&size=3">3</a></li>
                <li class="page-item"><a class="page-link" href="/hoa_don/phan_trang?page=${pagephai}&size=3">»</a></li>
            </ul>
        </nav>
    </div>
</div>
</body>
</html>
