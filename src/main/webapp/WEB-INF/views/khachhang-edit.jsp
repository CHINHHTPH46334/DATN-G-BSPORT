<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Cập nhật khách hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<div class="container mt-4">
    <h2 class="mb-3">Cập nhật khách hàng</h2>
    <form action="/admin/khach-hang/update" method="post">
        <input type="hidden" name="idKhachHang" value="${khachHang.idKhachHang}">
        <div class="mb-3"><label>Mã KH:</label><input type="text" name="maKhachHang" class="form-control" value="${khachHang.maKhachHang}"></div>
        <div class="mb-3"><label>Tên KH:</label><input type="text" name="tenKhachHang" class="form-control" value="${khachHang.tenKhachHang}"></div>
        <div class="mb-3"><label>Email:</label><input type="email" name="email" class="form-control" value="${khachHang.email}"></div>
        <div class="mb-3"><label>Giới tính:</label>
            <select name="gioiTinh" class="form-control">
                <option value="true" ${khachHang.gioiTinh ? 'selected' : ''}>Nam</option>
                <option value="false" ${!khachHang.gioiTinh ? 'selected' : ''}>Nữ</option>
            </select>
        </div>
        <div class="mb-3"><label>Số ĐT:</label><input type="text" name="soDienThoai" class="form-control" value="${khachHang.soDienThoai}"></div>
        <div class="mb-3"><label>Ngày sinh:</label><input type="date" name="ngaySinh" class="form-control" value="${khachHang.ngaySinh}"></div>
        <div class="mb-3"><label>Trạng thái:</label>
            <select name="trangThai" class="form-control">
                <option value="Đang hoạt động" ${khachHang.trangThai == 'Đang hoạt động' ? 'selected' : ''}>Đang hoạt động</option>
                <option value="Không hoạt động" ${khachHang.trangThai == 'Không hoạt động' ? 'selected' : ''}>Không hoạt động</option>
            </select>
        </div>
        <button type="submit" class="btn btn-warning">Cập nhật</button>
        <a href="/admin/khach-hang/view" class="btn btn-secondary">Quay lại</a>
    </form>
</div>

</body>
</html>
