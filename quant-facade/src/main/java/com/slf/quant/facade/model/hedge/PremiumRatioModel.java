package com.slf.quant.facade.model.hedge;

import java.math.BigDecimal;

import com.slf.quant.facade.model.QuoteDepth;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PremiumRatioModel implements Comparable
{
    private BigDecimal ratio;
    
    private QuoteDepth longQuoteDepth;
    
    private QuoteDepth shortQuoteDepth;
    
    @Override
    public int compareTo(Object o)
    {
        PremiumRatioModel model = (PremiumRatioModel) o;
        return model.getRatio().compareTo(this.ratio);
    }
}
