package com.example.tradeservice;

import api.dtos.TradeRequestDto;
import api.dtos.TradeResultDto;

public interface TradeService {
    TradeResultDto executeTrade(TradeRequestDto dto);
}
