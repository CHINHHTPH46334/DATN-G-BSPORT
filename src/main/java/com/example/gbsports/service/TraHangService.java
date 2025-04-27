package com.example.gbsports.service;

import com.example.gbsports.request.HoaDonRequest;
import com.example.gbsports.request.TraHangRequest;

import com.example.gbsports.response.HoaDonResponse;
import com.example.gbsports.response.TraHangResponse;



public interface TraHangService {

    TraHangResponse taoYeuCauTraHang(TraHangRequest request);

}
