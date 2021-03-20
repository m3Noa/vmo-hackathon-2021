package com.scalar.carapp.contract.car.rental;

import com.scalar.base.BaseConstant;
import com.scalar.carapp.constant.CarConstant;
import com.scalar.carapp.constant.CarRentalConstant;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.database.Ledger;

import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class CarRentalUpdateStatus extends Contract {
	
	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			if (!argument.containsKey(CarRentalConstant.ID)) {
				throw new ContractContextException("a required key is missing: [id]");
			}

			String uuid = argument.getString(CarRentalConstant.ID);
			
			// get data of asset_id [CarRentalConstant.ASSET_HOLD_ID]
			Optional<Asset> response = ledger.get(CarRentalConstant.ASSET_HOLD_ID);
						
			JsonArray dataList = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();

			if (!this.isExistCarRental(dataList, uuid)) {
				throw new ContractContextException("Car Rental does not exist!");
			}
			
			JsonArrayBuilder rs = Json.createArrayBuilder();
			String carId = "";
			
			for (int i = 0; i < dataList.size(); i++) {
				JsonObject jsonTemp = dataList.getJsonObject(i).asJsonObject();

				JsonObject carRental = null;
				if (jsonTemp != null && jsonTemp.getString(CarConstant.ID).toString().equals(uuid)) {
					carRental = this.getDataUpdate(jsonTemp, argument);
					
					if(carRental != null) {
						carId = jsonTemp.getString(CarRentalConstant.CAR_ID);
					}
				}else if(jsonTemp != null){
					carRental = this.cloneJsonObject(jsonTemp);
				}
				if(carRental != null) {					
					rs.add(carRental);
				}
			}
			
			int status = argument.getInt(CarRentalConstant.STATUS);
			if((status == 1 || status == 2) && !carId.isEmpty()) {				
				this.updateCarRentalStatus(ledger, carId, status);
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
	 * update Car Rental: only allow status, updateAt, updateBy
	 * @param data
	 * @return JsonObject
	 */
	private JsonObject getDataUpdate(JsonObject data, JsonObject argument) {
		try {
			int status = argument.getInt(CarRentalConstant.STATUS);
			
			// set data for automation calculator fields
			double basePrice = Double.parseDouble(data.get(CarRentalConstant.BASE_PRICE).toString());
			double discount = Double.parseDouble(data.get(CarRentalConstant.DISCOUNT).toString());
			double tax = Double.parseDouble(data.get(CarRentalConstant.TAX).toString());
			double totalPrice = Double.parseDouble(data.get(CarRentalConstant.TOTAL_PRICE).toString());
			
			String updateAt = argument.getString(CarRentalConstant.UPDATE_AT);
			String updateBy = argument.getString(CarRentalConstant.UPDATE_BY);
			
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
					
					.add(CarRentalConstant.STATUS, status)
					
					.add(CarRentalConstant.CREATE_AT, data.getString(CarRentalConstant.CREATE_AT))
					.add(CarRentalConstant.CREATE_BY, data.getString(CarRentalConstant.CREATE_BY))
					.add(CarRentalConstant.UPDATE_AT, updateAt)
					.add(CarRentalConstant.UPDATE_BY, updateBy)
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
	
	
	// ----------------------------------------
	// FOR UPDATE RENTAL STATUS OF CAR
	// ----------------------------------------
	
	/**
	 * update car rental status
	 * @param ledger
	 * @param argument
	 * @param property
	 */
	private void updateCarRentalStatus(Ledger ledger, String carId, int statusRental) {
		try {
			String carUUID = carId;
			
			// get data by CarConstant.ASSET_HOLD_ID
			Optional<Asset> responseCar = ledger.get(CarConstant.ASSET_HOLD_ID);
			if (!responseCar.isPresent()) {
				throw new ContractContextException("Asset " + CarConstant.ASSET_HOLD_ID + " does not exist");
			}

			JsonArray dataListCarFn = responseCar.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			int carStatus = 0;
			if(statusRental == 1) {
				carStatus = CarConstant.RENTED;
			}
			
			JsonArrayBuilder rsCar = Json.createArrayBuilder();
			for (int i = 0; i < dataListCarFn.size(); i++) {
				JsonObject jsonTempCar = dataListCarFn.getJsonObject(i).asJsonObject();

				JsonObject carObject = null;
				if (jsonTempCar != null && jsonTempCar.getString(CarConstant.ID).toString().equals(carUUID)) {
					carObject = this.getRentalStatusCar(jsonTempCar, carStatus);
				}else if(jsonTempCar != null){
					carObject = this.cloneCarJsonObject(jsonTempCar);
				}
				if(carObject != null) {					
					rsCar.add(carObject);
				}
			}
			
			ledger.put(CarConstant.ASSET_HOLD_ID, Json.createObjectBuilder().add(BaseConstant.LIST, rsCar).build());
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
		
	}
	
	/**
	 * update Car: only allow update rental status
	 * @param data
	 * @return JsonObject
	 */
	private JsonObject getRentalStatusCar(JsonObject data, int status) {
		try {
			JsonObject json = Json.createObjectBuilder()
					// fields will update
					.add(CarConstant.RENTAL_STATUS, status)
					
					// clone old data [not update]
					.add(CarConstant.ID, data.getString(CarConstant.ID))
					.add(CarConstant.CATEGORY_ID, data.getString(CarConstant.CATEGORY_ID))
					.add(CarConstant.NAME, data.getString(CarConstant.NAME))
					.add(CarConstant.DESCRIPTION, data.getString(CarConstant.DESCRIPTION))
					.add(CarConstant.IMAGE_URL, data.getString(CarConstant.IMAGE_URL))
					.add(CarConstant.PRICE_PER_DAY, Double.parseDouble(data.get(CarConstant.PRICE_PER_DAY).toString()))
					
					.add(CarConstant.IS_DELETED, data.getInt(CarConstant.IS_DELETED))
					.add(CarConstant.IS_ACTIVE, data.getInt(CarConstant.IS_ACTIVE))
					.add(CarConstant.UPDATE_AT, data.getString(CarConstant.UPDATE_AT))
					
					.add(CarConstant.UPDATE_BY, data.getString(CarConstant.UPDATE_BY))
					.add(CarConstant.CREATE_AT, data.getString(CarConstant.CREATE_AT))
					.add(CarConstant.CREATE_BY, data.getString(CarConstant.CREATE_BY))
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
	public JsonObject cloneCarJsonObject(JsonObject data) {
		try {
			JsonObject json = Json.createObjectBuilder()
					.add(CarConstant.ID, data.getString(CarConstant.ID))
					
					.add(CarConstant.CATEGORY_ID, data.getString(CarConstant.CATEGORY_ID))
					.add(CarConstant.NAME, data.getString(CarConstant.NAME))
					.add(CarConstant.DESCRIPTION, data.getString(CarConstant.DESCRIPTION))
					.add(CarConstant.IMAGE_URL, data.getString(CarConstant.IMAGE_URL))
					.add(CarConstant.PRICE_PER_DAY, Double.parseDouble(data.get(CarConstant.PRICE_PER_DAY).toString()))
					.add(CarConstant.RENTAL_STATUS, data.getInt(CarConstant.RENTAL_STATUS))
					
					.add(CarConstant.IS_DELETED, data.getInt(CarConstant.IS_DELETED))
					.add(CarConstant.IS_ACTIVE, data.getInt(CarConstant.IS_ACTIVE))
					
					.add(CarConstant.UPDATE_AT, data.getString(CarConstant.UPDATE_AT))
					.add(CarConstant.UPDATE_BY, data.getString(CarConstant.UPDATE_BY))
					.add(CarConstant.CREATE_AT, data.getString(CarConstant.CREATE_AT))
					.add(CarConstant.CREATE_BY, data.getString(CarConstant.CREATE_BY))
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
}
