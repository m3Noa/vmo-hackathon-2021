package com.scalar.carapp.contract.customer;

import com.scalar.base.BaseConstant;
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

public class CustomerUpdater extends Contract {
	
	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			if (!argument.containsKey(CustomerConstant.FIRST_NAME) || !argument.containsKey(CustomerConstant.LAST_NAME) 
					|| !argument.containsKey(CustomerConstant.ADDRESS) || !argument.containsKey(CustomerConstant.MOBILE_PHONE)) {
				throw new ContractContextException("a required key is missing: [firstName] or [lastName] or [address] or [mobilephone]");
			}

			String uuid = argument.getString(CustomerConstant.ID);
			
			// get data by CustomerConstant.ASSET_HOLD_ID
			Optional<Asset> response = ledger.get(CustomerConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset does not exist");
			}

			JsonArray dataList = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();

			if (!this.isExistCustomer(dataList, uuid)) {
				throw new ContractContextException("Customer does not exist!");
			}
			
			JsonArrayBuilder rs = Json.createArrayBuilder();
			for (int i = 0; i < dataList.size(); i++) {
				JsonObject jsonTemp = dataList.getJsonObject(i).asJsonObject();

				JsonObject category = null;
				if (jsonTemp != null && jsonTemp.getString(CustomerConstant.ID).toString().equals(uuid)) {
					category = this.getDataUpdate(jsonTemp, argument);
				}else if(jsonTemp != null){
					category = this.cloneJsonObject(jsonTemp);
				}
				if(category != null) {					
					rs.add(category);
				}
			}
			
			ledger.put(CustomerConstant.ASSET_HOLD_ID, Json.createObjectBuilder().add(BaseConstant.LIST, rs).build());
			return Json.createObjectBuilder()
					.add("status", "Succeeded")
					.add("message", "Customer updated successfully!")
					.build();
			
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}

	/**
	 * check exist of Customer
	 * 
	 * @param listData
	 * @param id: ID of Customer
	 * @return True/False
	 */
	private boolean isExistCustomer(JsonArray listData, String id) {
		boolean result = false;

		for (int i = 0; i < listData.size(); i++) {
			JsonObject jsonTemp = listData.getJsonObject(i).asJsonObject();

			if (jsonTemp != null && jsonTemp.getString(CustomerConstant.ID).toString().equals(id) 
					&& jsonTemp.getInt(CustomerConstant.IS_DELETED) != BaseConstant.IS_DELETED) {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * update Customer: only allow update firstName, lastName, address, mobilephone
	 * @param data
	 * @return JsonObject
	 */
	private JsonObject getDataUpdate(JsonObject data, JsonObject argument) {
		try {
			String firstName = argument.getString(CustomerConstant.FIRST_NAME);
			String lastName = argument.getString(CustomerConstant.LAST_NAME);
			String address = argument.getString(CustomerConstant.ADDRESS);
			String mobile = argument.getString(CustomerConstant.MOBILE_PHONE);
			
			String updateAt = argument.getString(CustomerConstant.UPDATE_AT);
			String updateBy = argument.getString(CustomerConstant.UPDATE_BY);
			
			int isActiveStatus = -1;
			if(argument != null && argument.containsKey(CustomerConstant.IS_ACTIVE)) {
				isActiveStatus = argument.getInt(CustomerConstant.IS_ACTIVE);
			}else if(data != null && data.containsKey(CustomerConstant.IS_ACTIVE)){
				isActiveStatus = data.getInt(CustomerConstant.IS_ACTIVE);
			}
			
			JsonObject json = Json.createObjectBuilder()
					// fields will update
					.add(CustomerConstant.FIRST_NAME, firstName)
					.add(CustomerConstant.LAST_NAME, lastName)
					.add(CustomerConstant.ADDRESS, address)
					.add(CustomerConstant.MOBILE_PHONE, mobile)
					
					.add(CustomerConstant.UPDATE_AT, updateAt)
					.add(CustomerConstant.UPDATE_BY, updateBy)
					
					// clone old data [not update]
					.add(CustomerConstant.ID, data.getString(CustomerConstant.ID).toString())
					
					.add(CustomerConstant.IS_ACTIVE, isActiveStatus)
					.add(CustomerConstant.IS_DELETED, data.getInt(CustomerConstant.IS_DELETED))
					
					.add(CustomerConstant.CREATE_AT, data.getString(CustomerConstant.CREATE_AT))
					.add(CustomerConstant.CREATE_BY, data.getString(CustomerConstant.CREATE_BY))
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
					.add(CustomerConstant.ID, data.getString(CustomerConstant.ID))
					.add(CustomerConstant.FIRST_NAME, data.getString(CustomerConstant.FIRST_NAME))
					.add(CustomerConstant.LAST_NAME, data.getString(CustomerConstant.LAST_NAME))
					.add(CustomerConstant.ADDRESS, data.getString(CustomerConstant.ADDRESS))
					.add(CustomerConstant.MOBILE_PHONE, data.getString(CustomerConstant.MOBILE_PHONE))
					
					.add(CustomerConstant.IS_ACTIVE, data.getInt(CustomerConstant.IS_ACTIVE))
					.add(CustomerConstant.IS_DELETED, data.getInt(CustomerConstant.IS_DELETED))
					
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
