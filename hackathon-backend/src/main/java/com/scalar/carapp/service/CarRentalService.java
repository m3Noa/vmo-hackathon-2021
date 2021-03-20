package com.scalar.carapp.service;

import com.scalar.base.BaseConstant;
import com.scalar.base.BaseService;
import com.scalar.base.utils.BaseUtils;
import com.scalar.carapp.constant.CarRentalConstant;
import com.scalar.carapp.repository.CarRentalRepository;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CarRentalService extends BaseService {

	@Autowired
	private CarRentalRepository carRentalRepository;

	@Cacheable(BaseConstant.CACHE_CAR_RENTAL)
	public ResponseEntity<String> list(String code, String status) {
		try {
			String codeSearch = code != null ? code : "";
			String statusSearch = status != null ? status : "all";

			JsonObject argument = Json.createObjectBuilder()
					.add(BaseConstant.CODE, codeSearch)
					.add(BaseConstant.STATUS, statusSearch)
					.build();
			ThrowableFunction f = a -> carRentalRepository.list(a);
			return serve(f, argument);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@CacheEvict(value = BaseConstant.CACHE_CAR_RENTAL, allEntries = true)
	public ResponseEntity<String> create(String id, String carId, String customerId, String fromDate, String dueDate, long dateDiff, String createBy) {
		// default is new: 0
		int status = CarRentalConstant.STATUS_VALUE[0];

		String createAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);
		String updateAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);
		String updateBy = "system";

		JsonObject argument = Json.createObjectBuilder()
				.add(CarRentalConstant.ID, id)
				
				.add(CarRentalConstant.CAR_ID, carId)
				.add(CarRentalConstant.CUSTOMER_ID, customerId)
				
				.add(CarRentalConstant.FROM_DATE, fromDate)
				.add(CarRentalConstant.DUE_DATE, dueDate)
				.add(CarRentalConstant.DATE_DIFF, dateDiff)
				
				.add(CarRentalConstant.STATUS, status)

				.add(CarRentalConstant.CREATE_AT, createAt)
				.add(CarRentalConstant.CREATE_BY, createBy)
				.add(CarRentalConstant.UPDATE_AT, updateAt)
				.add(CarRentalConstant.UPDATE_BY, updateBy)
				.build();
		ThrowableFunction f = a -> carRentalRepository.create(a);
		return serve(f, argument);
	}

	@Caching(evict = {
		    @CacheEvict(value = BaseConstant.CACHE_CAR_RENTAL, allEntries = true),
		    @CacheEvict(value = BaseConstant.CACHE_CAR_RENTAL_DETAILS, allEntries = true)
	})
	public ResponseEntity<String> update(String id, String carId, String customerId, String fromDate, String dueDate, long dateDiff, 
			double discount, double tax, int status, String updateBy) {

		String updateAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);

		JsonObjectBuilder argument = Json.createObjectBuilder()
				.add(CarRentalConstant.ID, id)
				
				.add(CarRentalConstant.DATE_DIFF, dateDiff)

				.add(CarRentalConstant.CAR_ID, carId)
				.add(CarRentalConstant.CUSTOMER_ID, customerId)

				.add(CarRentalConstant.FROM_DATE, fromDate)
				.add(CarRentalConstant.DUE_DATE, dueDate)

				.add(CarRentalConstant.UPDATE_AT, updateBy)
				.add(CarRentalConstant.UPDATE_BY, updateAt);
		
		if(status != -1) {
			argument.add(CarRentalConstant.STATUS, status);
		}
		if(discount > 0) {
			argument.add(CarRentalConstant.DISCOUNT, discount);
		}
		if(tax > 0) {
			argument.add(CarRentalConstant.TAX, tax);
		}
		
		JsonObject body = argument.build();
		ThrowableFunction f = a -> carRentalRepository.update(a);
		return serve(f, body);
	}

	@Cacheable(BaseConstant.CACHE_CAR_RENTAL_DETAILS)
	public ResponseEntity<String> details(String id) {
		JsonObjectBuilder argumentBuilder = Json.createObjectBuilder()
				.add(CarRentalConstant.ID, id);
		ThrowableFunction f = a -> carRentalRepository.details(a);
		return serve(f, argumentBuilder.build());
	}

	@Caching(evict = {
		    @CacheEvict(value = BaseConstant.CACHE_CAR_RENTAL, allEntries = true),
		    @CacheEvict(value = BaseConstant.CACHE_CAR_RENTAL_DETAILS, allEntries = true)
	})
	public ResponseEntity<String> updateStatusCarRental(String id, int status, String updateBy) {
		String updateAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);

		JsonObject argument = Json.createObjectBuilder()
				.add(CarRentalConstant.ID, id)

				.add(CarRentalConstant.STATUS, status)
				.add(CarRentalConstant.UPDATE_AT, updateBy)
				.add(CarRentalConstant.UPDATE_BY, updateAt)
				
				.build();
		ThrowableFunction f = a -> carRentalRepository.updateStatusCarRental(a);
		return serve(f, argument);
	}

}
