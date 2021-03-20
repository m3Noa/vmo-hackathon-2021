package com.scalar.carapp.controller;

import com.scalar.base.model.CustomerEntity;
import com.scalar.base.utils.BaseUtils;
import com.scalar.carapp.service.CustomerService;

import java.util.UUID;

import javax.json.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/v1/api/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@GetMapping
	public ResponseEntity<String> list(@RequestParam(value="keywords", required = false) String keywords) {
		return customerService.list(keywords);
	}
	
	@GetMapping("/all")
	public ResponseEntity<String> listAll(@RequestParam(value="keywords", required = false) String keywords) {
		return customerService.listAll(keywords);
	}
	
	@PostMapping
	public ResponseEntity<String> create(@RequestBody CustomerEntity body) {
		UUID id = UUID.randomUUID();
		
		if(this.isValidCreateData(body)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Json.createObjectBuilder()
					.add("status", "Faild")
					.add("message", "a required key is missing: [firstName] or [lastName] or [address] or [mobilephone]")
					.build().toString());
		}
		
		// turn on status change date
		BaseUtils.turnOnStatus();
		
		String createBy = "system";
		if(body.getCreateBy() != null && !BaseUtils.isEmptyString(body.getCreateBy())) {
			createBy = body.getCreateBy();
		}
		return customerService.create(id.toString(), body.getFirstName(), body.getLastName(), body.getAddress(), body.getMobilephone(), createBy);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<String> update(@PathVariable("id") String id, @RequestBody CustomerEntity body) {
		if(this.isValidCreateData(body)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Json.createObjectBuilder()
					.add("status", "Faild")
					.add("message", "a required key is missing: [firstName] or [lastName] or [address] or [mobilephone]")
					.build().toString());
		}
		
		// turn on status change date
		BaseUtils.turnOnStatus();
		
		String updateBy = "system";
		if(body.getUpdateBy() != null && !BaseUtils.isEmptyString(body.getUpdateBy())) {
			updateBy = body.getUpdateBy();
		}
		return customerService.update(id.toString(), body.getFirstName(), body.getLastName(), body.getAddress(), body.getMobilephone(), updateBy, body.getIsActive());
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<String> details(@PathVariable("id") String id) {
		return customerService.details(id);
	}
	
	/**
	 * Validate status valid of data when create customer
	 * @param body
	 * @return
	 */
	private boolean isValidCreateData(CustomerEntity body) {
		
		boolean rs = false;
		
		if(body == null) {
			rs = true;
		}
		else if(body.getFirstName() == null || body.getFirstName().isEmpty()) {
			rs = true;
		}
		else if(body.getLastName() == null || body.getLastName().isEmpty()) {
			rs = true;
		}
		else if(body.getAddress() == null || body.getAddress().isEmpty()) {
			rs = true;
		}
		else if(body.getMobilephone() == null || body.getMobilephone().isEmpty()) {
			rs = true;
		}
		return rs;
	}
}
