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

public class CarRentalUpdater extends Contract {
	
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
			String uuid = argument.getString(CarRentalConstant.ID);
			Optional<Asset> response = ledger.get(CarRentalConstant.ASSET_HOLD_ID);
						
			JsonArray dataList = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();

			if (!this.isExistCarRental(dataList, uuid)) {
				throw new ContractContextException("Car Rental does not exist!");
			}
			
			JsonArrayBuilder rs = Json.createArrayBuilder();
			for (int i = 0; i < dataList.size(); i++) {
				JsonObject jsonTemp = dataList.getJsonObject(i).asJsonObject();

				JsonObject carRental = null;
				if (jsonTemp != null && jsonTemp.getString(CarConstant.ID).toString().equals(uuid)) {
					carRental = this.getDataUpdate(jsonTemp, argument, carInfo);
				}else if(jsonTemp != null){
					carRental = this.cloneJsonObject(jsonTemp);
				}
				if(carRental != null) {					
					rs.add(carRental);
				}
			}
			
			ledger.put(CarRentalConstant.ASSET_HOLD_ID, Json.createObjectBuilder().add(BaseConstant.LIST, rs).build());
			return Json.createObjectBuilder()
					.add("status", "Succeeded")
					.add("message", "Car rental updated successfully!")
					.build();
			
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
	/**
	 * get car by Id
	 * @param ledger
	 * @param argument
	 * @param property
	 * @param carId
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
	 * @param customerId
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
	 * check exist of Car Rental
	 * 
	 * @param listData
	 * @param id: ID of Car
	 * @return True/False
	 */
	private boolean isExistCarRental(JsonArray listData, String id) {
		boolean result = false;

		for (int i = 0; i < listData.size(); i++) {
			JsonObject jsonTemp = listData.getJsonObject(i).asJsonObject();

			if (jsonTemp != null && jsonTemp.getString(CarRentalConstant.ID).toString().equals(id)) {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * update Car Rental: only allow update carId, customerId, fromDate, dueDate
	 * Price info automation calculator
	 *  @param data
	 * @return JsonObject
	 */
	private JsonObject getDataUpdate(JsonObject data, JsonObject argument, JsonObject car) {
		try {
			String uuid = data.getString(CarRentalConstant.ID);
			
			// get from argument
			String carId = argument.getString(CarRentalConstant.CAR_ID);
			String customerId = argument.getString(CarRentalConstant.CUSTOMER_ID);
			String fromDate = argument.getString(CarRentalConstant.FROM_DATE);
			String dueDate = argument.getString(CarRentalConstant.DUE_DATE);
			
			long dateDiff = Long.parseLong(argument.get(CarRentalConstant.DATE_DIFF).toString());
			
			// get from car object
			double pricePerDayOfCar = Double.parseDouble(car.get(CarConstant.PRICE_PER_DAY).toString());
			
			// get from argument
			int status = data.getInt(CarRentalConstant.STATUS);
			if(argument.containsKey(CarRentalConstant.STATUS)) {
				status = argument.getInt(CarRentalConstant.STATUS);
			}
			
			String createAt = data.getString(CarRentalConstant.CREATE_AT);
			String createBy = data.getString(CarRentalConstant.CREATE_AT);
			
			String updateAt = argument.getString(CarRentalConstant.UPDATE_AT);
			String updateBy = argument.getString(CarRentalConstant.UPDATE_BY);
			
			// set data for automation calculator fields
			double basePrice = pricePerDayOfCar * dateDiff;
			double discount = 0;
			double tax = 0;
			
			if(argument.containsKey(CarRentalConstant.DISCOUNT)) {
				discount = Double.parseDouble(argument.get(CarRentalConstant.DISCOUNT).toString());
			}
			if(argument.containsKey(CarRentalConstant.TAX)) {
				tax = Double.parseDouble(argument.get(CarRentalConstant.TAX).toString());
			}
			
			double totalPrice = basePrice;
			if(discount > 0 || tax > 0) {
				totalPrice = basePrice - (discount + tax);
			}
			
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
	
	/**
	 * Clone to new object
	 * @param data
	 * @return
	 */
	public JsonObject cloneJsonObject(JsonObject data) {
		try {
			double basePrice = Double.parseDouble(data.get(CarRentalConstant.BASE_PRICE).toString());
			double discount = Double.parseDouble(data.get(CarRentalConstant.DISCOUNT).toString());
			double tax = Double.parseDouble(data.get(CarRentalConstant.TAX).toString());
			double totalPrice = Double.parseDouble(data.get(CarRentalConstant.TOTAL_PRICE).toString());
			
			JsonObject json = Json.createObjectBuilder()
					.add(CarRentalConstant.ID, data.getString(CarRentalConstant.ID))
					
					.add(CarRentalConstant.CAR_ID, data.getString(CarRentalConstant.CAR_ID))
					.add(CarRentalConstant.CUSTOMER_ID, data.getString(CarRentalConstant.CUSTOMER_ID))
					
					.add(CarRentalConstant.FROM_DATE, data.getString(CarRentalConstant.FROM_DATE))
					.add(CarRentalConstant.DUE_DATE, data.getString(CarRentalConstant.DUE_DATE))
					
					.add(CarRentalConstant.BASE_PRICE, basePrice)
					.add(CarRentalConstant.DISCOUNT, discount)
					.add(CarRentalConstant.TAX, tax)
					.add(CarRentalConstant.TOTAL_PRICE, totalPrice)
					
					.add(CarRentalConstant.STATUS, data.getInt(CarRentalConstant.STATUS))
					
					.add(CarRentalConstant.CREATE_AT, data.getString(CarRentalConstant.CREATE_AT))
					.add(CarRentalConstant.CREATE_BY, data.getString(CarRentalConstant.CREATE_BY))
					.add(CarRentalConstant.UPDATE_AT, data.getString(CarRentalConstant.UPDATE_AT))
					.add(CarRentalConstant.UPDATE_BY, data.getString(CarRentalConstant.UPDATE_BY))
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
}
