package com.example.backend;

import com.example.backend.enums.Currency;
import com.example.backend.enums.UserRole;
import com.example.backend.models.*;
import com.example.backend.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UploadSessionRepository uploadSessionRepository;
	@Autowired
	private FileReportRepository fileReportRepository;
	@Autowired
	private InvoiceRepository invoiceRepository;
	@Autowired
	private ItemRepository itemRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void testUserRepository() {

		User user = new User("John Doe", "johndoe@domain.com", "pass1234", UserRole.USER);
		System.out.println(user);
		userRepository.save(user);

		User retrievedUser = userRepository.findById(user.getId()).orElse(null);
		System.out.println(retrievedUser);

		Assertions.assertNotNull(retrievedUser);
		Assertions.assertEquals(user.getUsername(), retrievedUser.getUsername());
		Assertions.assertEquals(user.getEmail(), retrievedUser.getEmail());
		Assertions.assertEquals(user.getPassword(), retrievedUser.getPassword());
		Assertions.assertEquals(user.getRole(), retrievedUser.getRole());

		userRepository.delete(user);
	}

	@Test
	void testFindAllUsers() {

		userRepository.save(new User("Bob", "bobby@domain.com", "a", UserRole.ADMIN));
		userRepository.save(new User("Charles", "charlie@example.com", "b", UserRole.USER));
		userRepository.save(new User("Michael", "mickey@example.com", "c", UserRole.USER));

		List<User> users = userRepository.findAll();
		for(var user : users) {
			System.out.println(user);
		}

		Assertions.assertFalse(users.isEmpty());

		userRepository.deleteAll();
	}

	@Test
	void testUploadSessionRepository() {
		UploadSession uploadSession = new UploadSession(1, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
				LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusHours(1), "completed");
		System.out.println(uploadSession);
		uploadSessionRepository.save(uploadSession);

		UploadSession retrievedUploadSession = uploadSessionRepository.findById(uploadSession.getId()).orElse(null);
		System.out.println(retrievedUploadSession);

		Assertions.assertNotNull(retrievedUploadSession);
		Assertions.assertEquals(uploadSession.getUserId(), retrievedUploadSession.getUserId());
		Assertions.assertEquals(uploadSession.getStartTime(), retrievedUploadSession.getStartTime());
		Assertions.assertEquals(uploadSession.getEndTime(), retrievedUploadSession.getEndTime());
		Assertions.assertEquals(uploadSession.getStatus(), retrievedUploadSession.getStatus());

		uploadSessionRepository.delete(uploadSession);

		// Verify that the UploadSession has been deleted
		Assertions.assertFalse(uploadSessionRepository.existsById(uploadSession.getId()));
	}

	@Test
	void testFileReportRepository() {

		FileReport fileReport = new FileReport(1, 1, "file.txt", "text/plain", LocalDate.now(), 1000, "uploaded");
		System.out.println(fileReport);
		fileReportRepository.save(fileReport);

		FileReport retrievedFileReport = fileReportRepository.findById(fileReport.getId()).orElse(null);
		System.out.println(retrievedFileReport);

		Assertions.assertNotNull(retrievedFileReport);
		Assertions.assertEquals(fileReport.getFilename(), retrievedFileReport.getFilename());
		Assertions.assertEquals(fileReport.getFiletype(), retrievedFileReport.getFiletype());
		Assertions.assertEquals(fileReport.getDateUploaded(), retrievedFileReport.getDateUploaded());
		Assertions.assertEquals(fileReport.getSize(), retrievedFileReport.getSize());
		Assertions.assertEquals(fileReport.getUserId(), retrievedFileReport.getUserId());
		Assertions.assertEquals(fileReport.getStatus(), retrievedFileReport.getStatus());

		fileReportRepository.delete(fileReport);

		Assertions.assertFalse(fileReportRepository.existsById(fileReport.getId()));
	}

	@Test
	void testInvoiceRepository() {

		Invoice invoice = new Invoice();
		invoice.setType("Invoice");
		invoice.setDate(LocalDate.now());
		invoice.setCurrency(Currency.USD);
		invoice.setTaxPercent(new BigDecimal("10.00"));
		invoice.setClientName("Client Name");
		invoice.setClientId("Client ID");
		invoice.setClientAddress("Client Address");
		invoice.setSupplierName("Supplier Name");
		invoice.setSupplierId("Supplier ID");
		invoice.setSupplierAddress("Supplier Address");
		invoice.setTaxExclusiveAmount(new BigDecimal("1000.00"));
		invoice.setTaxAmount(new BigDecimal("100.00"));
		invoice.setTaxInclusiveAmount(new BigDecimal("1100.00"));

		invoiceRepository.save(invoice);

		Invoice retrievedInvoice = invoiceRepository.findById(invoice.getId()).orElse(null);

		Assertions.assertNotNull(retrievedInvoice);
		Assertions.assertEquals(invoice.getType(), retrievedInvoice.getType());
		Assertions.assertEquals(invoice.getDate(), retrievedInvoice.getDate());
		Assertions.assertEquals(invoice.getCurrency(), retrievedInvoice.getCurrency());
		Assertions.assertEquals(invoice.getTaxPercent(), retrievedInvoice.getTaxPercent());
		Assertions.assertEquals(invoice.getClientName(), retrievedInvoice.getClientName());
		Assertions.assertEquals(invoice.getClientId(), retrievedInvoice.getClientId());
		Assertions.assertEquals(invoice.getClientAddress(), retrievedInvoice.getClientAddress());
		Assertions.assertEquals(invoice.getSupplierName(), retrievedInvoice.getSupplierName());
		Assertions.assertEquals(invoice.getSupplierId(), retrievedInvoice.getSupplierId());
		Assertions.assertEquals(invoice.getSupplierAddress(), retrievedInvoice.getSupplierAddress());
		Assertions.assertEquals(invoice.getTaxExclusiveAmount(), retrievedInvoice.getTaxExclusiveAmount());
		Assertions.assertEquals(invoice.getTaxAmount(), retrievedInvoice.getTaxAmount());
		Assertions.assertEquals(invoice.getTaxInclusiveAmount(), retrievedInvoice.getTaxInclusiveAmount());

		invoiceRepository.delete(invoice);

		Assertions.assertFalse(invoiceRepository.existsById(invoice.getId()));
	}

	@Test
	void testItemRepository() {

		Item item = new Item();
		item.setInvoiceId(1);
		item.setName("sample");
		item.setQuantity(5);
		item.setUnitPrice(new BigDecimal("10.00"));

		itemRepository.save(item);

		Item retrievedItem = itemRepository.findById(item.getId()).orElse(null);

		Assertions.assertNotNull(retrievedItem);
		Assertions.assertEquals(item.getInvoiceId(), retrievedItem.getInvoiceId());
		Assertions.assertEquals(item.getName(), retrievedItem.getName());
		Assertions.assertEquals(item.getQuantity(), retrievedItem.getQuantity());
		Assertions.assertEquals(item.getUnitPrice(), retrievedItem.getUnitPrice());

		itemRepository.delete(item);

		Assertions.assertFalse(itemRepository.existsById(item.getId()));
	}
}
