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
    <style>
        /* CSS tùy chỉnh cho nút gạt */
        .switch {
            position: relative;
            display: inline-block;
            width: 60px;
            height: 34px;
        }
        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }
        .slider {
            position: absolute;
            cursor: pointer;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: #ccc;
            transition: .4s;
            border-radius: 34px;
        }
        .slider:before {
            position: absolute;
            content: "";
            height: 26px;
            width: 26px;
            left: 4px;
            bottom: 4px;
            background-color: white;
            transition: .4s;
            border-radius: 50%;
        }
        input:checked + .slider {
            background-color: #28a745;
        }
        input:checked + .slider:before {
            transform: translateX(26px);
        }
    </style>
</head>
<body>
<div class="container mt-4">
    <h2 class="text-center mb-5">Quản lý khách hàng</h2>

    <c:if test="${not empty message}">
        <div class="alert alert-success">${message}</div>
    </c:if>

    <div class="row mb-3 align-items-center">
        <div class="col-auto">
            <a href="/admin/khach-hang/add" class="btn btn-success">Thêm khách hàng</a>
        </div>
        <div class="col-auto ms-3">
            <form action="/admin/khach-hang/view" method="get" class="d-flex align-items-center">
                <input type="text" name="keyword" class="form-control me-2" placeholder="Nhập từ khóa tìm kiếm..." value="${keyword}" style="width: 200px;">
                <button type="submit" class="btn btn-primary">Tìm kiếm</button>
            </form>
        </div>
        <div class="col-auto ms-3">
            <form action="/admin/khach-hang/view" method="get" class="d-flex align-items-center">
                <select name="trangThai" class="form-control me-2" style="width: 200px;">
                    <option value="">-- Chọn trạng thái --</option>
                    <option value="Đang hoạt động" ${trangThai == 'Đang hoạt động' ? 'selected' : ''}>Đang hoạt động</option>
                    <option value="Không hoạt động" ${trangThai == 'Không hoạt động' ? 'selected' : ''}>Không hoạt động</option>
                </select>
                <button type="submit" class="btn btn-success">Lọc</button>
            </form>
        </div>
    </div>

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
            <th>Địa chỉ</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${danhSachKhachHang}" var="kh" varStatus="s">
            <tr> <!-- Bỏ class làm nổi bật -->
                <td>${s.count}</td>
                <td>${kh.maKhachHang}</td>
                <td>${kh.tenKhachHang}</td>
                <td>${kh.email}</td>
                <td>${kh.gioiTinh ? "Nam" : "Nữ"}</td>
                <td>${kh.soDienThoai}</td>
                <td><fmt:formatDate value="${kh.ngaySinh}" pattern="dd/MM/yyyy"/></td>
                <td>${diaChiMap[kh.idKhachHang]}</td>
                <td>
                    <label class="switch">
                        <input type="checkbox" ${kh.trangThai == 'Đang hoạt động' ? 'checked' : ''} onchange="toggleStatus(${kh.idKhachHang})">
                        <span class="slider"></span>
                    </label>
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
            <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                <a class="page-link" href="?page=0&keyword=${keyword}&trangThai=${trangThai}">Trang đầu</a>
            </li>
            <li class="page-item ${currentPage == 0 ? 'disabled' : ''}">
                <a class="page-link" href="?page=${currentPage - 1}&keyword=${keyword}&trangThai=${trangThai}">Trước</a>
            </li>
            <li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="?page=${currentPage + 1}&keyword=${keyword}&trangThai=${trangThai}">Sau</a>
            </li>
            <li class="page-item ${currentPage == totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="?page=${totalPages - 1}&keyword=${keyword}&trangThai=${trangThai}">Trang cuối</a>
            </li>
        </ul>
    </nav>
</div>

<script>
    function toggleStatus(idKhachHang) {
        fetch('/admin/khach-hang/chuyen-trang-thai', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'idKhachHang=' + idKhachHang
        }).then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert('Có lỗi xảy ra khi thay đổi trạng thái!');
            }
        }).catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra khi gọi API!');
        });
    }
</script>

</body>
</html>