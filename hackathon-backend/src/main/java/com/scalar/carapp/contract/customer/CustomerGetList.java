package com.scalar.carapp.contract.customer;

import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import com.scalar.base.BaseConstant;
import com.scalar.carapp.constant.CustomerConstant;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;

public class CustomerGetList extends Contract {

	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			// get Asset by [CustomerConstant.ASSET_HOLD_ID]
			Optional<Asset> response = ledger.get(CustomerConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset does not exist!");
			}
			
			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			
			JsonArrayBuilder rs = Json.createArrayBuilder();
			
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();

				JsonObject category = null;
				if(jsonTemp != null && jsonTemp.getInt(CustomerConstant.IS_DELETED) != BaseConstant.IS_DELETED && 
						jsonTemp.getInt(CustomerConstant.IS_ACTIVE) == BaseConstant.IS_ACTIVED) {
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
			
			String idTempJson = "";
			String firstNameTempJson = "";
			String lastNameTempJson = "";
			String addressTempJson = "";
			String mobileTempJson = "";
			int isActiveStatusTempJson = 1;
			
			if(data.containsKey(CustomerConstant.ID)) {
				idTempJson = data.getString(CustomerConstant.ID);
			}
			if(data.containsKey(CustomerConstant.FIRST_NAME)) {
				firstNameTempJson = data.getString(CustomerConstant.FIRST_NAME);
			}
			if(data.containsKey(CustomerConstant.LAST_NAME)) {
				lastNameTempJson = data.getString(CustomerConstant.LAST_NAME);
			}
			if(data.containsKey(CustomerConstant.ADDRESS)) {
				addressTempJson = data.getString(CustomerConstant.ADDRESS);
			}
			if(data.containsKey(CustomerConstant.MOBILE_PHONE)) {
				mobileTempJson = data.getString(CustomerConstant.MOBILE_PHONE);
			}
			
			if(data.containsKey(CustomerConstant.IS_ACTIVE)) {
				isActiveStatusTempJson = data.getInt(CustomerConstant.IS_ACTIVE);
			}
			
			JsonObject json = Json.createObjectBuilder()
					.add(CustomerConstant.ID, idTempJson)
					.add(CustomerConstant.FIRST_NAME, firstNameTempJson)
					.add(CustomerConstant.LAST_NAME, lastNameTempJson)
					.add(CustomerConstant.ADDRESS, addressTempJson)
					.add(CustomerConstant.MOBILE_PHONE, mobileTempJson)
					
					.add(CustomerConstant.IS_ACTIVE, isActiveStatusTempJson)
					
					.add(CustomerConstant.CREATE_AT, data.getString(CustomerConstant.CREATE_AT))
					.add(CustomerConstant.CREATE_BY, data.getString(CustomerConstant.CREATE_BY))
					
					.add(CustomerConstant.UPDATE_AT, data.getString(CustomerConstant.UPDATE_AT))
					.add(CustomerConstant.UPDATE_BY, data.getString(CustomerConstant.UPDATE_BY))
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
}
