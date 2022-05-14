package com.kiran.ms.ccs.controllers;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.kiran.ms.ccs.feignClients.CurrencyExchangeProxy;
import com.kiran.ms.ccs.vos.CurrencyConversion;

@RestController
public class CurrencyConversionController {
	
	@Autowired
	CurrencyExchangeProxy ceProxy;

	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{qno}")
	public CurrencyConversion getCurrencyConversion(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal qno)
	{
		return getFromRestTemplate(from,to,qno);
	}
	
	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{qno}")
	public CurrencyConversion getCurrencyConversionFeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal qno)
	{
		return getFromFeignClient(from,to,qno);
	}
	
	private CurrencyConversion getFromRestTemplate(String from, String to, BigDecimal qno)
	{
		HashMap<String, String> reqVar = new HashMap<>();
		reqVar.put("from",from);
		reqVar.put("to", to);
		ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity
				("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class, reqVar);
		CurrencyConversion cv = responseEntity.getBody();
		return new CurrencyConversion(
				cv.getId(), cv.getFrom(), cv.getTo(), cv.getConversionMultiple(), qno, qno.multiply(cv.getConversionMultiple()) );
		
	}
	
	private CurrencyConversion getFromFeignClient(String from, String to, BigDecimal qno)
	{
		CurrencyConversion cv = ceProxy.retrieveExchangeValue(from, to);
		return new CurrencyConversion(
				cv.getId(), cv.getFrom(), cv.getTo(), cv.getConversionMultiple(), qno, qno.multiply(cv.getConversionMultiple()) );
		
	}
	
}

