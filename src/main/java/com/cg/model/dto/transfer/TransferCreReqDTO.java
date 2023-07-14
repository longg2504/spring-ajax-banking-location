package com.cg.model.dto.transfer;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class TransferCreReqDTO implements Validator {

    private Long senderId;
    private Long recipientId;
    private String transferAmount;

    @Override
    public boolean supports(Class<?> aClass) {
        return TransferCreReqDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        TransferCreReqDTO transferCreReqDTO = (TransferCreReqDTO) o;

        String transferAmountStr = transferCreReqDTO.getTransferAmount();


        if (transferAmountStr == null) {
            errors.rejectValue("transferAmount", "transferAmount.null", "Số tiền chuyển là bắt buộc");
            return;
        }

        if (transferAmountStr.length() == 0) {
            errors.rejectValue("transferAmount", "transferAmount.length", "Vui lòng nhập số tiền muốn chuyển");
        }
        else {
            if (!transferAmountStr.matches("\\d+")) {
                errors.rejectValue("transferAmount", "transferAmount.matches", "Vui lòng nhập giá trị tiền bằng chữ số");
            }
            else {
                BigDecimal transactionAmount = BigDecimal.valueOf(Long.parseLong(transferAmountStr));

                if (transactionAmount.compareTo(BigDecimal.valueOf(100L)) <= 0) {
                    errors.rejectValue("transferAmount", "transferAmount.min", "Số tiền muốn chuyển thấp nhất là $100");
                }

            }
        }
    }
}
