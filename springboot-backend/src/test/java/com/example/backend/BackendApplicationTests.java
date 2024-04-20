package com.example.backend;

import com.example.backend.enums.UserRole;
import com.example.backend.models.FileReport;
import com.example.backend.models.UploadSession;
import com.example.backend.models.User;
import com.example.backend.repositories.FileReportRepository;
import com.example.backend.repositories.UploadSessionRepository;
import com.example.backend.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}
