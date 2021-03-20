package com.scalar.carapp.contract.car;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import com.scalar.base.BaseConstant;
import com.scalar.carapp.constant.CarConstant;
import com.scalar.carapp.constant.CarRentalConstant;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;

public class CarGetListAvailable extends Contract {

	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			if (!argument.containsKey(CarRentalConstant.FROM_DATE) || !argument.containsKey(CarRentalConstant.DUE_DATE)) {
				throw new ContractContextException("a required key is missing: [fromDate] or [dueDate]");
			}
			
			String fromDate = argument.getString(CarRentalConstant.FROM_DATE);
			String dueDate = argument.getString(CarRentalConstant.DUE_DATE);
			
			// get Asset by [CarConstant.ASSET_HOLD_ID]
			Optional<Asset> response = ledger.get(CarConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset does not exist!");
			}
			
			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			
			
			// get car rentals list
			JsonArray carRenals = this.getAllCarRentals(ledger, argument, property);
			JsonArrayBuilder rs = Json.createArrayBuilder();
			
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();

				JsonObject car = null;
				if(jsonTemp != null && jsonTemp.getInt(CarConstant.IS_DELETED) != BaseConstant.IS_DELETED) {
					car = this.cloneJsonObject(jsonTemp);
					
					if(this.isAvailableInTimes(carRenals, car.getString(CarConstant.ID), fromDate, dueDate)) {						
						rs.add(car);
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
	 * check status available of car in times
	 * @param carRentals
	 * @param carId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private boolean isAvailableInTimes(JsonArray carRentals, String carId, String startDateString, String endDateString) {
		
		boolean isAvailable = true;
		
		try {
			Date startDate = this.stringToDate(startDateString);
			Date endDate = this.stringToDate(startDateString);
			
			
			for(int i = 0; i < carRentals.size(); i++) {
				
				// return if have one item valid
				if(!isAvailable) {
					return isAvailable;
				}
				
				JsonObject carRental = carRentals.getJsonObject(i).asJsonObject();
				
				// if status is New and same carId
				if(isAvailable && carRental.getInt(CarRentalConstant.STATUS) == 0 && carRental.getString(CarRentalConstant.CAR_ID).equals(carId)) {
					
					Date startDateOfRental = this.stringToDate(carRental.getString(CarRentalConstant.FROM_DATE));
					Date dueDateOfRental = this.stringToDate(carRental.getString(CarRentalConstant.DUE_DATE));
					
					// ---- | start ----- end--|---
					// -------| from --- due-|--- 
					if(startDate.before(startDateOfRental) && endDate.after(dueDateOfRental)) {
						isAvailable = false;
					}
					// -----|--start---end---|---
					// -----|--from---due----|---
					else if(startDate.equals(startDateOfRental) || endDate.equals(dueDateOfRental)) {
						isAvailable = false;
					}
					// -----|--start---end---|---
					// -------|--from---------end (any)--
					else if(startDate.after(startDateOfRental) && (startDate.before(dueDateOfRental))) {
						isAvailable = false;
					}
					// -----|--start----end----|---
					// -start(any)-----------|-end--
					else if(endDate.after(startDateOfRental) && endDate.before(dueDateOfRental)) {
						isAvailable = false;
					}
				}
			}
			
			return isAvailable;
		} catch (ParseException e) {
			return false;
		}
	}
	
	/**
	 * Convert string with format [ApplianceConstant.DATE_FORMAT] to date
	 * @return Date
	 * @throws ParseException 
	 */
	private Date stringToDate(String dateString) throws ParseException {
		SimpleDateFormat sDf = new SimpleDateFormat(BaseConstant.DATE_FORMAT);
		Date date = sDf.parse(dateString);
		return date;
	}
	
	/**
	 * get list car rentals
	 * @param ledger
	 * @param argument
	 * @param property
	 * @return
	 */
	private JsonArray getAllCarRentals(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		Optional<Asset> carResponse = ledger.get(CarRentalConstant.ASSET_HOLD_ID);
		if (!carResponse.isPresent()) {
			throw new ContractContextException("Asset of Car Rental " + CarRentalConstant.ASSET_HOLD_ID + "does not exist!");
		}
		return carResponse.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
	}
	
	/**
	 * Clone to new object
	 * @param data
	 * @return
	 */
	public JsonObject cloneJsonObject(JsonObject data) {
		try {
			JsonObject json = Json.createObjectBuilder()
					.add(CarConstant.ID, data.getString(CarConstant.ID))
					.add(CarConstant.CATEGORY_ID, data.getString(CarConstant.CATEGORY_ID))
					.add(CarConstant.NAME, data.getString(CarConstant.NAME))
					.add(CarConstant.DESCRIPTION, data.getString(CarConstant.DESCRIPTION))
					.add(CarConstant.IMAGE_URL, data.getString(CarConstant.IMAGE_URL))
					.add(CarConstant.PRICE_PER_DAY, Double.parseDouble(data.get(CarConstant.PRICE_PER_DAY).toString()))
					.add(CarConstant.RENTAL_STATUS, data.getInt(CarConstant.RENTAL_STATUS))
					
					.add(CarConstant.CREATE_AT, data.getString(CarConstant.CREATE_AT))
					.add(CarConstant.CREATE_BY, data.getString(CarConstant.CREATE_BY))
					
					.add(CarConstant.UPDATE_AT, data.getString(CarConstant.UPDATE_AT))
					.add(CarConstant.UPDATE_BY, data.getString(CarConstant.UPDATE_BY))
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
}
