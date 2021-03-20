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

public class CustomerCreater extends Contract {

	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			if (!argument.containsKey(CustomerConstant.FIRST_NAME) || !argument.containsKey(CustomerConstant.LAST_NAME) 
					|| !argument.containsKey(CustomerConstant.ADDRESS) || !argument.containsKey(CustomerConstant.MOBILE_PHONE)) {
				throw new ContractContextException("a required key is missing: [firstName] or [lastName] or [address] or [mobilephone]");
			}

			// get data of asset_id [CustomerConstant.ASSET_HOLD_ID]
			Optional<Asset> response = ledger.get(CustomerConstant.ASSET_HOLD_ID);

			if (response.isPresent()) {
				return this.putMoreItem(ledger, argument, property, response);
			} else {
				return this.addFirstItem(ledger, argument, property);
			}
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}

	/**
	 * create new asset with name is [CustomerConstant.ASSET_HOLD_ID] and add first item list
	 * 
	 * @param ledger
	 * @param argument
	 * @param property
	 * @return JsonObject
	 */
	private JsonObject addFirstItem(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			JsonArrayBuilder dataList = Json.createArrayBuilder();
			dataList.add(this.initNewItem(argument));

			ledger.put(CustomerConstant.ASSET_HOLD_ID, Json.createObjectBuilder().add(BaseConstant.LIST, dataList).build());

			return Json.createObjectBuilder().add("status", "Succeeded")
					.add("message", "Customer " + argument.getString(CustomerConstant.ID) + " created").build();
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}

	}

	/**
	 * add more items into object 'list' of asset [CustomerConstant.ASSET_HOLD_ID]
	 * 
	 * @param ledger
	 * @param argument
	 * @param property
	 * @param response
	 * @return JsonObject
	 */
	private JsonObject putMoreItem(Ledger ledger, JsonObject argument, Optional<JsonObject> property,
			Optional<Asset> response) {
		try {
			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();

			// add more items
			JsonArrayBuilder dataList = Json.createArrayBuilder(jsonArray);
			dataList.add(this.initNewItem(argument));

			ledger.put(CustomerConstant.ASSET_HOLD_ID,
					Json.createObjectBuilder().add(BaseConstant.LIST, dataList).build());

			return Json.createObjectBuilder().add("status", "Succeeded")
					.add("message", "Customer " + argument.getString(CustomerConstant.ID) + " created").build();
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}

	/**
	 * Initial new data item from argument
	 * 
	 * @param argument
	 * @return JsonObject
	 */
	private JsonObject initNewItem(JsonObject argument) {
		try {
			String uuid = argument.getString(CustomerConstant.ID);
			String firstName = argument.getString(CustomerConstant.FIRST_NAME);
			String lastName = argument.getString(CustomerConstant.LAST_NAME);
			String address = argument.getString(CustomerConstant.ADDRESS);
			String mobile = argument.getString(CustomerConstant.MOBILE_PHONE);
			
			int isDeleted = argument.getInt(CustomerConstant.IS_DELETED);
			int isActive = argument.getInt(CustomerConstant.IS_ACTIVE);
			
			String createAt = argument.getString(CustomerConstant.CREATE_AT);
			String createBy = argument.getString(CustomerConstant.CREATE_BY);
			String updateAt = argument.getString(CustomerConstant.UPDATE_AT);
			String updateBy = argument.getString(CustomerConstant.UPDATE_BY);
			
			JsonObject json = Json.createObjectBuilder()
					.add(CustomerConstant.ID, uuid)
					.add(CustomerConstant.FIRST_NAME, firstName)
					.add(CustomerConstant.LAST_NAME, lastName)
					.add(CustomerConstant.ADDRESS, address)
					.add(CustomerConstant.MOBILE_PHONE, mobile)
					
					.add(CustomerConstant.IS_DELETED, isDeleted)
					.add(CustomerConstant.IS_ACTIVE, isActive)
					
					.add(CustomerConstant.CREATE_AT, createAt)
					.add(CustomerConstant.CREATE_BY, createBy)
					.add(CustomerConstant.UPDATE_AT, updateAt)
					.add(CustomerConstant.UPDATE_BY, updateBy)
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
}
