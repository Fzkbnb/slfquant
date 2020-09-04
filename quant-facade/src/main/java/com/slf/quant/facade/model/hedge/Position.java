package com.slf.quant.facade.model.hedge;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Position {
    private BigDecimal longCont = BigDecimal.ZERO;
    private BigDecimal shortCont = BigDecimal.ZERO;
}
