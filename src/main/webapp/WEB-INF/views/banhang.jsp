<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Bán hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://kit.fontawesome.com/fea5ab1520.js" crossorigin="anonymous"></script>
    <style>
        body {
            font-family: Arial, sans-serif;

        }
        .sidebar {
            background-color: #f8f9fa;
            border-right: 1px solid #a1a1a1;
        }
        .sidebar a {
            text-decoration: none;
            display: block;
            padding: 10px 20px;
            color: #ffffff;
        }
        .sidebar a:hover {
            border: 1px solid #a1a1a1;
        }
        .sidebar .active {
            border: 1px solid #a1a1a1;
        }
        .content {
            padding: 20px;
        }
        .backg {
            background-color: dimgrey;
        }
        .table-responsive {
            max-width: 100%;
            overflow-x: auto;
            white-space: nowrap
        }

        footer {
            background-color: #f8f9fa;
            padding: 10px 0;
            position: relative;
            width: 100%;
        }
        .co{
            background-color: #a1a1a1;
        }
        .dropdown-menu {
            background-color: dimgrey;
            color: white;
            padding: 10px;
        }
        .dropdown-item:hover {
            background-color: dimgrey;
            color: white;
        }
    </style>
</head>
<body class="container" style="width: 1700px">
<nav class="navbar navbar-expand-lg bg-body-tertiary">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">Navbar</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="#">Home</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#">Link</a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        Dropdown
                    </a>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="#">Action</a></li>
                        <li><a class="dropdown-item" href="#">Another action</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#">Something else here</a></li>
                    </ul>
                </li>
                <li class="nav-item">
                    <a class="nav-link disabled" aria-disabled="true">Disabled</a>
                </li>
            </ul>
            <form class="d-flex" role="search">
                <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search">
                <button class="btn btn-outline-success" type="submit">Search</button>
            </form>
        </div>
    </div>
