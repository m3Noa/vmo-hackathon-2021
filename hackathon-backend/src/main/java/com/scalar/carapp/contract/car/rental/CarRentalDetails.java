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
import javax.json.JsonObject;

public class CarRentalDetails extends Contract {
	
	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {

		try {
			// validate APIs parameters
			if (!argument.containsKey(CarRentalConstant.ID)) {
				throw new ContractContextException("a required key is missing: [id]");
			}
			String uuid = argument.getString(CarRentalConstant.ID);

			// get data by CarRentalConstant.ASSET_HOLD_ID
			Optional<Asset> response = ledger.get(CarRentalConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset " + CarRentalConstant.ASSET_HOLD_ID + " does not exist!");
			}
			
			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			JsonObject jsonResult = null;
			JsonObject carDetails = null;
			JsonObject customerDetails = null;
			
			// search details car renal in list data legger [CarRentalConstant.ASSET_HOLD_ID]
			for (int i = 0; i < jsonArray.size(); i++) {
				
				JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();
				
				if(jsonTemp != null && jsonTemp.getString(CarRentalConstant.ID).equals(uuid)) {
					String carId = jsonTemp.getString(CarRentalConstant.CAR_ID);
					String customerId = jsonTemp.getString(CarRentalConstant.CUSTOMER_ID);
					
					carDetails = this.getCarById(ledger, argument, property, carId);
					customerDetails = this.getCustomerById(ledger, argument, property, customerId);
					
					jsonResult = this.initCarRentalDetails(jsonTemp, carDetails, customerDetails);
				}
			}

			// check exist car rental or not
			if (jsonResult == null) {
				throw new ContractContextException("Car Rental does not exist!");
			}
			
			return Json.createObjectBuilder().add("status", "Succeeded")
					.add("data", jsonResult)
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
	 * 
	 * @param argument
	 * @return
	 */
	private JsonObject initCarRentalDetails(JsonObject data, JsonObject car, JsonObject customer) {
		try {
			String uuid = data.getString(CarRentalConstant.ID);
			
			String fromDate = data.getString(CarRentalConstant.FROM_DATE);
			String dueDate = data.getString(CarRentalConstant.DUE_DATE);
			
			double basePrice = Double.parseDouble(data.get(CarRentalConstant.BASE_PRICE).toString());
			double discount = Double.parseDouble(data.get(CarRentalConstant.DISCOUNT).toString());
			double tax = Double.parseDouble(data.get(CarRentalConstant.TAX).toString());
			double totalPrice = Double.parseDouble(data.get(CarRentalConstant.TOTAL_PRICE).toString());
			
			int status = data.getInt(CarRentalConstant.STATUS);
			String statusName = "";
			
			switch (status) {
				case 0:
					statusName = "New";
					break;
				case 1:
					statusName = "Started";
					break;
				case 2:
					statusName = "Returned";
					break;
				case 3:
					statusName = "Cancelled";
					break;
			}
			
			JsonObject json = Json.createObjectBuilder()
					.add(CarRentalConstant.ID, uuid)
					
					.add(CarRentalConstant.CAR, car.asJsonObject())
					.add(CarRentalConstant.CUSTOMER, customer.asJsonObject())
					
					.add(CarRentalConstant.FROM_DATE, fromDate)
					.add(CarRentalConstant.DUE_DATE, dueDate)
					
					.add(CarRentalConstant.BASE_PRICE, basePrice)
					.add(CarRentalConstant.DISCOUNT, discount)
					.add(CarRentalConstant.TAX, tax)
					.add(CarRentalConstant.TOTAL_PRICE, totalPrice)
					
					.add(CarRentalConstant.STATUS, status)
					.add(CarRentalConstant.STATUS_NAME, statusName)
					
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
