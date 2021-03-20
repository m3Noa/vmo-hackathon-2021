package com.scalar.carapp.controller;

import com.scalar.base.BaseConstant;
import com.scalar.base.model.CarEntity;
import com.scalar.base.model.CarRentalEntity;
import com.scalar.base.utils.BaseUtils;
import com.scalar.carapp.service.CarService;
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
@RequestMapping("/v1/api/car")
public class CarController {

	@Autowired
	private CarService carService;

	@GetMapping
	public ResponseEntity<String> list(@RequestParam(value="keywords", required = false) String keywords,
			@RequestParam(value="status", required = false) String status) {
		return carService.list(keywords, status);
	}
	
	@PostMapping("/available")
	public ResponseEntity<String> getAvailable(@RequestBody CarRentalEntity body) {
		if(body.getFromDate() == null || body.getDueDate() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Json.createObjectBuilder()
					.add("status", "Faild")
					.add("message", "a required key is missing: [fromDate] or [dueDate]")
					.build().toString());
		}
		
		String fromDateString = BaseUtils.dateToStringFormat(body.getFromDate(), BaseConstant.DATE_FORMAT);
		String dueDateString = BaseUtils.dateToStringFormat(body.getDueDate(), BaseConstant.DATE_FORMAT);
		long dateDiff = BaseUtils.caculatorDateDiff(fromDateString, dueDateString);
		
		if(dateDiff < 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Json.createObjectBuilder()
					.add("status", "Faild")
					.add("message", "[fromDate] cannot be greater than to [dueDate]")
					.build().toString());
		}
		
		return carService.listAvailableCars(fromDateString, dueDateString);
	}
	
	@PostMapping
	public ResponseEntity<String> create(@RequestBody CarEntity body) {
		UUID id = UUID.randomUUID();
		
		if(this.isValidCreateData(body)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Json.createObjectBuilder()
					.add("status", "Faild")
					.add("message", "a required key is missing: [categoryCode] or [name] or [description] or [imageUrl] or [pricePerDay]")
					.build().toString());
		}
		
		// turn on status change date
		BaseUtils.turnOnStatus();
		
		String createBy = "system";
		if(body.getCreateBy() != null && !BaseUtils.isEmptyString(body.getCreateBy())) {
			createBy = body.getCreateBy();
		}
		return carService.create(id.toString(), body.getCategoryCode(), body.getName(), body.getDescription(), 
				body.getImageUrl(), body.getPricePerDay(), createBy);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<String> update(@PathVariable("id") String id, @RequestBody CarEntity body) {
		if(this.isValidCreateData(body)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Json.createObjectBuilder()
					.add("status", "Faild")
					.add("message", "a required key is missing: [categoryCode] or [name] or [description] or [imageUrl] or [pricePerDay]")
					.build().toString());
		}
		
		// turn on status change date
		BaseUtils.turnOnStatus();
		
		String updateBy = "system";
		if(body.getUpdateBy() != null && !BaseUtils.isEmptyString(body.getUpdateBy())) {
			updateBy = body.getUpdateBy();
		}
		return carService.update(id.toString(), body.getCategoryCode(), body.getName(), body.getDescription(), 
				body.getImageUrl(), body.getPricePerDay(), updateBy);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<String> details(@PathVariable("id") String id) {
		return carService.details(id);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") String id) {
		
		// turn on status change date
		BaseUtils.turnOnStatus();
		return carService.delete(id);
	}
	
	/**
	 * Validate status valid of data when create car
	 * @param body
	 * @return
	 */
	private boolean isValidCreateData(CarEntity body) {
		
		boolean rs = false;
		
		if(body == null) {
			rs = true;
		}
		else if(body.getCategoryCode() == null || body.getCategoryCode().isEmpty()) {
			rs = true;
		}
		else if(body.getName() == null || body.getName().isEmpty()) {
			rs = true;
		}
		else if(body.getDescription() == null || body.getDescription().isEmpty()) {
			rs = true;
		}
		else if(body.getImageUrl() == null || body.getImageUrl().isEmpty()) {
			rs = true;
		}
		else if(body.getPricePerDay() == 0) {
			rs = true;
		}
		return rs;
	}
}
