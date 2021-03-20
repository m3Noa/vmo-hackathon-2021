package com.scalar.carapp.controller;

import com.scalar.base.model.CarCategoryEntity;
import com.scalar.base.utils.BaseUtils;
import com.scalar.carapp.service.CarCategoryService;
import java.util.UUID;

import javax.json.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/v1/api/car-cartegory")
public class CarCategoryController {

	@Autowired
	private CarCategoryService carCategoryService;

	@GetMapping
	public ResponseEntity<String> list(@RequestParam(value="keywords", required = false) String keywords) {
		return carCategoryService.list(keywords);
	}
	
	@PostMapping
	public ResponseEntity<String> create(@RequestBody CarCategoryEntity body) {
		UUID id = UUID.randomUUID();
		
		if(this.isInvalidCreateUpdate(body)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Json.createObjectBuilder()
					.add("status", "Faild").add("message", "a required key is missing: [code] or [name]").build().toString());
		}
		
		// turn on status change date
		BaseUtils.turnOnStatus();
		
		String createBy = "system";
		if(body.getCreateBy() != null && !BaseUtils.isEmptyString(body.getCreateBy())) {
			createBy = body.getCreateBy();
		}
		
		return carCategoryService.create(id.toString(), body.getCode(), body.getName(), createBy);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<String> update(@PathVariable("id") String id, @RequestBody CarCategoryEntity body) {
		
		if(this.isInvalidCreateUpdate(body)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Json.createObjectBuilder()
					.add("status", "Faild").add("message", "a required key is missing: [code] or [name]").build().toString());
		}
		
		// turn on status change date
		BaseUtils.turnOnStatus();
		
		String updateBy = "system";
		if(body.getUpdateBy() != null && !BaseUtils.isEmptyString(body.getUpdateBy())) {
			updateBy = body.getUpdateBy();
		}
		return carCategoryService.update(id.toString(), body.getCode(), body.getName(), updateBy);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<String> details(@PathVariable("id") String id) {
		return carCategoryService.details(id);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") String id) {
		
		// turn on status change date
		BaseUtils.turnOnStatus();
		return carCategoryService.delete(id);
	}
	
	/**
	 * check status valid of data when create, update
	 * @param body
	 * @return true/false
	 */
	private boolean isInvalidCreateUpdate(@RequestBody CarCategoryEntity body) {
		boolean rs = false;
		if(body == null || (body != null && (body.getCode() == null || body.getCode().isEmpty())) 
				|| (body != null && (body.getName() == null || body.getName().isEmpty()))) {
			rs= true;
		}
		return rs;
	}
}
