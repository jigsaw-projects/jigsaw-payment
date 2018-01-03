/**
 * 
 */
package org.jigsaw.payment.order;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomUtils;
import org.jigsaw.payment.model.FeeUnit;
import org.jigsaw.payment.model.PayOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class TestPayOrderRepository {
	@Resource(name = "payOrderRepository")
	private PayOrderRepository repository;

	@Test
	public void testCreateAndGet() {
		PayOrder.Builder builder = PayOrder.newBuilder();
		long uid = System.nanoTime();
		String code = UUID.randomUUID().toString();
		builder.setId(System.currentTimeMillis());
		builder.setAppId("myapp");
		builder.setCreateTime(new Date().getTime());
		builder.setCurrentKey(19283745);
		builder.setDestPayType(1982);
		builder.setSubId(uid);
		Calendar expireTime = Calendar.getInstance();
		expireTime.add(Calendar.HOUR, 1);
		builder.setExpireTime(expireTime.getTime().getTime());
		int fee = RandomUtils.nextInt(1, 1000000);
		builder.setFee(fee);
		builder.setFeeReal(RandomUtils.nextInt(1, fee));
		builder.setFeeUnit(FeeUnit.CNY_VALUE);
		builder.setNotifyUrl("http://localhost/notify");
		builder.setReturnUrl("http://localhost/returnUrl");
		builder.setOrderDetail("OrderDetail");
		builder.setOrderId(UUID.randomUUID().toString());
		repository.create(builder.build());
		PayOrder order = repository.get(uid, code);
		assertEquals(order.getSubId(), uid);

	}

}
