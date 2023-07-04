package com.cg.service.transfer;

import com.cg.model.Transfer;
import com.cg.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class TranferServiceImpl implements ITransferService{
    @Autowired
    private TransferRepository transferRepository;
    @Override
    public List<Transfer> findAll() {
        return transferRepository.findAll();
    }

    @Override
    public Optional<Transfer> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Transfer save(Transfer transfer) {
        return null;
    }

    @Override
    public void delete(Transfer transfer) {

    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public BigDecimal getAllFeesAmount() {
        return transferRepository.getAllFeesAmount();
    }
}