</nav>
<div class="container">
    <div class="row mt-2">
        <div class="col-lg-8">
            <form action="/admin/ban-hang/view/add-hoa-don" method="post">
                <button type="submit" class="btn btn-primary">Tạo hoá đơn</button>
            </form>
            <br><br>
            <div class="input-group mb-6" style="width: 30%">
                <input type="text" class="form-control" >
                <button class="btn btn-outline-primary" type="button" id="button-addon2">Button</button>
            </div>
            <br>
            <div class="table-responsive">
                <table class="table table-bordered" style="width: 95%">
                    <thead class="co">
                    <tr style="text-align: center">
                        <th>STT</th>
                        <th>Mã hoá đơn</th>
                        <th>Ngày tạo</th>
                        <th>Tổng tiền</th>
                        <th>Trạng thái</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${listHoaDon}" var="hd" varStatus="s">
                        <tr style="text-align: center" onclick="window.location.href = '/admin/ban-hang/view/${hd.id_hoa_don}'">
                            <td>${s.count}</td>
                            <td>${hd.ma_hoa_don}</td>
                            <td>${hd.ngay_tao}</td>
                            <td>${hd.tong_tien_sau_giam}</td>
                            <td>${hd.trang_thai}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="table-responsive">
                <table class="table table-bordered" style="width: 95%">
                    <thead class="co">
                    <tr style="text-align: center">
                        <th>STT</th>
                        <th>Mã sản phẩm</th>
                        <th>Tên sản phẩm</th>
                        <th>Ảnh</th>
                        <th>Số lượng</th>
                        <th>Giá bán</th>
                        <th>Tổng tiền</th>
                        <th>Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${listGH}" var="gh" varStatus="s">
                        <tr style="text-align: center">
                            <td>${s.count}</td>
                            <td>${gh.ma_san_pham}</td>
                            <td>${gh.ten_san_pham}</td>
                            <td>${gh.hinh_anh}</td>
                            <td>${gh.so_luong}</td>
                            <td>${gh.gia_ban}</td>
                            <td>${gh.don_gia}</td>
                            <td><button class="btn btn-primary"><i class="fa-solid fa-pen"></i></button></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

        </div>
        <div class="form col-lg-4">
            <h2>Form Bán Hàng</h2>
            <form action="BanHangServlet" method="POST">
                <div class="mb-3">
                    <label for="maHoaDon" class="form-label">Mã hóa đơn</label>
                    <input type="text" class="form-control" id="maHoaDon" name="ma_hoa_don" value="${hdbh.ma_hoa_don}" readonly>
                </div>

                <div class="mb-3">
                    <label for="idNhanVien" class="form-label">Nhân viên</label>
                    <select class="form-select" id="idNhanVien" name="idNhanVien" required>
                        <c:choose>
                            <c:when test="${not empty hdbh and not empty hdbh.id_nhan_vien}">
                                <c:forEach var="nv" items="${listNV}">
                                    <c:if test="${nv.id_nhan_vien == hdbh.id_nhan_vien}">
                                        <option value="${nv.id_nhan_vien}" selected>${nv.ten_nhan_vien}</option>
                                    </c:if>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <option value="">Chọn nhân viên</option>
                                <c:forEach var="nv" items="${listNV}">
                                    <option value="${nv.id_nhan_vien}">${nv.ten_nhan_vien}</option>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="idKhachHang" class="form-label">Khách hàng</label>
                    <select class="form-select" id="idKhachHang" name="idKhachHang" required>
                        <option value="">Chọn khách hàng</option>
                        <c:choose>
                            <c:when test="${not empty hdbh}">
                                <c:forEach var="kh" items="${listKH}">
                                    <option value="${kh.idKhachHang}"
                                            <c:if test="${kh.idKhachHang == hdbh.id_khach_hang}">selected</c:if>>
                                            ${kh.tenKhachHang}
                                    </option>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="kh" items="${listKH}">
                                    <option value="${kh.idKhachHang}">${kh.tenKhachHang}</option>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="hinhThucThanhToan" class="form-label">Hình thức thanh toán</label>
                    <select class="form-select" id="hinhThucThanhToan" name="hinhThucThanhToan" required>
                        <option value="" <c:if test="${empty hdbh || hdbh.hinh_thuc_thanh_toan == null}">selected</c:if>>Chọn hình thức</option>
                        <option value="Tiền mặt" <c:if test="${not empty hdbh && hdbh.hinh_thuc_thanh_toan == 'Tiền mặt'}">selected</c:if>>Tiền mặt</option>
                        <option value="Chuyển khoản" <c:if test="${not empty hdbh && hdbh.hinh_thuc_thanh_toan == 'Chuyển khoản'}">selected</c:if>>Chuyển khoản</option>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="phuongThucNhanHang" class="form-label">Phương thức nhận hàng</label>
                    <select class="form-select" id="phuongThucNhanHang" name="phuongThucNhanHang" required>
                        <option value="">Chọn phương thức</option>
                        <option value="Giao hàng" <c:if test="${hdbh.phuong_thuc_nhan_hang == 'Giao hàng'}">selected</c:if>>Giao hàng</option>
                        <option value="Nhận tại cửa hàng" <c:if test="${hdbh.phuong_thuc_nhan_hang == 'Nhận tại cửa hàng'}">selected</c:if>>Nhận tại cửa hàng</option>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="phiVanChuyen" class="form-label">Phí vận chuyển</label>
                    <input type="number" class="form-control" id="phiVanChuyen" name="phiVanChuyen" value="0" min="0" onchange="">
                </div>

                <div class="mb-3">
                    <label for="tongTienTG" class="form-label">Tổng tiền trước giảm</label>
                    <input type="text" class="form-control" id="tongTienTG" name="tongTienTruocGiam" min="0" value="${hdbh.tong_tien_truoc_giam}" readonly>
                </div>

                <div class="mb-3">
                    <label for="idVoucher" class="form-label">Voucher (nếu có)</label>
                    <select class="form-select" id="idVoucher" name="idVoucher">
                        <option value="">Không áp dụng</option>
                        <c:choose>
                            <c:when test="${not empty hdbh}">
                                <!-- Nếu hdbh có giá trị, chọn idVoucher từ hdbh -->
                                <c:forEach var="voucher" items="${listVC}">
                                    <option value="${voucher.id}"
                                            <c:if test="${voucher.id == hdbh.id_voucher}">selected</c:if>>
                                            ${voucher.tenVoucher}
                                    </option>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <!-- Nếu hdbh không có giá trị, hiển thị danh sách bình thường -->
                                <c:forEach var="voucher" items="${listVC}">
                                    <option value="${voucher.id}">${voucher.tenVoucher}</option>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="tongTienSG" class="form-label">Tổng tiền sau giảm</label>
                    <input type="text" class="form-control" id="tongTienSG" name="tongTienSauGiam" min="0" value="${hdbh.tong_tien_sau_giam}" readonly>
                </div>

                <button type="submit" class="btn btn-success w-100">Thanh toán</button>
            </form>
        </div>
        <div class="table-responsive">
            <table class="table table_bordered">
                <thead>
                <tr style="text-align: center">
                    <th scope="col">STT</th>
                    <th scope="col">Tên sản phẩm</th>
    <%--                <th scope="col">Hình ảnh</th>--%>
                    <th scope="col">Giá bán</th>
                    <th scope="col">Số lượng</th>
    <%--                <th scope="col">Giới tính</th>--%>
                    <th scope="col">Chất liệu</th>
                    <th scope="col">Thương hiệu</th>
                    <th scope="col">Danh mục</th>
                    <th scope="col">Trạng thái</th>
                    <th scope="col">Hành động</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${listCTSP}" var="ctsp" varStatus="s">
                    <tr style="text-align: center">
                        <td>${s.count}</td>
                        <td>${ctsp.ten_san_pham}</td>
    <%--                    <td>${ctsp.hinh_anh}</td>--%>
                        <td>${ctsp.gia_ban}</td>
                        <td>${ctsp.so_luong}</td>
    <%--                    <td>${ctsp.gioi_tinh ? "Nam" : "Nữ"}</td>--%>
                        <td>${ctsp.ten_chat_lieu}</td>
                        <td>${ctsp.ten_thuong_hieu}</td>
                        <td>${ctsp.ten_danh_muc}</td>
                        <td>${ctsp.trang_thai}</td>
                        <td>
                            <button type="button"
                                    class="btn btn-primary"
                                    data-bs-toggle="modal"
                                    data-bs-target="#themSPGH"
                                    data-id="${ctsp.id_chi_tiet_san_pham}"
                                    data-sp="${ctsp.ten_san_pham}"
                                    data-slsp="${ctsp.so_luong}"
                                    <c:choose>
                                        <c:when test="${hdbh.id_hoa_don == null}">disabled</c:when>
                                        <c:otherwise></c:otherwise>
                                    </c:choose>
                                    onclick="openModal(this)">
                                <i class="fa-solid fa-cart-plus"></i>
                            </button>

                        </td>

                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="modal fade" id="themSPGH" tabindex="-1" aria-labelledby="themSPGHLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="themSPGHLabel">Thêm sản phẩm vào giỏ hàng</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form action="/admin/ban-hang/view/addAndUdateSPGH" method="get">
                            <input type="hidden" name="idCTSP" id="idSanPham">
                            <input type="hidden" name="idHoaDon" value="${hdbh.id_hoa_don}">
                            <div class="mb-3">
                                <label for="soLuong" class="form-label">Sản phẩm</label>
                                <input type="text" class="form-control" id="tenSP">
                            </div>
                            <div class="mb-3">
                                <label for="soLuong" class="form-label">Số lượng</label>
                                <input type="number" class="form-control" id="soLuong" name="soLuong" value="1" min="0" max="">
                            </div>
                            <button type="submit" class="btn btn-success">Thêm vào giỏ</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <footer class="co">
            <small style="margin-left: 15px;">Release 1.0</small>
        </footer>
    </div>

</div>
<script !src="">
    function openModal(button) {
        var id = button.getAttribute("data-id");
        var sp = button.getAttribute("data-sp");
        var slsp = button.getAttribute("data-slsp");

        document.getElementById("idSanPham").value = id;
        document.getElementById("tenSP").value = sp || "Không có tên";
        document.getElementById("soLuong").setAttribute("max", slsp);
        document.getElementById("soLuong").value = 1;
    }
    function submitForm() {
        var form = document.getElementById("addToCartForm");
        var formData = new FormData(form);

        fetch('/admin/ban-hang/add-to-cart', {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("Đã thêm vào giỏ hàng!");
                    // Đóng modal
                    var modal = bootstrap.Modal.getInstance(document.getElementById("themSPGH"));
                    modal.hide();
                    // Cập nhật giao diện nếu cần (ví dụ: số lượng sản phẩm trong giỏ)
                    updateCartUI(data.cartCount);
                } else {
                    alert("Có lỗi xảy ra!");
                }
            })
            .catch(error => console.error('Error:', error));
    }

    function updateCartUI(cartCount) {
        // Ví dụ: cập nhật số lượng sản phẩm trong giỏ trên giao diện
        document.getElementById("cartCount").textContent = cartCount;
    }
</script>
</body>
</html>