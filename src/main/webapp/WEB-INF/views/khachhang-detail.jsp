<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi Tiết Khách Hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <style>
        .container {
            max-width: 600px;
            margin: auto;
        }
        .table th {
            background-color: #f8f9fa;
        }
        .btn-back {
            width: 100%;
        }
    </style>
</head>
<body>

<div class="container mt-5">
    <h2 class="text-center">Chi Tiết Khách Hàng</h2>
    <table class="table table-bordered mt-3">
        <tr>
            <th>ID</th>
            <td>${khachHang.idKhachHang}</td>
        </tr>
        <tr>
            <th>Mã Khách Hàng</th>
            <td>${khachHang.maKhachHang}</td>
        </tr>
        <tr>
            <th>Họ và Tên</th>
            <td>${khachHang.tenKhachHang}</td>
        </tr>
        <tr>
            <th>Giới Tính</th>
            <td>
                <c:choose>
                    <c:when test="${khachHang.gioiTinh}">Nam</c:when>
                    <c:otherwise>Nữ</c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th>Ngày sinh</th>
            <td><fmt:formatDate value="${khachHang.ngaySinh}" pattern="dd/MM/yyyy"/></td>
        </tr>
        <tr>
            <th>Số Điện Thoại</th>
            <td>${khachHang.soDienThoai}</td>
        </tr>
        <tr>
            <th>Email</th>
            <td>${khachHang.email}</td>
        </tr>
        <tr>
            <th>Trạng Thái</th>
            <td>
                <c:choose>
                    <c:when test="${khachHang.trangThai == 'Đang hoạt động'}">
                        <span class="text-success">Đang hoạt động</span>
                    </c:when>
                    <c:otherwise>
                        <span class="text-danger">Không hoạt động</span>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </table>
    <div class="text-center">
        <a href="/admin/khach-hang/view" class="btn btn-primary btn-back">Quay lại danh sách</a>
    </div>
</div>

</body>
</html>
