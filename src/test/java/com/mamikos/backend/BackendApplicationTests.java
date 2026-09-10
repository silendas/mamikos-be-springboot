package com.mamikos.backend;

import com.mamikos.backend.model.Role;
import com.mamikos.backend.model.User;
import com.mamikos.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
		assertThat(true).isTrue();
	}

}
