package com.scalar.carapp.contract.car.rental;

import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import com.scalar.base.BaseConstant;
import com.scalar.carapp.constant.CarConstant;
import com.scalar.carapp.constant.CarRentalConstant;
import com.scalar.carapp.constant.CustomerConstant;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;

public class CarRentalGetList extends Contract {

	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			// get Asset by [CarRentalConstant.ASSET_HOLD_ID]
			Optional<Asset> response = ledger.get(CarRentalConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset " + CarRentalConstant.ASSET_HOLD_ID + " does not exist!");
			}
			
			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			
			// get cars and customers
			JsonArray cars = this.getCars(ledger, argument, property);
			JsonArray customers = this.getCustomers(ledger, argument, property);
			
			JsonArrayBuilder rs = Json.createArrayBuilder();
			
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();

				JsonObject carRental = null;
				if(jsonTemp != null) {
					carRental = this.cloneJsonObject(jsonTemp, cars, customers);
					
					if(carRental != null) {						
						rs.add(carRental);
					}
				}
			}
			
			return Json.createObjectBuilder().add("status", "Succeeded")
					.add("data", rs)
					.build();
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
	/**
	 * Get list cars
	 * @param ledger
	 * @param argument
	 * @param property
	 * @return
	 */
	private JsonArray getCars(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		Optional<Asset> carResponse = ledger.get(CarConstant.ASSET_HOLD_ID);
		
		if (!carResponse.isPresent()) {
			throw new ContractContextException("Asset of Car " + CarConstant.ASSET_HOLD_ID + "does not exist!");
		}
		
		return carResponse.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
	}
	
	/**
	 * Get list cars
	 * @param ledger
	 * @param argument
	 * @param property
	 * @return
	 */
	private JsonArray getCustomers(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		Optional<Asset> carResponse = ledger.get(CustomerConstant.ASSET_HOLD_ID);
		
		if (!carResponse.isPresent()) {
			throw new ContractContextException("Asset of Customer " + CarConstant.ASSET_HOLD_ID + "does not exist!");
		}
		
		return carResponse.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
	}
	
	
	/**
	 * filter a object by id in list
	 * @param cars
	 * @param id
	 * @return
	 */
	private JsonObject getItemById(JsonArray datas, String id) {
		JsonObject obj = null;
		for (int i = 0; i < datas.size(); i++) {
			JsonObject jsonTemp = datas.getJsonObject(i).asJsonObject();
			
			if(jsonTemp != null && jsonTemp.getString(BaseConstant.ID).toString().equals(id)) {
				obj = jsonTemp;
			}
		}
		return obj;
	}
	
	/**
	 * Clone to new object
	 * @param data
	 * @return
	 */
	public JsonObject cloneJsonObject(JsonObject data, JsonArray cars, JsonArray customers) {
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
			
			String carId = data.getString(CarRentalConstant.CAR_ID);
			String customerId = data.getString(CarRentalConstant.CUSTOMER_ID);
			
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
					
					.add(CarRentalConstant.CAR_ID, carId)
					.add(CarRentalConstant.CUSTOMER_ID, customerId)
					
					.add(CarRentalConstant.CAR, this.getItemById(cars, carId).asJsonObject())
					.add(CarRentalConstant.CUSTOMER, this.getItemById(customers, customerId).asJsonObject())
					
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
