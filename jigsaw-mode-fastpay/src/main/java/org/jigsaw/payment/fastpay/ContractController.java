package org.jigsaw.payment.fastpay;


import javax.validation.Valid;

import org.jigsaw.payment.model.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 签约
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年9月3日
 */
@RestController
public class ContractController{
   
    @ResponseBody
    @RequestMapping("/contract")
    public ContractResponse bind(@Valid ContractRequest request) {    	
    	ContractResponse response = new ContractResponse();
    	response.setStatus(StatusCode.SUCCESS);
    	response.setMessage("Binded!");
    	return response;
    }


}
