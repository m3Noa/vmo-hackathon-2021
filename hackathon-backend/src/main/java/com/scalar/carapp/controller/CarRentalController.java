package com.scalar.carapp.controller;

import com.scalar.base.BaseConstant;
import com.scalar.base.model.CarRentalEntity;
import com.scalar.base.utils.BaseUtils;
import com.scalar.carapp.constant.CarRentalConstant;
import com.scalar.carapp.service.CarRentalService;
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
@RequestMapping("/v1/api/car-rental")
public class CarRentalController {

	@Autowired
	private CarRentalService carRentalService;

	@GetMapping
	public ResponseEntity<String> list(@RequestParam(value = "keywords", required = false) String code,
			@RequestParam(value = "status", required = false) String status) {
		return carRentalService.list(code, status);
	}

	@PostMapping
	public ResponseEntity<String> create(@RequestBody CarRentalEntity body) {
		UUID id = UUID.randomUUID();

		if (this.isValidCreateData(body) || body.getFromDate() == null || body.getDueDate() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Json.createObjectBuilder().add("status", "Faild")
					.add("message", "a required key is missing: [carId] or [customerId] or [fromDate] or [dueDate]")
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

		// turn on status change date
		BaseUtils.turnOnStatus();
		
		String createBy = "system";
		if(body.getCreateBy() != null && !BaseUtils.isEmptyString(body.getCreateBy())) {
			createBy = body.getCreateBy();
		}
		return carRentalService.create(id.toString(), body.getCarId(), body.getCustomerId(), fromDateString,
				dueDateString, dateDiff, createBy);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<String> update(@PathVariable("id") String id, @RequestBody CarRentalEntity body) {
		if (this.isValidCreateData(body) || body.getFromDate() == null || body.getDueDate() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Json.createObjectBuilder()
					.add("status", "Faild")
					.add("message", "a required key is missing: [carId] or [customerId] or [fromDate] or [dueDate]")
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

		double discount = body.getDiscount() > 0 ? body.getDiscount() : 0;
		double tax = body.getTax() > 0 ? body.getTax() : 0;
		int status = body.getStatus() != -1 ? body.getStatus() : -1;
		// turn on status change date
		BaseUtils.turnOnStatus();
		
		String updateBy = "system";
		if(body.getUpdateBy() != null && !BaseUtils.isEmptyString(body.getUpdateBy())) {
			updateBy = body.getUpdateBy();
		}
		return carRentalService.update(id.toString(), body.getCarId(), body.getCustomerId(), fromDateString,
				dueDateString, dateDiff, discount, tax, status, updateBy);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<String> details(@PathVariable("id") String id) {
		return carRentalService.details(id);
	}

	@PutMapping("/{id}/cancel")
	public ResponseEntity<String> cancelRental(@PathVariable("id") String id, @RequestBody(required = false) CarRentalEntity body) {
		// set value of status = 3
		int status = CarRentalConstant.STATUS_VALUE[3];
		
		// turn on status change date
		BaseUtils.turnOnStatus();
		
		String updateBy = "system";
		if(body != null && body.getUpdateBy() != null && !BaseUtils.isEmptyString(body.getUpdateBy())) {
			updateBy = body.getUpdateBy();
		}
		return carRentalService.updateStatusCarRental(id, status, updateBy);
	}
	
	@PutMapping("/{id}/start")
	public ResponseEntity<String> startRental(@PathVariable("id") String id, @RequestBody(required = false) CarRentalEntity body) {
		// set value of status = 1
		int status = CarRentalConstant.STATUS_VALUE[1];
				
		// turn on status change date
		BaseUtils.turnOnStatus();
		
		String updateBy = "system";
		if(body != null && body.getUpdateBy() != null && !BaseUtils.isEmptyString(body.getUpdateBy())) {
			updateBy = body.getUpdateBy();
		}
		return carRentalService.updateStatusCarRental(id, status, updateBy);
	}

	@PutMapping("/{id}/return")
	public ResponseEntity<String> returnRental(@PathVariable("id") String id, @RequestBody(required = false) CarRentalEntity body) {
		// set value of status = 2
		int status = CarRentalConstant.STATUS_VALUE[2];
				
		// turn on status change date
		BaseUtils.turnOnStatus();
		
		String updateBy = "system";
		if(body != null && body.getUpdateBy() != null && !BaseUtils.isEmptyString(body.getUpdateBy())) {
			updateBy = body.getUpdateBy();
		}
		return carRentalService.updateStatusCarRental(id, status, updateBy);
	}

	/**
	 * Validate status valid of data when create car
	 * 
	 * @param body
	 * @return
	 */
	private boolean isValidCreateData(CarRentalEntity body) {

		boolean rs = false;

		if (body == null) {
			rs = true;
		} else if (body.getCarId() == null || body.getCarId().isEmpty()) {
			rs = true;
		} else if (body.getCustomerId() == null || body.getCustomerId().isEmpty()) {
			rs = true;
		}
		return rs;
	}
}
