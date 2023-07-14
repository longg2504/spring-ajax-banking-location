package com.cg.api;


import com.cg.model.Customer;
import com.cg.model.dto.deposit.DepositCreReqDTO;
import com.cg.model.dto.transfer.TransferCreReqDTO;
import com.cg.model.dto.transfer.TransferCreResDTO;
import com.cg.service.customer.ICustomerService;
import com.cg.utils.AppUtils;
import com.cg.utils.ValidateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/transfers")
public class TransferAPI {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private AppUtils appUtils;

    @Autowired
    private ValidateUtils validateUtils;
    @PostMapping
    public ResponseEntity<?> transfer(@RequestBody TransferCreReqDTO transferCreReqDTO , BindingResult bindingResult) {

        new TransferCreReqDTO().validate(transferCreReqDTO, bindingResult);
        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }


        Long senderId = transferCreReqDTO.getSenderId();
        Long recipientId = transferCreReqDTO.getRecipientId();

        customerService.transfer(transferCreReqDTO);

        TransferCreResDTO transferCreResDTO = new TransferCreResDTO();
        Customer sender = customerService.findById(senderId).get();
        Customer recipient = customerService.findById(recipientId).get();

        transferCreResDTO.setSender(sender.toCustomerResDTO());
        transferCreResDTO.setRecipient(recipient.toCustomerResDTO());

//        Map<String, CustomerResDTO> data = new HashMap<>();
//        data.put("sender", sender.toCustomerResDTO());
//        data.put("recipient", recipient.toCustomerResDTO());

        return new ResponseEntity<>(transferCreResDTO, HttpStatus.OK);
    }
}
