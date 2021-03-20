package com.scalar.carapp.contract.car.cartegory;

import com.scalar.base.BaseConstant;
import com.scalar.carapp.constant.CarCategoryConstant;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.database.Ledger;

import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class CarCategoryDelete extends Contract {
	
	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			if (!(argument.containsKey(CarCategoryConstant.ID))) {
				throw new ContractContextException("a required key is missing: [id]");
			}

			String uuid = argument.getString(CarCategoryConstant.ID);
			
			// get data by CarCategoryConstant.ASSET_HOLD_ID
			Optional<Asset> response = ledger.get(CarCategoryConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset does not exist");
			}

			JsonArray dataList = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();

			if (!this.isExistCarCatergory(dataList, uuid)) {
				throw new ContractContextException("Car Category does not exist!");
			}
			
			JsonArrayBuilder rs = Json.createArrayBuilder();
			for (int i = 0; i < dataList.size(); i++) {
				JsonObject jsonTemp = dataList.getJsonObject(i).asJsonObject();

				JsonObject category = null;
				if (jsonTemp != null && jsonTemp.getString(CarCategoryConstant.ID).toString().equals(uuid)) {
					category = this.getDataDeleted(jsonTemp, argument);
				}else if(jsonTemp != null){
					category = this.cloneJsonObject(jsonTemp);
				}
				if(category != null) {					
					rs.add(category);
				}
			}
			
			ledger.put(CarCategoryConstant.ASSET_HOLD_ID, Json.createObjectBuilder().add(BaseConstant.LIST, rs).build());
			return Json.createObjectBuilder()
					.add("status", "Succeeded")
					.add("message", "Car Category deleted successfully!")
					.build();
			
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}

	/**
	 * check exist of Car Category
	 * 
	 * @param listData
	 * @param id: ID of Car Category
	 * @return True/False
	 */
	private boolean isExistCarCatergory(JsonArray listData, String id) {
		boolean result = false;

		for (int i = 0; i < listData.size(); i++) {
			JsonObject jsonTemp = listData.getJsonObject(i).asJsonObject();

			if (jsonTemp != null && jsonTemp.getString(CarCategoryConstant.ID).toString().equals(id) 
					&& jsonTemp.getInt(CarCategoryConstant.IS_DELETED) != BaseConstant.IS_DELETED) {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * update Car-Category: only allow update isDeleted status
	 * @param data
	 * @return JsonObject
	 */
	private JsonObject getDataDeleted(JsonObject data, JsonObject argument) {
		try {
			
			int isDeleted = argument.getInt(CarCategoryConstant.IS_DELETED);
			
			JsonObject json = Json.createObjectBuilder()
					// fields will update
					.add(CarCategoryConstant.IS_DELETED, isDeleted)
					
					// clone old data [not update]
					.add(CarCategoryConstant.ID, data.getString(CarCategoryConstant.ID))
					.add(CarCategoryConstant.CODE, data.getString(CarCategoryConstant.CODE))
					.add(CarCategoryConstant.NAME, data.getString(CarCategoryConstant.NAME))
					.add(CarCategoryConstant.IS_ACTIVE, data.getInt(CarCategoryConstant.IS_ACTIVE))
					.add(CarCategoryConstant.UPDATE_AT, data.getString(CarCategoryConstant.UPDATE_AT))
					.add(CarCategoryConstant.UPDATE_BY, data.getString(CarCategoryConstant.UPDATE_BY))
					.add(CarCategoryConstant.CREATE_AT, data.getString(CarCategoryConstant.CREATE_AT))
					.add(CarCategoryConstant.CREATE_BY, data.getString(CarCategoryConstant.CREATE_BY))
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
					.add(CarCategoryConstant.ID, data.getString(CarCategoryConstant.ID))
					.add(CarCategoryConstant.CODE, data.getString(CarCategoryConstant.CODE))
					.add(CarCategoryConstant.NAME, data.getString(CarCategoryConstant.NAME))
					.add(CarCategoryConstant.IS_ACTIVE, data.getInt(CarCategoryConstant.IS_ACTIVE))
					.add(CarCategoryConstant.IS_DELETED, data.getInt(CarCategoryConstant.IS_DELETED))
					.add(CarCategoryConstant.UPDATE_AT, data.getString(CarCategoryConstant.UPDATE_AT))
					.add(CarCategoryConstant.UPDATE_BY, data.getString(CarCategoryConstant.UPDATE_BY))
					.add(CarCategoryConstant.CREATE_AT, data.getString(CarCategoryConstant.CREATE_AT))
					.add(CarCategoryConstant.CREATE_BY, data.getString(CarCategoryConstant.CREATE_BY))
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
}
