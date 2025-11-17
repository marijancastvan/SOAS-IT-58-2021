package com.example.cryptoconversion;

import api.dtos.CryptoConversionDto;
import api.dtos.CryptoConversionResultDto;

public interface CryptoConversionService {
    CryptoConversionResultDto convert(CryptoConversionDto dto);
}
