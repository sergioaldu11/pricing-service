package com.inditex.pricingservice;

import com.inditex.pricingservice.controller.PriceController;
import com.inditex.pricingservice.controller.impl.PriceControllerImpl;
import com.inditex.pricingservice.dto.InputDTO;
import com.inditex.pricingservice.dto.OutputDTO;
import com.inditex.pricingservice.service.PriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class pricingServiceTest {

	@Mock
	private PriceService priceService;

	@InjectMocks
	private PriceController priceController = new PriceControllerImpl();

	@Test
	public void testGetPrice_At10AM_Day14() {
		LocalDateTime applicationDate = LocalDateTime.of(2023, 1, 14, 10, 0);
		Long productId = 35455L;
		Long brandId = 1L;

		InputDTO inputDTO = InputDTO.builder()
				.applicationDate(applicationDate)
				.productId(productId)
				.brandId(brandId)
				.build();

		OutputDTO expectedOutputDTO = OutputDTO.builder()
				.brandId(brandId)
				.productId(productId)
				.priceList(1)
				.startDate(LocalDateTime.of(2023, 1, 14, 0, 0))
				.endDate(LocalDateTime.of(2023, 12, 31, 23, 59, 59))
				.price(35.50)
				.currency("EUR")
				.build();

		ResponseEntity<OutputDTO> responseEntity = ResponseEntity.ok(expectedOutputDTO);
		when(priceService.findPrice(inputDTO)).thenReturn(responseEntity);

		ResponseEntity<OutputDTO> result = priceController.getPrice(applicationDate, productId, brandId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(expectedOutputDTO, result.getBody());
	}

	@Test
	public void testGetPrice_At4PM_Day14() {
		LocalDateTime applicationDate = LocalDateTime.of(2023, 1, 14, 16, 0);
		Long productId = 35455L;
		Long brandId = 1L;
		InputDTO inputDTO = InputDTO.builder()
				.applicationDate(applicationDate)
				.productId(productId)
				.brandId(brandId)
				.build();

		OutputDTO expectedOutputDTO = OutputDTO.builder()
				.brandId(brandId)
				.productId(productId)
				.priceList(2)
				.startDate(LocalDateTime.of(2023, 1, 14, 15, 0))
				.endDate(LocalDateTime.of(2023, 1, 14, 18, 30))
				.price(25.45)
				.currency("EUR")
				.build();

		ResponseEntity<OutputDTO> responseEntity = ResponseEntity.ok(expectedOutputDTO);
		when(priceService.findPrice(inputDTO)).thenReturn(responseEntity);

		ResponseEntity<OutputDTO> result = priceController.getPrice(applicationDate, productId, brandId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(expectedOutputDTO, result.getBody());
	}

	@Test
	public void testGetPrice_At9PM_Day14() {
		LocalDateTime applicationDate = LocalDateTime.of(2023, 1, 14, 21, 0);
		Long productId = 35455L;
		Long brandId = 1L;
		InputDTO inputDTO = InputDTO.builder()
				.applicationDate(applicationDate)
				.productId(productId)
				.brandId(brandId)
				.build();

		OutputDTO expectedOutputDTO = OutputDTO.builder()
				.brandId(brandId)
				.productId(productId)
				.priceList(1)
				.startDate(LocalDateTime.of(2023, 1, 14, 0, 0))
				.endDate(LocalDateTime.of(2023, 12, 31, 23, 59, 59))
				.price(35.50)
				.currency("EUR")
				.build();

		ResponseEntity<OutputDTO> responseEntity = ResponseEntity.ok(expectedOutputDTO);
		when(priceService.findPrice(inputDTO)).thenReturn(responseEntity);

		ResponseEntity<OutputDTO> result = priceController.getPrice(applicationDate, productId, brandId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(expectedOutputDTO, result.getBody());
	}

	@Test
	public void testGetPrice_At10AM_Day15() {
		LocalDateTime applicationDate = LocalDateTime.of(2023, 1, 15, 10, 0);
		Long productId = 35455L;
		Long brandId = 1L;
		InputDTO inputDTO = InputDTO.builder()
				.applicationDate(applicationDate)
				.productId(productId)
				.brandId(brandId)
				.build();

		OutputDTO expectedOutputDTO = OutputDTO.builder()
				.brandId(brandId)
				.productId(productId)
				.priceList(3)
				.startDate(LocalDateTime.of(2023, 1, 15, 0, 0))
				.endDate(LocalDateTime.of(2023, 1, 15, 11, 0))
				.price(30.50)
				.currency("EUR")
				.build();

		ResponseEntity<OutputDTO> responseEntity = ResponseEntity.ok(expectedOutputDTO);
		when(priceService.findPrice(inputDTO)).thenReturn(responseEntity);

		ResponseEntity<OutputDTO> result = priceController.getPrice(applicationDate, productId, brandId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(expectedOutputDTO, result.getBody());
	}

	@Test
	public void testGetPrice_At9PM_Day16() {
		LocalDateTime applicationDate = LocalDateTime.of(2023, 1, 16, 21, 0);
		Long productId = 35455L;
		Long brandId = 1L;
		InputDTO inputDTO = InputDTO.builder()
				.applicationDate(applicationDate)
				.productId(productId)
				.brandId(brandId)
				.build();

		OutputDTO expectedOutputDTO = OutputDTO.builder()
				.brandId(brandId)
				.productId(productId)
				.priceList(4)
				.startDate(LocalDateTime.of(2023, 1, 15, 16, 0))
				.endDate(LocalDateTime.of(2023, 12, 31, 23, 59, 59))
				.price(38.95)
				.currency("EUR")
				.build();

		ResponseEntity<OutputDTO> responseEntity = ResponseEntity.ok(expectedOutputDTO);
		when(priceService.findPrice(inputDTO)).thenReturn(responseEntity);

		ResponseEntity<OutputDTO> result = priceController.getPrice(applicationDate, productId, brandId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertEquals(expectedOutputDTO, result.getBody());
	}
}
