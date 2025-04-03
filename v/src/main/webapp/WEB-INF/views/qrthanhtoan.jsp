<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Thanh toán bằng QR Code</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>Vui lòng quét mã QR để thanh toán</h1>
<img src="${qrCodeUrl}" alt="QR Code">
<p>${message}</p>
</body>
</html>