package com.example.gbsports.ImportAndExportEx;

import com.example.gbsports.request.ChiTietSanPhamRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Service
public class ChiTietSanPhamValidate {
    private final Validator validator;

    public List<String> validate(List<ChiTietSanPhamRequest> list) {
        return list.stream()
                .flatMap(item -> {
                    Set<ConstraintViolation<ChiTietSanPhamRequest>> violations = validator.validate(item);
                    return violations.stream().map(v -> "Lỗi dòng " + (list.indexOf(item) + 1) + ": " + v.getMessage());
                })
                .collect(Collectors.toList());
    }

}
