package com.scalar.carapp.contract.car.cartegory;

import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import com.scalar.base.BaseConstant;
import com.scalar.carapp.constant.CarCategoryConstant;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;

public class CarCategoryGetList extends Contract {

	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			// get Asset by [CarCategoryConstant.ASSET_HOLD_ID]
			Optional<Asset> response = ledger.get(CarCategoryConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset does not exist!");
			}
			
			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			
			JsonArrayBuilder rs = Json.createArrayBuilder();
			
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();

				JsonObject category = null;
				if(jsonTemp != null && jsonTemp.getInt(CarCategoryConstant.IS_DELETED) != BaseConstant.IS_DELETED) {
					category = this.cloneJsonObject(jsonTemp);
					rs.add(category);
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
					
					.add(CarCategoryConstant.CREATE_AT, data.getString(CarCategoryConstant.CREATE_AT))
					.add(CarCategoryConstant.CREATE_BY, data.getString(CarCategoryConstant.CREATE_BY))
					
					.add(CarCategoryConstant.UPDATE_AT, data.getString(CarCategoryConstant.UPDATE_AT))
					.add(CarCategoryConstant.UPDATE_BY, data.getString(CarCategoryConstant.UPDATE_BY))
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
}
