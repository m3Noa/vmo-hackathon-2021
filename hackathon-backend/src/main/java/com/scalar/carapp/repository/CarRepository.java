package com.scalar.carapp.repository;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.carapp.constant.CarConstant;
import com.scalar.dl.client.config.ClientConfig;
import com.scalar.dl.client.service.ClientModule;
import com.scalar.dl.client.service.ClientService;
import com.scalar.dl.ledger.model.ContractExecutionResult;
import java.io.File;
import java.io.IOException;
import javax.inject.Singleton;
import javax.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@Singleton
public class CarRepository {

	private static final Logger logger = LogManager.getLogger(CarRepository.class);
	
	private final ClientService clientService;

	public CarRepository(@Value("${client.properties.path}") String properties) throws IOException {
		Injector injector = Guice.createInjector(new ClientModule(new ClientConfig(new File(properties))));
		this.clientService = injector.getInstance(ClientService.class);
	}

	public ContractExecutionResult create(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarConstant.CONTRACT_CREATE_NEW, argument);
		logResponse("create-car", result);
		return result;
	}
	
	public ContractExecutionResult listAvailableCars(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarConstant.CONTRACT_LIST_AVAILABLE, argument);
		logResponse("list-available-cars", result);
		return result;
	}
	
	public ContractExecutionResult update(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarConstant.CONTRACT_UPDATE, argument);
		logResponse("update-car", result);
		return result;
	}
	
	public ContractExecutionResult list(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarConstant.CONTRACT_LIST, argument);
		logResponse("list-car", result);
		return result;
	}

	public ContractExecutionResult details(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarConstant.CONTRACT_DETAILS, argument);
		logResponse("car-details", result);
		return result;
	}
	
	public ContractExecutionResult delete(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarConstant.CONTRACT_DELETE, argument);
		logResponse("delete-car", result);
		return result;
	}

	private void logResponse(String header, ContractExecutionResult result) {
		logger.info(header + ": (" + (result.getResult().isPresent() ? result.getResult().get() : "{}") + ")");
	}
}
