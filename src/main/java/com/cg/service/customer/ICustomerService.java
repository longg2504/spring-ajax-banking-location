package com.cg.service.customer;

import com.cg.model.*;
import com.cg.model.dto.customer.CustomerCreReqDTO;
import com.cg.model.dto.customer.CustomerCreResDTO;
import com.cg.model.dto.customer.CustomerResDTO;
import com.cg.model.dto.transfer.TransferCreReqDTO;
import com.cg.service.IGeneralService;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ICustomerService extends IGeneralService<Customer, Long> {

    List<Customer> findAllByIdNot(Long id);

    List<CustomerResDTO> findAllCustomerResDTOS();

    List<CustomerResDTO> findAllRecipientsWithoutSenderId(@Param("senderId") Long senderId);

    Boolean existsByEmail(String email);
    Boolean existsByEmailAndIdNot(String email, Long id);

    CustomerCreResDTO create(CustomerCreReqDTO customerCreReqDTO);

    Customer deposit(Deposit deposit);
    Customer withdraw(Withdraw withdraw);

    void transfer(TransferCreReqDTO transferCreReqDTO);

    void incrementBalance(Long id, BigDecimal amount);

    void transfer(Transfer transfer);

    Customer update(Customer customer, LocationRegion oldLocationRegion);
}
