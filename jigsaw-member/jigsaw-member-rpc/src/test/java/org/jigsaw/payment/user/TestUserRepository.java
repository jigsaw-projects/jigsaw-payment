/**
 * 
 */
package org.jigsaw.payment.user;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomUtils;
import org.jigsaw.payment.model.FeeUnit;
import org.jigsaw.payment.model.PayOrder;
import org.jigsaw.payment.model.User;
import org.jigsaw.payment.user.RpcConfiguration;
import org.jigsaw.payment.user.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月11日
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(classes = TestConfiguration.class, webEnvironment = WebEnvironment.NONE)
public class TestUserRepository {

	@Resource(name = "userRepository")
	private UserRepository userRepository;

	@Test
	public void testCreateAndGet() {
		long uid = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			uid++;
			User.Builder builder = User.newBuilder();
			builder.setId(uid);
			builder.setKey(uid);
			builder.setCreatedTime(System.currentTimeMillis());
			builder.setUserName("test" + uid);
			User user = builder.build();
			
			Assert.assertEquals(userRepository.create(user), ""+uid);
			//Assert.assertNotNull("could not find user with id "+ uid +", index = "+ i, user);
			User readed = userRepository.get("" + uid);
			Assert.assertNotNull("could not find user with id "+ uid +", index = "+ i, readed);
			Assert.assertEquals(readed.getId(), uid);
		}

	}

}
