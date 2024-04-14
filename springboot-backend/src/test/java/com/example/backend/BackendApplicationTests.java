package com.example.backend;

import com.example.backend.enums.UserRole;
import com.example.backend.models.User;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	UserRepository userRepository;
	@Test
	void contextLoads() {
	}

	@Test
	void testUserRepository() {

		User user;
		user = new User("John Doe", "johndoe@domain.com", "pass1234", UserRole.USER);
		userRepository.save(user);

		// Retrieve the saved user from the database
		User retrievedUser = userRepository.findById(user.getId()).orElse(null);
		System.out.println(user);

		// Verify that the retrieved user matches the saved user
		Assertions.assertNotNull(retrievedUser);
		Assertions.assertEquals(user.getUsername(), retrievedUser.getUsername());
		Assertions.assertEquals(user.getEmail(), retrievedUser.getEmail());
		Assertions.assertEquals(user.getPassword(), retrievedUser.getPassword());
		Assertions.assertEquals(user.getRole(), retrievedUser.getRole());

		userRepository.delete(user);
	}

	@Test
	void testFindAllUsers() {
		// Save multiple test users
		userRepository.save(new User("Bob", "bobby@domain.com", "a", UserRole.ADMIN));
		userRepository.save(new User("Charles", "charlie@example.com", "b", UserRole.USER));
		userRepository.save(new User("Michael", "mickey@example.com", "c", UserRole.USER));

		// Retrieve all users from the database
		List<User> users = userRepository.findAll();
		for(var user : users) {
			System.out.println(user);
		}

		// Verify that the list is not empty
		Assertions.assertFalse(users.isEmpty());

		// Delete the test users
		userRepository.deleteAll();
	}

}
