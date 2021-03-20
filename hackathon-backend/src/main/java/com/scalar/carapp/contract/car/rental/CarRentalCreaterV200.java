package com.scalar.carapp.contract.car.rental;

import com.scalar.base.BaseConstant;
import com.scalar.carapp.constant.CarConstant;
import com.scalar.carapp.constant.CarRentalConstant;
import com.scalar.carapp.constant.CustomerConstant;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.database.Ledger;

import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class CarRentalCreaterV200 extends Contract {
	
	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			if (!argument.containsKey(CarRentalConstant.CAR_ID) || !argument.containsKey(CarRentalConstant.CUSTOMER_ID) 
					|| !argument.containsKey(CarRentalConstant.FROM_DATE) || !argument.containsKey(CarRentalConstant.DUE_DATE)) {
				throw new ContractContextException("a required key is missing: [carId] or [customerId] or [fromDate] or [dueDate]");
			}

			// get car info by id
			JsonObject carInfo = this.getCarById(ledger, argument, property, argument.getString(CarRentalConstant.CAR_ID));
			if(carInfo == null) {
				throw new ContractContextException("Car with ID " + argument.getString(CarRentalConstant.CAR_ID) + " does not exist!");
			}
			
			// get customer by id
			JsonObject customer = this.getCustomerById(ledger, argument, property, argument.getString(CarRentalConstant.CUSTOMER_ID));
			if(customer == null) {
				throw new ContractContextException("Customer with ID " + argument.getString(CarRentalConstant.CUSTOMER_ID) + " does not exist!");
			}
			
			// get data of asset_id [CarRentalConstant.ASSET_HOLD_ID]
			Optional<Asset> response = ledger.get(CarRentalConstant.ASSET_HOLD_ID);
			
			if (response.isPresent()) {
				return this.putMoreItem(ledger, argument, property, response, carInfo, customer);
			} else {
				return this.addFirstItem(ledger, argument, property, carInfo, customer);
			}
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
	/**
	 * get car by Id
	 * @param ledger
	 * @param argument
	 * @param property
	 * @param codeCategory
	 * @return
	 */
	private JsonObject getCarById(Ledger ledger, JsonObject argument, Optional<JsonObject> property, String carId) {
		JsonObject car = null;
		Optional<Asset> carResponse = ledger.get(CarConstant.ASSET_HOLD_ID);
		
		if (!carResponse.isPresent()) {
			throw new ContractContextException("Asset of Car " + CarConstant.ASSET_HOLD_ID + "does not exist!");
		}
		
		JsonArray jsonArray = carResponse.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			
			JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();
			
			if(jsonTemp != null && jsonTemp.getInt(CarConstant.IS_DELETED) != BaseConstant.IS_DELETED 
					&& jsonTemp.getString(CarConstant.ID).equals(carId)) {
				car = jsonTemp;
			}
		}
		return car;
	}
	
	/**
	 * get customer by Id
	 * @param ledger
	 * @param argument
	 * @param property
	 * @param codeCategory
	 * @return
	 */
	private JsonObject getCustomerById(Ledger ledger, JsonObject argument, Optional<JsonObject> property, String customerId) {
		JsonObject customer = null;
		Optional<Asset> usResponse = ledger.get(CustomerConstant.ASSET_HOLD_ID);
		
		if (!usResponse.isPresent()) {
			throw new ContractContextException("Asset of Customer " +  CustomerConstant.ASSET_HOLD_ID + " does not exist!");
		}
		
		JsonArray jsonArray = usResponse.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			
			JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();
			
			if(jsonTemp != null && jsonTemp.getInt(CustomerConstant.IS_DELETED) != BaseConstant.IS_DELETED 
					&& jsonTemp.getString(CustomerConstant.ID).equals(customerId)) {
				customer = jsonTemp;
			}
		}
		return customer;
	}

	/**
	 * create new asset with name is [CarConstant.ASSET_HOLD_ID] and add first for car item
	 * 
	 * @param ledger
	 * @param argument
	 * @param property
	 * @return JsonObject
	 */
	private JsonObject addFirstItem(Ledger ledger, JsonObject argument, Optional<JsonObject> property,
			JsonObject car, JsonObject customer) {
		try {
			JsonArrayBuilder dataListAppliance = Json.createArrayBuilder();
			dataListAppliance.add(this.initRentalData(argument, car, customer));

			ledger.put(CarRentalConstant.ASSET_HOLD_ID, Json.createObjectBuilder().add(BaseConstant.LIST, dataListAppliance).build());

			return Json.createObjectBuilder()
					.add("status", "Succeeded")
					.add("message", "Car Rental " + argument.getString(CarRentalConstant.ID) + " created")
					.build();
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}

	}

	/**
	 * add more car rental item into object 'list' of asset [CarRentalConstant.ASSET_HOLD_ID]
	 * 
	 * @param ledger
	 * @param argument
	 * @param property
	 * @param response
	 * @return JsonObject
	 */
	private JsonObject putMoreItem(Ledger ledger, JsonObject argument, Optional<JsonObject> property,
			Optional<Asset> response, JsonObject car, JsonObject customer) {
		try {
			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			
			// add more items
			JsonArrayBuilder dataListAppliance = Json.createArrayBuilder(jsonArray);
			dataListAppliance.add(this.initRentalData(argument, car, customer));

			ledger.put(CarRentalConstant.ASSET_HOLD_ID,
					Json.createObjectBuilder().add(BaseConstant.LIST, dataListAppliance).build());

			return Json.createObjectBuilder()
					.add("status", "Succeeded")
					.add("message", "Car Rental " + argument.getString(CarConstant.ID) + " created").build();
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}

	/**
	 * Initial car rental data from argument
	 * 
	 * @param argument
	 * @return JsonObject
	 */
	private JsonObject initRentalData(JsonObject argument, JsonObject car, JsonObject customer) {
		try {
			String uuid = argument.getString(CarRentalConstant.ID);
			
			// get from argument
			String carId = argument.getString(CarRentalConstant.CAR_ID);
			String customerId = argument.getString(CarRentalConstant.CUSTOMER_ID);
			String fromDate = argument.getString(CarRentalConstant.FROM_DATE);
			String dueDate = argument.getString(CarRentalConstant.DUE_DATE);
			
			long dateDiff = Long.parseLong(argument.get(CarRentalConstant.DATE_DIFF).toString());
			
			// get from car object
			double pricePerDayOfCar = Double.parseDouble(car.get(CarConstant.PRICE_PER_DAY).toString());
			
			// get from argument
			int status = argument.getInt(CarRentalConstant.STATUS);
			String createAt = argument.getString(CarRentalConstant.CREATE_AT);
			String createBy = argument.getString(CarRentalConstant.CREATE_BY);
			String updateAt = argument.getString(CarRentalConstant.UPDATE_AT);
			String updateBy = argument.getString(CarRentalConstant.UPDATE_BY);
			
			// set data for automation calculator fields
			double basePrice = pricePerDayOfCar * dateDiff;
			double discount = 0;
			double tax = 0;
			double totalPrice = basePrice;
			
			JsonObject json = Json.createObjectBuilder()
					.add(CarRentalConstant.ID, uuid)
					.add(CarRentalConstant.CAR_ID, carId)
					.add(CarRentalConstant.CUSTOMER_ID, customerId)
					.add(CarRentalConstant.FROM_DATE, fromDate)
					.add(CarRentalConstant.DUE_DATE, dueDate)
					
					// for automation calculator fields
					.add(CarRentalConstant.BASE_PRICE, basePrice)
					.add(CarRentalConstant.DISCOUNT, discount)
					.add(CarRentalConstant.TAX, tax)
					.add(CarRentalConstant.TOTAL_PRICE, totalPrice)
					
					
					.add(CarRentalConstant.STATUS, status)
					
					.add(CarConstant.CREATE_AT, createAt)
					.add(CarConstant.CREATE_BY, createBy)
					.add(CarConstant.UPDATE_AT, updateAt)
					.add(CarConstant.UPDATE_BY, updateBy)
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
}
