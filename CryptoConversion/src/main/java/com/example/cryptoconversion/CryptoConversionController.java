package com.example.cryptoconversion;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import api.dtos.CryptoConversionDto;
import api.dtos.CryptoConversionResultDto;

@RestController
@RequestMapping("/api/conversion")
public class CryptoConversionController {

    private final CryptoConversionService service;

    public CryptoConversionController(CryptoConversionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CryptoConversionResultDto> convert(@RequestBody CryptoConversionDto dto) {
        return ResponseEntity.ok(service.convert(dto));
    }
}
