package com.cg.service.transfer;

import com.cg.model.Transfer;
import com.cg.service.IGeneralService;

import java.math.BigDecimal;

public interface ITransferService extends IGeneralService<Transfer,Long> {
    BigDecimal getAllFeesAmount();
}
