package com.scalar.carapp.repository;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.carapp.constant.CarRentalConstant;
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
public class CarRentalRepository {

	private static final Logger logger = LogManager.getLogger(CarRentalRepository.class);
	
	private final ClientService clientService;

	public CarRentalRepository(@Value("${client.properties.path}") String properties) throws IOException {
		Injector injector = Guice.createInjector(new ClientModule(new ClientConfig(new File(properties))));
		this.clientService = injector.getInstance(ClientService.class);
	}

	public ContractExecutionResult create(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarRentalConstant.CONTRACT_CREATE_NEW, argument);
		logResponse("create-new-rental", result);
		return result;
	}
	
	public ContractExecutionResult update(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarRentalConstant.CONTRACT_UPDATE, argument);
		logResponse("update-rental", result);
		return result;
	}
	
	public ContractExecutionResult list(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarRentalConstant.CONTRACT_LIST, argument);
		logResponse("list-rental", result);
		return result;
	}

	public ContractExecutionResult details(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarRentalConstant.CONTRACT_DETAILS, argument);
		logResponse("rental-details", result);
		return result;
	}
	
	public ContractExecutionResult updateStatusCarRental(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarRentalConstant.CONTRACT_UPDATE_STATUS, argument);
		logResponse("return-rent-car", result);
		return result;
	}

	private void logResponse(String header, ContractExecutionResult result) {
		logger.info(header + ": (" + (result.getResult().isPresent() ? result.getResult().get() : "{}") + ")");
	}
}
