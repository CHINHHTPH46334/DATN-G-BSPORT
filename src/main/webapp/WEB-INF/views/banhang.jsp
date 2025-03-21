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

        .co {
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
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
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
                    <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                       aria-expanded="false">
                        Dropdown
                    </a>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="#">Action</a></li>
                        <li><a class="dropdown-item" href="#">Another action</a></li>
                        <li>
                            <hr class="dropdown-divider">
                        </li>
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
            <br>
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
                        <tr style="text-align: center"
                            onclick="window.location.href = '/admin/ban-hang/view/${hd.id_hoa_don}?idHd=${hd.id_hoa_don}'">
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
                            <td>
                                <button class="btn btn-primary">
                                    <i class="fa-solid fa-pen"></i>
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

        </div>
        <div class="form col-lg-4">
            <h2>Form Bán Hàng</h2>
            <form id="thanhToanForm" action="/admin/ban-hang/thanh-toan" method="POST">
                <input type="hidden" name="idHoaDon" id="idHoaDon" value="${hdbh != null ? hdbh.id_hoa_don : ''}">
                <div class="mb-3">
                    <label for="maHoaDon" class="form-label">Mã hóa đơn</label>
                    <input type="text" class="form-control" id="maHoaDon" name="ma_hoa_don" value="${hdbh.ma_hoa_don}"
                           readonly>
                </div>

                <div class="mb-3">
                    <label for="idNhanVien" class="form-label">Nhân viên</label>
                    <select class="form-select" id="idNhanVien" name="idNhanVien" required>
                        <c:choose>
                            <c:when test="${not empty hdbh and not empty hdbh.id_nhan_vien}">
                                <c:forEach var="nv" items="${listNV}">
                                    <c:if test="${nv.idNhanVien == hdbh.id_nhan_vien}">
                                        <option value="${nv.idNhanVien}" selected>${nv.tenNhanVien}</option>
                                    </c:if>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <option value="">Chọn nhân viên</option>
                                <c:forEach var="nv" items="${listNV}">
                                    <option value="${nv.idNhanVien}">${nv.tenNhanVien}</option>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="idKhachHang" class="form-label">Khách hàng</label>
                    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#themKhachHangModal">
                        Thêm khách hàng mới
                    </button>
                    <select class="form-select" id="idKhachHang" name="idKhachHang" required>
                        <option value="">Chọn khách hàng</option>
                        <c:forEach var="kh" items="${listKH}">
                            <option value="${kh.idKhachHang}"
                                    <c:if test="${hdbh != null && kh.idKhachHang == hdbh.id_khach_hang}">selected</c:if>>
                                    ${kh.tenKhachHang}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="hinhThucThanhToan" class="form-label">Hình thức thanh toán</label>
                    <select class="form-select" id="hinhThucThanhToan" name="hinhThucThanhToan" required>
                        <option value=""
                                <c:if test="${empty hdbh || hdbh.hinh_thuc_thanh_toan == null}">selected</c:if>>Chọn
                            hình thức
                        </option>
                        <option value="Tiền mặt"
                                <c:if test="${not empty hdbh && hdbh.hinh_thuc_thanh_toan == 'Tiền mặt'}">selected</c:if>>
                            Tiền mặt
                        </option>
                        <option value="Chuyển khoản"
                                <c:if test="${not empty hdbh && hdbh.hinh_thuc_thanh_toan == 'Chuyển khoản'}">selected</c:if>>
                            Chuyển khoản
                        </option>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="phuongThucNhanHang" class="form-label">Phương thức nhận hàng</label>
                    <select class="form-select" id="phuongThucNhanHang" name="phuongThucNhanHang" required>
                        <option value="">Chọn phương thức</option>
                        <option value="Giao hàng"
                                <c:if test="${hdbh.phuong_thuc_nhan_hang == 'Giao hàng'}">selected</c:if>>Giao hàng
                        </option>
                        <option value="Nhận tại cửa hàng"
                                <c:if test="${hdbh.phuong_thuc_nhan_hang == 'Nhận tại cửa hàng'}">selected</c:if>>Nhận
                            tại cửa hàng
                        </option>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="phiVanChuyen" class="form-label">Phí vận chuyển</label>
                    <input type="number" class="form-control" id="phiVanChuyen" name="phiVanChuyen" value="0" min="0"
                           onchange="updatePhiVanChuyen()">
                </div>

                <div class="mb-3">
                    <label for="tongTienTG" class="form-label">Tổng tiền trước giảm</label>
                    <input type="text" class="form-control" id="tongTienTG" name="tongTienTruocGiam" min="0"
                           value="${hdbh.tong_tien_truoc_giam}" readonly>
                </div>

                <div class="mb-3">
                    <label for="idVoucher" class="form-label">Voucher (nếu có)</label>
                    <select class="form-select" id="idVoucher" name="idVoucher" onchange="updateVoucher()">
                        <option value="">Không áp dụng</option>
                        <c:forEach var="voucher" items="${listVC}">
                            <c:if test="${hdbh.tong_tien_truoc_giam >= voucher.giaTriToiThieu}">
                                <option value="${voucher.id}"
                                        data-giatrigiam="${voucher.giaTriGiam}"
                                        <c:if test="${voucher.id == hdbh.id_voucher}">selected</c:if>>
                                        ${voucher.tenVoucher} - Giảm ${voucher.giaTriGiam}
                                </option>
                            </c:if>
                        </c:forEach>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="tongTienSG" class="form-label">Tổng tiền sau giảm</label>
                    <input type="text" class="form-control" id="tongTienSG" name="tongTienSauGiam" min="0"
                           value="${hdbh.tong_tien_sau_giam}" readonly>
                </div>

                <div class="mb-3" id="tienKhachDuaGroup" style="display: none;">
                    <label for="tienKhachDua" class="form-label">Tiền khách đưa</label>
                    <input type="number" class="form-control" id="tienKhachDua" name="tienKhachDua" min="0">
                </div>

                <div class="mb-3" id="tienTraLaiGroup" style="display: none;">
                    <label for="tienTraLai" class="form-label">Tiền trả lại</label>
                    <input type="number" class="form-control" id="tienTraLai" name="tienTraLai" readonly>
                </div>

                <button type="submit"
                        class="btn btn-success w-100">
                    Thanh toán
                </button>
            </form>
        </div>
        <div class="input-group mb-6" style="width: 30%">
            <input type="text" class="form-control">
            <button class="btn btn-outline-primary" type="button" id="button-addon2">Button</button>
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
                                    data-idctsp="${ctsp.id_chi_tiet_san_pham}"
                                    data-tensp="${ctsp.ten_san_pham}"
                                    data-slsp="${ctsp.so_luong}"
                                    <c:choose>
                                        <c:when test="${hdbh.id_hoa_don == null}">disabled</c:when>
                                        <c:otherwise></c:otherwise>
                                    </c:choose>>
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
                            <input type="hidden" name="idCTSP" id="id_chi_tiet_san_phamadd">
                            <input type="hidden" name="idHoaDonADGH" value="${hdbh.id_hoa_don}">
                            <div class="mb-3">
                                <label for="tenSP" class="form-label">Sản phẩm</label>
                                <input type="text" class="form-control" id="tenSP">
                            </div>
                            <div class="mb-3">
                                <label for="soLuong" class="form-label">Số lượng</label>
                                <input type="number" class="form-control" id="soLuong" name="soLuong" value="1" min="0">
                            </div>
                            <button type="submit" class="btn btn-success">Thêm vào giỏ</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="themKhachHangModal" tabindex="-1" aria-labelledby="themKhachHangModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="themKhachHangModalLabel">Thêm khách hàng mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="themKhachHangForm">
                            <div class="mb-3">
                                <label for="tenKhachHang" class="form-label">Tên khách hàng</label>
                                <input type="text" class="form-control" id="tenKhachHang" name="tenKhachHang" required>
                            </div>
                            <div class="mb-3">
                                <label for="sdt" class="form-label">Số điện thoại</label>
                                <input type="text" class="form-control" id="sdt" name="sdt" required>
                            </div>
                            <div class="mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input type="email" class="form-control" id="email" name="email">
                            </div>
                            <div class="mb-3">
                                <label for="diaChi" class="form-label">Địa chỉ</label>
                                <input type="text" class="form-control" id="diaChi" name="diaChi">
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                        <button type="button" class="btn btn-primary" onclick="themKhachHang()">Lưu</button>
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
    document.addEventListener('DOMContentLoaded', function () {
        const hinhThucThanhToan = document.getElementById('hinhThucThanhToan');
        const tienKhachDuaGroup = document.getElementById('tienKhachDuaGroup');
        const tienTraLaiGroup = document.getElementById('tienTraLaiGroup');
        const form = document.getElementById('thanhToanForm');

        if (!form || !hinhThucThanhToan || !tienKhachDuaGroup || !tienTraLaiGroup) {
            console.error('Không tìm thấy một hoặc nhiều phần tử HTML cần thiết.');
            return;
        }

        hinhThucThanhToan.addEventListener('change', function () {
            if (this.value === 'Tiền mặt') {
                tienKhachDuaGroup.style.display = 'block';
                tienTraLaiGroup.style.display = 'block';
            } else {
                tienKhachDuaGroup.style.display = 'none';
                tienTraLaiGroup.style.display = 'none';
            }
        });

        document.getElementById('tienKhachDua').addEventListener('input', function () {
            const tienKhachDua = parseFloat(this.value) || 0;
            const tongTienSauGiam = parseFloat(document.getElementById('tongTienSG').value) || 0;
            const tienTraLai = tienKhachDua - tongTienSauGiam;

            document.getElementById('tienTraLai').value = tienTraLai >= 0 ? tienTraLai.toFixed(2) : '0';
        });

        form.addEventListener('submit', function (event) {
            event.preventDefault();

            const hinhThucThanhToanValue = hinhThucThanhToan.value;
            const idHoaDon = document.getElementById("idHoaDon").value;
            const idKhachHang = document.getElementById('idKhachHang').value;
            const tongTienSauGiam = document.getElementById('tongTienSG')?.value;

            if (hinhThucThanhToanValue === 'Tiền mặt') {
                const tienKhachDua = parseFloat(document.getElementById('tienKhachDua').value) || 0;
                const tongTienSauGiamValue = parseFloat(document.getElementById('tongTienSG').value) || 0;

                if (tienKhachDua < tongTienSauGiamValue) {
                    alert('Số tiền khách đưa không đủ để thanh toán!');
                } else {
                    alert('Thanh toán thành công!');
                    const inHoaDon = confirm('Bạn có muốn in hoá đơn không?');
                    if (inHoaDon) {
                        window.print();
                    }
                    fetch('/admin/ban-hang/thanh-toan', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: `id_hoa_don=${idHoaDon}&tienKhachDua=${tienKhachDua}&tongTienSauGiam=${tongTienSauGiamValue}&hinhThucThanhToan=${hinhThucThanhToanValue}`
                    })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                alert('Thanh toán thành công!');
                                window.location = 'view';
                            } else {
                                alert('Lỗi khi thanh toán: ' + data.message);
                            }
                        })
                        .catch(error => console.error('Lỗi khi thanh toán:', error));

                    // Gửi yêu cầu cập nhật trạng thái hóa đơn
                    // updateInvoiceStatus(idHoaDon, 'Đã thanh toán').then(() => {
                    //     window.location = 'view';
                    // });
                }
            } else if (hinhThucThanhToanValue === 'Chuyển khoản') {
                if (!idHoaDon || !idKhachHang || !tongTienSauGiam) {
                    console.log(idHoaDon + ":" + idKhachHang + ":" + tongTienSauGiam);
                    alert('Thiếu thông tin cần thiết để thanh toán!');
                    return;
                }

                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '/api/zalopay/create-order';

                const idKhachHangInput = document.createElement('input');
                idKhachHangInput.type = 'hidden';
                idKhachHangInput.name = 'idKhachHang';
                idKhachHangInput.value = idKhachHang;

                const tongTienSauGiamInput = document.createElement('input');
                tongTienSauGiamInput.type = 'hidden';
                tongTienSauGiamInput.name = 'tongTienSauGiam';
                tongTienSauGiamInput.value = tongTienSauGiam;

                const idHoaDonInput = document.createElement('input');
                idHoaDonInput.type = 'hidden';
                idHoaDonInput.name = 'id_hoa_don';
                idHoaDonInput.value = idHoaDon;

                form.appendChild(idKhachHangInput);
                form.appendChild(tongTienSauGiamInput);
                form.appendChild(idHoaDonInput);

                document.body.appendChild(form);
                form.submit();
            }
        });

        // function updateInvoiceStatus(idHoaDon, status) {
        //     console.log("phương thức status")
        //     return fetch('/api/hoa-don/update-status', {
        //         method: 'POST',
        //         headers: {
        //             'Content-Type': 'application/json',
        //         },
        //         body: JSON.stringify({ idHoaDon, status })
        //
        //     })
        //         .then(response => response.json())
        //         .then(data => {
        //             if (data.success) {
        //                 console.log('Cập nhật trạng thái hóa đơn thành công!');
        //             } else {
        //                 console.error('Lỗi khi cập nhật trạng thái hóa đơn:', data.message);
        //             }
        //         })
        //         .catch(error => {
        //             console.error('Lỗi khi gửi yêu cầu cập nhật trạng thái:', error);
        //         });
        // }

        const modal = document.getElementById('themSPGH');
        if (modal) {
            modal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const idCTSP = button.getAttribute('data-idctsp');
                const tenSP = button.getAttribute('data-tensp');
                const soLuongTon = button.getAttribute('data-slsp');

                const idInput = document.getElementById('id_chi_tiet_san_phamadd');
                const tenInput = document.getElementById('tenSP');
                const soLuongInput = document.getElementById('soLuong');

                idInput.value = idCTSP || '';
                tenInput.value = tenSP || '';
                soLuongInput.max = soLuongTon || '';
                soLuongInput.dataset.max = soLuongTon;
            });
        }

        const soLuongInput = document.getElementById('soLuong');
        if (soLuongInput) {
            soLuongInput.addEventListener('input', function (e) {
                let soLuong = parseInt(e.target.value) || 0;
                let idCTSP = document.getElementById('id_chi_tiet_san_phamadd').value;

                if (soLuong < 0) {
                    alert('Số lượng không thể nhỏ hơn 0');
                    e.target.value = 0;
                    return;
                }

                fetch(`/admin/ban-hang/check-so-luong?idCTSP=${idCTSP}`)
                    .then(response => response.json())
                    .then(data => {
                        if (data.soLuongTon < soLuong) {
                            alert(`Số lượng tồn kho chỉ còn ${data.soLuongTon}`);
                            e.target.value = data.soLuongTon;
                        }
                        e.target.max = data.soLuongTon;
                    })
                    .catch(error => console.error('Lỗi:', error));
            });
        }

        const themSPGHForm = document.querySelector('#themSPGH form');
        if (themSPGHForm) {
            themSPGHForm.addEventListener('submit', function (event) {
                const soLuongInput = document.getElementById('soLuong');
                const soLuong = parseInt(soLuongInput.value) || 0;
                const maxSoLuong = parseInt(soLuongInput.dataset.max);

                if (soLuong > maxSoLuong) {
                    alert('Số lượng nhập vào vượt quá số lượng tồn kho!');
                    event.preventDefault();
                }
            });
        }

        const idVoucherSelect = document.getElementById('idVoucher');
        if (idVoucherSelect) {
            idVoucherSelect.addEventListener('change', updateVoucher);
        }

        const idKhachHangSelect = document.getElementById('idKhachHang');
        if (idKhachHangSelect) {
            idKhachHangSelect.addEventListener('change', function() {
                const idKhachHang = this.value;
                const idHoaDon = document.getElementById("idHoaDon").value;

                if (!idHoaDon || !idKhachHang) {
                    alert("Vui lòng chọn hóa đơn và khách hàng!");
                    return;
                }

                fetch('/admin/ban-hang/update-khach-hang', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `idHoaDonUDKH=${idHoaDon}&idKhachHangUDKH=${idKhachHang}`
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            alert('Cập nhật khách hàng thành công!');
                        } else {
                            alert('Không thể cập nhật khách hàng: ' + data.message);
                        }
                    })
                    .catch(error => console.error('Lỗi khi cập nhật khách hàng:', error));
            });
        }
    });

    function themKhachHang() {
        const tenKhachHang = document.getElementById('tenKhachHang').value;
        const sdt = document.getElementById('sdt').value;
        const email = document.getElementById('email').value;
        const diaChi = document.getElementById('diaChi').value;

        if (!tenKhachHang || !sdt) {
            alert("Vui lòng nhập tên khách hàng và số điện thoại!");
            return;
        }

        fetch('/admin/khach-hang/them-moi', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                tenKhachHang: tenKhachHang,
                sdt: sdt,
                email: email,
                diaChi: diaChi
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("Thêm khách hàng mới thành công!");
                    // Đóng modal
                    $('#themKhachHangModal').modal('hide');
                    // Cập nhật combobox với khách hàng mới
                    const option = document.createElement('option');
                    option.value = data.idKhachHang;
                    option.text = tenKhachHang;
                    document.getElementById('idKhachHang').appendChild(option);
                    // Chọn khách hàng mới
                    document.getElementById('idKhachHang').value = data.idKhachHang;
                } else {
                    alert("Lỗi khi thêm khách hàng: " + data.message);
                }
            })
            .catch(error => console.error('Lỗi khi thêm khách hàng:', error));
    }

    function updateVoucher() {
        const idVoucher = document.getElementById('idVoucher').value;
        const idHoaDon = document.querySelector('input[name="idHoaDon"]')?.value;

        if (idHoaDon) {
            fetch('/admin/ban-hang/update-voucher', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `idHoaDon=${idHoaDon}&idVoucher=${idVoucher}`
            })
                .then(response => response.json())
                .then(data => {
                    if (data.tongTienSauGiam !== undefined) {
                        document.getElementById('tongTienSG').value = data.tongTienSauGiam;
                    }
                    if (data.tongTienTruocGiam !== undefined) {
                        document.getElementById('tongTienTG').value = data.tongTienTruocGiam;
                    }
                })
                .catch(error => console.error('Lỗi khi cập nhật voucher:', error));
        }
    }

    function updatePhiVanChuyen() {
        const phiVanChuyen = document.getElementById('phiVanChuyen').value || 0;
        const idHoaDon = document.querySelector('input[name="idHoaDon"]')?.value;

        if (idHoaDon) {
            fetch('/admin/ban-hang/update-phi-van-chuyen', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `idHoaDon=${idHoaDon}&phiVanChuyen=${phiVanChuyen}`
            })
                .then(response => response.json())
                .then(data => {
                    document.getElementById('tongTienTG').value = data.tongTienTruocGiam;
                    document.getElementById('tongTienSG').value = data.tongTienSauGiam;

                    if (data.reloadVouchers) {
                        location.reload();
                    }
                })
                .catch(error => console.error('Lỗi khi cập nhật phí vận chuyển:', error));
        }
    }
</script>
</body>
</html>