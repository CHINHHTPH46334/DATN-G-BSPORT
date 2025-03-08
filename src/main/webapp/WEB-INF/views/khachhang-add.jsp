<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm khách hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .form-label { font-weight: bold; margin-bottom: 0.2rem; }
        .error-text { font-size: 0.9em; display: block; margin-top: 0.2rem; }
        .form-group { margin-bottom: 1rem; }
    </style>
</head>
<body>
<div class="container mt-4">
    <h2 class="text-center mb-3">Thêm khách hàng</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <form action="/admin/khach-hang/add" method="post" class="row g-3">
        <div class="col-md-6 form-group">
            <label class="form-label">Mã KH:</label>
            <input type="text" name="maKhachHang" class="form-control" value="${khachHang.maKhachHang}">
            <c:if test="${not empty fieldErrors.maKhachHang}">
                <small class="text-danger error-text">${fieldErrors.maKhachHang}</small>
            </c:if>
        </div>
        <div class="col-md-6 form-group">
            <label class="form-label">Tên KH:</label>
            <input type="text" name="tenKhachHang" class="form-control" value="${khachHang.tenKhachHang}">
            <c:if test="${not empty fieldErrors.tenKhachHang}">
                <small class="text-danger error-text">${fieldErrors.tenKhachHang}</small>
            </c:if>
        </div>
        <div class="col-md-6 form-group">
            <label class="form-label">Email:</label>
            <input type="email" name="email" class="form-control" value="${khachHang.email}">
            <c:if test="${not empty fieldErrors.email}">
                <small class="text-danger error-text">${fieldErrors.email}</small>
            </c:if>
        </div>
        <div class="col-md-6 form-group">
            <label class="form-label">Giới tính:</label>
            <select name="gioiTinh" class="form-control">
                <option value="true" ${khachHang.gioiTinh ? 'selected' : ''}>Nam</option>
                <option value="false" ${!khachHang.gioiTinh ? 'selected' : ''}>Nữ</option>
            </select>
            <c:if test="${not empty fieldErrors.gioiTinh}">
                <small class="text-danger error-text">${fieldErrors.gioiTinh}</small>
            </c:if>
        </div>
        <div class="col-md-6 form-group">
            <label class="form-label">Số ĐT:</label>
            <input type="text" name="soDienThoai" class="form-control" value="${khachHang.soDienThoai}">
            <c:if test="${not empty fieldErrors.soDienThoai}">
                <small class="text-danger error-text">${fieldErrors.soDienThoai}</small>
            </c:if>
        </div>
        <div class="col-md-6 form-group">
            <label class="form-label">Ngày sinh:</label>
            <input type="date" name="ngaySinh" class="form-control" value="${khachHang.ngaySinh}">
            <c:if test="${not empty fieldErrors.ngaySinh}">
                <small class="text-danger error-text">${fieldErrors.ngaySinh}</small>
            </c:if>
        </div>
        <div class="col-md-6 form-group">
            <label class="form-label">Địa chỉ:</label>
            <textarea name="diaChi" class="form-control" rows="3">${khachHang.diaChi}</textarea>
            <c:if test="${not empty fieldErrors.diaChi}">
                <small class="text-danger error-text">${fieldErrors.diaChi}</small>
            </c:if>
        </div>
        <div class="col-md-6 form-group">
            <label class="form-label">Trạng thái:</label>
            <select name="trangThai" class="form-control">
                <option value="Đang hoạt động" ${khachHang.trangThai == 'Đang hoạt động' ? 'selected' : ''}>Đang hoạt động</option>
                <option value="Không hoạt động" ${khachHang.trangThai == 'Không hoạt động' ? 'selected' : ''}>Không hoạt động</option>
            </select>
            <c:if test="${not empty fieldErrors.trangThai}">
                <small class="text-danger error-text">${fieldErrors.trangThai}</small>
            </c:if>
        </div>
        <div class="col-12 text-center mt-3">
            <button type="submit" class="btn btn-success me-2">Thêm mới</button>
            <a href="/admin/khach-hang/view" class="btn btn-secondary">Quay lại</a>
        </div>
    </form>
</div>
</body>
</html>