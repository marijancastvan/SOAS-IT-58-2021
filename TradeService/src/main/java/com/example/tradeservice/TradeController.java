package com.example.tradeservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import api.dtos.TradeRequestDto;
import api.dtos.TradeResultDto;
import api.services.TradeService;

@RestController
@RequestMapping("/api/trade")
public class TradeController {

    private final TradeService service;

    public TradeController(TradeService service) {
        this.service = service;
    }

    /*@PostMapping
    public ResponseEntity<TradeResultDto> executeTrade(@RequestBody TradeRequestDto dto) {
        return ResponseEntity.ok(service.executeTrade(dto));
    }*/
    
    @PostMapping
    public ResponseEntity<TradeResultDto> executeTrade(@RequestBody TradeRequestDto dto) {
        try {
            return ResponseEntity.ok(service.executeTrade(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(new TradeResultDto(e.getMessage(), null, null));
        }
    }

}
