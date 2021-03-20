package com.scalar.carapp.contract.car;

import com.scalar.base.BaseConstant;
import com.scalar.carapp.constant.CarConstant;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.database.Ledger;

import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class CarDelete extends Contract {
	
	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			if (!(argument.containsKey(CarConstant.ID))) {
				throw new ContractContextException("a required key is missing: [id]");
			}

			String uuid = argument.getString(CarConstant.ID);
			
			// get data by CarConstant.ASSET_HOLD_ID
			Optional<Asset> response = ledger.get(CarConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset does not exist");
			}

			JsonArray dataList = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();

			if (!this.isExistCar(dataList, uuid)) {
				throw new ContractContextException("Car does not exist!");
			}
			
			JsonArrayBuilder rs = Json.createArrayBuilder();
			for (int i = 0; i < dataList.size(); i++) {
				JsonObject jsonTemp = dataList.getJsonObject(i).asJsonObject();

				JsonObject category = null;
				if (jsonTemp != null && jsonTemp.getString(CarConstant.ID).toString().equals(uuid)) {
					category = this.getDataDeleted(jsonTemp, argument);
				}else if(jsonTemp != null){
					category = this.cloneJsonObject(jsonTemp);
				}
				if(category != null) {					
					rs.add(category);
				}
			}
			
			ledger.put(CarConstant.ASSET_HOLD_ID, Json.createObjectBuilder().add(BaseConstant.LIST, rs).build());
			return Json.createObjectBuilder()
					.add("status", "Succeeded")
					.add("message", "Car deleted successfully!")
					.build();
			
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}

	/**
	 * check exist of Car
	 * 
	 * @param listData
	 * @param id: ID of Car
	 * @return True/False
	 */
	private boolean isExistCar(JsonArray listData, String id) {
		boolean result = false;

		for (int i = 0; i < listData.size(); i++) {
			JsonObject jsonTemp = listData.getJsonObject(i).asJsonObject();

			if (jsonTemp != null && jsonTemp.getString(CarConstant.ID).toString().equals(id) 
					&& jsonTemp.getInt(CarConstant.IS_DELETED) != BaseConstant.IS_DELETED) {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * update Car: only allow update isDeleted status
	 * @param data
	 * @return JsonObject
	 */
	private JsonObject getDataDeleted(JsonObject data, JsonObject argument) {
		try {
			
			int isDeleted = argument.getInt(CarConstant.IS_DELETED);
			
			JsonObject json = Json.createObjectBuilder()
					// fields will update
					.add(CarConstant.IS_DELETED, isDeleted)
					
					// clone old data [not update]
					.add(CarConstant.ID, data.getString(CarConstant.ID))
					.add(CarConstant.CATEGORY_ID, data.getString(CarConstant.CATEGORY_ID))
					.add(CarConstant.NAME, data.getString(CarConstant.NAME))
					.add(CarConstant.DESCRIPTION, data.getString(CarConstant.DESCRIPTION))
					.add(CarConstant.IMAGE_URL, data.getString(CarConstant.IMAGE_URL))
					.add(CarConstant.PRICE_PER_DAY, Double.parseDouble(data.get(CarConstant.PRICE_PER_DAY).toString()))
					.add(CarConstant.RENTAL_STATUS, data.getInt(CarConstant.RENTAL_STATUS))
					
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
