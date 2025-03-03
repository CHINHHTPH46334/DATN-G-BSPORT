<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý khách hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
<div class="container mt-4">
    <h2 class="text-center">Quản lý khách hàng</h2>

    <!-- Form tìm kiếm -->
    <form action="/admin/khach-hang/view" method="get" class="mb-3">
        <input type="text" name="keyword" class="form-control" placeholder="Nhập từ khóa tìm kiếm...">
        <button type="submit" class="btn btn-primary mt-2">Tìm kiếm</button>
    </form>

    <!-- Form lọc trạng thái -->
    <form action="/admin/khach-hang/view" method="get" class="mb-3">
        <select name="trangThai" class="form-control">
            <option value="">-- Chọn trạng thái --</option>
            <option value="Đang hoạt động" ${trangThai == 'Đang hoạt động' ? 'selected' : ''}>Đang hoạt động</option>
            <option value="Không hoạt động" ${trangThai == 'Không hoạt động' ? 'selected' : ''}>Không hoạt động</option>
        </select>
        <button type="submit" class="btn btn-success mt-2">Lọc</button>
    </form>

    <table class="table table-bordered">
        <thead>
        <tr>
            <th>ID</th>
            <th>Mã KH</th>
            <th>Tên KH</th>
            <th>Email</th>
            <th>Giới tính</th>
            <th>SĐT</th>
            <th>Ngày sinh</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${danhSachKhachHang}" var="kh">
            <tr>
                <td>${kh.idKhachHang}</td>
                <td>${kh.maKhachHang}</td>
                <td>${kh.tenKhachHang}</td>
                <td>${kh.email}</td>
                <td>${kh.gioiTinh ? "Nam" : "Nữ"}</td>
                <td>${kh.soDienThoai}</td>
                <td><fmt:formatDate value="${kh.ngaySinh}" pattern="dd/MM/yyyy"/></td>

                <td>
                    <form action="/admin/khach-hang/chuyen-trang-thai" method="post">
                        <input type="hidden" name="idKhachHang" value="${kh.idKhachHang}">
                        <button type="submit" class="btn ${kh.trangThai == 'Đang hoạt động' ? 'btn-success' : 'btn-secondary'}">
                                ${kh.trangThai}
                        </button>
                    </form>
                </td>
                <td>
                    <a href="/admin/khach-hang/detail/${kh.idKhachHang}" class="btn btn-info">Chi tiết</a>
                    <a href="/admin/khach-hang/edit/${kh.idKhachHang}" class="btn btn-warning">Sửa</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <!-- Phân trang -->
    <nav>
        <ul class="pagination">
            <li class="page-item"><a class="page-link" href="?page=0">Trang đầu</a></li>
            <li class="page-item"><a class="page-link" href="?page=${currentPage - 1}">Trước</a></li>
            <li class="page-item"><a class="page-link" href="?page=${currentPage + 1}">Sau</a></li>
            <li class="page-item"><a class="page-link" href="?page=${totalPages - 1}">Trang cuối</a></li>
        </ul>
    </nav>

    <a href="/admin/khach-hang/add" class="btn btn-success">Thêm khách hàng</a>
</div>
</body>
</html>
