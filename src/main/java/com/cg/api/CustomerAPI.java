package com.cg.api;

import com.cg.exception.DataInputException;
import com.cg.exception.EmailExistsException;
import com.cg.model.Customer;
import com.cg.model.dto.customer.*;
import com.cg.model.dto.locationRegion.LocationRegionReqDTO;
import com.cg.service.customer.ICustomerService;
import com.cg.utils.AppUtils;
import com.cg.utils.ValidateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/customers")
public class CustomerAPI {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private AppUtils appUtils;

    @Autowired
    private ValidateUtils validateUtils;

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {

//        List<Customer> customers = customerService.findAll();

        List<CustomerResDTO> customerResDTOS = customerService.findAllCustomerResDTOS();

//        for (Customer item : customers) {
//            CustomerResDTO customerResDTO = item.toCustomerResDTO();
//
//            customerResDTOS.add(customerResDTO);
//        }

        return new ResponseEntity<>(customerResDTOS, HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getById(@PathVariable Long customerId) {

        Customer customer = customerService.findById(customerId).orElseThrow(() -> {
            throw new DataInputException("Mã khách hàng không tồn tại");
        });

        CustomerResDTO customerResDTO = customer.toCustomerResDTO();

        return new ResponseEntity<>(customerResDTO, HttpStatus.OK);
    }

    @GetMapping("/recipients-without-sender/{senderId}")
    public ResponseEntity<?> getAllRecipientsWithoutSender(@PathVariable Long senderId) {

        List<CustomerResDTO> recipients = customerService.findAllRecipientsWithoutSenderId(senderId);

        return new ResponseEntity<>(recipients, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CustomerCreReqDTO customerCreReqDTO) {

        String email = customerCreReqDTO.getEmail().trim();

        Boolean emailExists = customerService.existsByEmail(email);

        if (emailExists) {
            throw new EmailExistsException("Email đã tồn tại");
        }

        CustomerCreResDTO customerCreResDTO = customerService.create(customerCreReqDTO);

        return new ResponseEntity<>(customerCreResDTO, HttpStatus.CREATED);
    }

    @PatchMapping("/delete/{customerId}")
    public ResponseEntity<?> doDelete(@PathVariable("customerId") String customerIdstr){
        if(!ValidateUtils.isNumberValid(customerIdstr)){
            Map<String,String> data = new HashMap<>();
            data.put("message","Mã khách hàng không hợp lệ");
            return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
        }
        Long customerId = Long.parseLong(customerIdstr);
        Optional<Customer> customerOptional = customerService.findById(customerId);
        Customer customer = customerOptional.get();
        customer.setDeleted(true);
        customerService.save(customer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody CustomerUpdateReqDTO customerUpdateReqDTO, BindingResult bindingResult) {

        if (!ValidateUtils.isNumberValid(id)) {
            throw new DataInputException("Mã khách hàng không hợp lệ");
        }

        new CustomerUpdateReqDTO().validate(customerUpdateReqDTO, bindingResult);

        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        Long customerId = Long.parseLong(id);

        Optional<Customer> customerOptional = customerService.findById(customerId);

        if (!customerOptional.isPresent()) {
            throw new DataInputException("Mã khách hàng không tồn tại");
        }

        Boolean existEmail = customerService.existsByEmailAndIdNot(customerUpdateReqDTO.getEmail(), customerId);

        if (existEmail) {
            throw new EmailExistsException("Email đã tồn tại");
        }

        Customer customer = customerUpdateReqDTO.toCustomer();
        Customer updateCustomer = customerOptional.get();

        customer.setId(updateCustomer.getId());

        customerService.update(customer, updateCustomer.getLocationRegion());

        customer.setBalance(customerOptional.get().getBalance());

        CustomerUpdateResDTO customerUpdateResDTO = customer.toCustomerUpdateResDTO();

        return new ResponseEntity<>(customerUpdateResDTO, HttpStatus.OK);
    }

}
