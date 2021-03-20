package com.scalar.carapp.service;

import com.scalar.base.BaseConstant;
import com.scalar.base.BaseService;
import com.scalar.base.utils.BaseUtils;
import com.scalar.carapp.constant.CarConstant;
import com.scalar.carapp.constant.CarRentalConstant;
import com.scalar.carapp.repository.CarRepository;
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
public class CarService extends BaseService{

	@Autowired
	private CarRepository carRepository;

	@Cacheable(BaseConstant.CACHE_CAR)
	public ResponseEntity<String> list(String keywords, String status) {
		try {
			String keywordSearch = keywords != null ? keywords : "";
			String statusSearch = status != null ? status : "";
			
			JsonObject argument = Json.createObjectBuilder()
					.add(BaseConstant.KEYWORDS, keywordSearch)
					.add(BaseConstant.STATUS, statusSearch)
					.build();
			ThrowableFunction f = a -> carRepository.list(a);
			return serve(f, argument);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	public ResponseEntity<String> listAvailableCars(String starDate, String dueDate) {
		try {
			JsonObject argument = Json.createObjectBuilder()
					.add(CarRentalConstant.FROM_DATE, starDate)
					.add(CarRentalConstant.DUE_DATE, dueDate)
					.build();
			ThrowableFunction f = a -> carRepository.listAvailableCars(a);
			return serve(f, argument);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@Caching(evict = {
		    @CacheEvict(value = BaseConstant.CACHE_CAR, allEntries = true)
	})
	public ResponseEntity<String> create(String id, String categoryCode, String name, String description, String imageUrl, double pricePerDay,
			String createBy) {
		int isDeleted = BaseConstant.IS_NOT_DELETED;
		int isActive = BaseConstant.IS_ACTIVED;
		int rentalStatus = CarConstant.NOT_RENTAL;
		
		String createAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);
		String updateAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);
		String updateBy = "system";
		
		JsonObject argument = Json.createObjectBuilder()
				.add(CarConstant.ID, id)
				.add(CarConstant.CATEGORY_CODE, categoryCode)
				.add(CarConstant.NAME, name)
				.add(CarConstant.DESCRIPTION, description)
				.add(CarConstant.IMAGE_URL, imageUrl)
				.add(CarConstant.PRICE_PER_DAY, String.valueOf(pricePerDay))
				.add(CarConstant.RENTAL_STATUS, rentalStatus)
				
				.add(CarConstant.IS_DELETED, isDeleted)
				.add(CarConstant.IS_ACTIVE, isActive)
				
				.add(CarConstant.CREATE_AT, createAt)
				.add(CarConstant.CREATE_BY, createBy)
				.add(CarConstant.UPDATE_AT, updateAt)
				.add(CarConstant.UPDATE_BY, updateBy)
				.build();
		ThrowableFunction f = a -> carRepository.create(a);
		return serve(f, argument);
	}
	
	
	@Caching(evict = {
			@CacheEvict(value = BaseConstant.CACHE_CAR, allEntries = true),
		    @CacheEvict(value = BaseConstant.CACHE_CAR_DETAIL, allEntries = true)
	})
	public ResponseEntity<String> update(String id, String categoryCode, String name, String description, String imageUrl, double pricePerDay,
			String updateBy) {
		
		String updateAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);
		
		JsonObject argument = Json.createObjectBuilder()
				.add(CarConstant.ID, id)
				.add(CarConstant.CATEGORY_CODE, categoryCode)
				.add(CarConstant.NAME, name)
				.add(CarConstant.DESCRIPTION, description)
				.add(CarConstant.IMAGE_URL, imageUrl)
				.add(CarConstant.PRICE_PER_DAY, pricePerDay)
				
				.add(CarConstant.UPDATE_AT, updateAt)
				.add(CarConstant.UPDATE_BY, updateBy)
				.build();
		ThrowableFunction f = a -> carRepository.update(a);
		return serve(f, argument);
	}

	@Cacheable(BaseConstant.CACHE_CAR_DETAIL)
	public ResponseEntity<String> details(String id) {
		JsonObjectBuilder argumentBuilder = Json.createObjectBuilder()
				.add(CarConstant.ID, id);
		ThrowableFunction f = a -> carRepository.details(a);
		return serve(f, argumentBuilder.build());
	}
	
	@Caching(evict = {
			@CacheEvict(value = BaseConstant.CACHE_CAR, allEntries = true),
		    @CacheEvict(value = BaseConstant.CACHE_CAR_DETAIL, allEntries = true)
	})
	public ResponseEntity<String> delete(String id) {
		int isDelete = BaseConstant.IS_DELETED;
		
		JsonObjectBuilder argumentBuilder = Json.createObjectBuilder()
				.add(CarConstant.ID, id)
				.add(CarConstant.IS_DELETED, isDelete);
		ThrowableFunction f = a -> carRepository.delete(a);
		return serve(f, argumentBuilder.build());
	}
}
