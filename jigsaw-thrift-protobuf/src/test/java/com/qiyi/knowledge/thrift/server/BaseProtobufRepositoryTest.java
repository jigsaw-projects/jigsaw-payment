package com.qiyi.knowledge.thrift.server;

import com.google.protobuf.Descriptors;

import com.iqiyi.pay.sdk.EntityType;
import com.iqiyi.pay.sdk.Trade;
import com.iqiyi.pay.sdk.TradeStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author lihengjun@qiyi.com
 * @time 5/15/17
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Appconfig.class)
@ActiveProfiles("dev")
public class BaseProtobufRepositoryTest {
    @Autowired
    TradeRepository tradeRepository;
    long uid = 1012977600;
    int partnerId = 1;
    String code = "1111111111111";
    String tableName = "pay_trade_0";
    Trade trade;
    long time = (System.currentTimeMillis() / 1000) * 1000;//去掉毫秒数
    List<Trade> list = new ArrayList<>();
    List<String> codeList = new ArrayList<>();

    @Before
    public void init() {
        for (int i = 0; i < 1; i++) {
            Trade.Builder tb = Trade.newBuilder();
            tb.setId(1111111111111L);
            tb.setCode(code);
            tb.setPayIndex(1);
            tb.setPayScenarios(1);
            tb.setSubId(uid);
            tb.setAppId("123");
            tb.setReturnUrl("123");
            tb.setNotifyUrl("123");
            tb.setPartnerId(partnerId);
            tb.setFee(123);
            tb.setFeeReal(123);
            tb.setPartnerOrderNo(uid + "" + i);
            tb.setParentId(1);
            tb.setCurrentKey(122);
            tb.setSubType(EntityType.USER);
            tb.setDestPayType(344);
            tb.setSourcePayType(261);
            tb.setStatus(TradeStatus.UNPAID);
            tb.setSubIp("44.23465.2");
            tb.setOrderId("8888");
            tb.setCreateTime(time);
            tb.setUpdateTime(time);
            tb.setExpireTime(time);
            tb.setPayTime(time);
            tb.setThirdCreateTime(time);
            tb.setThirdPayTime(time);
            updateBuilder(tb, 1);

            trade = tb.build();
            list.add(trade);
        }
    }
    private void updateBuilder(Trade.Builder tb, int loadFactor) {
        this.updateBuilder(tb,loadFactor,null);
    }
    private void updateBuilder(Trade.Builder tb, int loadFactor,String[] excludeFields) {
        Set<String> set = null;
        if (excludeFields != null && excludeFields.length > 0) {
            set = new HashSet<>(Arrays.asList(excludeFields));
        }

        final Descriptors.Descriptor descriptor = tb.getDescriptorForType();
        for (Descriptors.FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            if(set!=null&&set.contains(fieldDescriptor.getName())){
                continue;
            }
            switch (fieldDescriptor.getJavaType()) {
                case LONG:
                    tb.setField(fieldDescriptor, 10000000 + loadFactor * 10000);
                    break;
                case DOUBLE:
                    tb.setField(fieldDescriptor, 100000 + loadFactor);
                    break;
                case INT:
                    tb.setField(fieldDescriptor, 10 + loadFactor);
                    break;
                case STRING:
                    tb.setField(fieldDescriptor, "test" + loadFactor);
                    break;
                case ENUM:
                    tb.setField(fieldDescriptor, fieldDescriptor.getEnumType().getValues().get(1));
                    break;
                default:
                    break;
            }
        }
    }


    private void validate(Trade expected, Trade actual) {
        this.validate(expected, actual, null);
    }

    private void validate(Trade expected, Trade actual, String[] notEquals) {
        final Descriptors.Descriptor descriptor = actual.getDescriptorForType();
        Set<String> set = null;
        if (notEquals != null && notEquals.length > 0) {
            set = new HashSet<>(Arrays.asList(notEquals));
        }
        for (Descriptors.FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            if (set != null && set.contains(fieldDescriptor.getName())) {
                assertNotEquals(fieldDescriptor.getName() + " is equal", actual.getField(fieldDescriptor), expected.getField(fieldDescriptor));
            } else {
                assertEquals(fieldDescriptor.getName() + " is not equal", actual.getField(fieldDescriptor), expected.getField(fieldDescriptor));
            }
        }
    }

    @Test
    public void insertMessage() throws Exception {
        Trade.Builder builder = Trade.newBuilder(trade);
        this.updateBuilder(builder, 10);
        final Trade trade2 = builder.build();
        delete(trade2.getCode());
        final long count = tradeRepository.insertMessage(trade2, new String[]{});
        assertEquals(1, count);
        this.validate(trade2, tradeRepository.queryByCode(trade2.getCode()));
        delete(trade2.getCode());
    }

    @Test
    public void insertMessage1() throws Exception {
        Trade.Builder builder = Trade.newBuilder(trade);
        this.updateBuilder(builder, 11);
        final Trade trade2 = builder.build();
        delete(trade2.getCode());
        assertEquals(1, tradeRepository.insertMessage(trade2, tableName));
        this.validate(trade2, tradeRepository.queryByCode(trade2.getCode()));
        delete(trade2.getCode());
    }

    @Test
    public void insertMessage2() throws Exception {
        Trade.Builder builder = Trade.newBuilder(trade);
        this.updateBuilder(builder, 13);
        final Trade trade2 = builder.build();
        delete(trade2.getCode());
        assertEquals(1, tradeRepository.insertMessage(trade2, new String[]{"create_time"}, tableName));
        this.validate(trade2, tradeRepository.queryByCode(trade2.getCode()), new String[]{"create_time"});
        delete(trade2.getCode());
    }

    @Test
    public void insertMessage3() throws Exception {
        Trade.Builder builder = Trade.newBuilder(trade);
        this.updateBuilder(builder, 2);
        final Trade trade2 = builder.build();
        delete(trade2.getCode());
        assertEquals(1, tradeRepository.insertMessage(trade2));
        this.validate(trade2, tradeRepository.queryByCode(trade2.getCode()));
        delete(trade2.getCode());
    }

    @Test
    public void insertOrUpdateMessage1() throws Exception {
        Trade.Builder builder = Trade.newBuilder(trade);
        this.updateBuilder(builder, 2);
        Trade trade2 = builder.build();
        delete(trade2.getCode());
        long count = tradeRepository.insertOrUpdateMessage(builder.build());
        this.updateBuilder(builder, 21,new String[]{"code"});
        trade2 =builder.build();
        count += tradeRepository.insertOrUpdateMessage(trade2, new String[]{"create_time","update_time"});
        assertTrue(count>=2);
        this.validate(trade2, tradeRepository.queryByCode(trade2.getCode()), new String[]{"create_time","update_time"});
        delete(trade2.getCode());
    }

    @Test
    public void insertOrUpdateMessage2() throws Exception {

        Trade.Builder builder = Trade.newBuilder(trade);
        this.updateBuilder(builder, 2);
        Trade trade2 = builder.build();
        delete(trade2.getCode());
        long count = tradeRepository.insertOrUpdateMessage(builder.build());
        this.updateBuilder(builder, 21,new String[]{"code"});
        trade2 =builder.build();
        count += tradeRepository.insertOrUpdateMessage(trade2, new String[]{"create_time","update_time"});
        assertTrue(count>=2);
        this.validate(trade2, tradeRepository.queryByCode(trade2.getCode()), new String[]{"create_time","update_time"});
        delete(trade2.getCode());
    }

    @Test
    public void insertOrUpdateMessage3() throws Exception {
        Trade.Builder builder = Trade.newBuilder(trade);
        this.updateBuilder(builder, 2);
        Trade trade2 = builder.build();
        delete(trade2.getCode());
        long count = tradeRepository.insertOrUpdateMessage(trade2);
        this.updateBuilder(builder, 21,new String[]{"code"});
        trade2 =builder.build();
        count += tradeRepository.insertOrUpdateMessage(trade2,tableName);
        assertTrue(count>=2);
        this.validate(trade2, tradeRepository.queryByCode(trade2.getCode()));
        delete(trade2.getCode());
    }

    @Test
    public void insertOrUpdateMessage4() throws Exception {
        Trade.Builder builder = Trade.newBuilder(trade);
        this.updateBuilder(builder, 2);
        Trade trade2 = builder.build();
        delete(trade2.getCode());
        long count = tradeRepository.insertOrUpdateMessage(trade2);
        this.updateBuilder(builder, 21,new String[]{"code"});
        trade2 =builder.build();
        count += tradeRepository.insertOrUpdateMessage(trade2, new String[]{"create_time","update_time"}, tableName);
        assertTrue(count>=2);
        this.validate(trade2, tradeRepository.queryByCode(trade2.getCode()), new String[]{"create_time","update_time"});
        delete(trade2.getCode());

    }

    @Test
    public void updateMessageByCondition() throws Exception {
        Trade.Builder builder = Trade.newBuilder(trade);
        this.updateBuilder(builder, 20);
        Trade trade2 = builder.build();
        delete(trade2.getCode());
        long count = tradeRepository.insertOrUpdateMessage(trade2);
        this.updateBuilder(builder, 21,new String[]{"code"});
        trade2 =builder.build();
        count += tradeRepository.updateMessageByCondition(trade2, new String[]{"create_time","update_time"},new String[]{"code"},new Object[]{trade2.getCode()} );
        assertTrue(count>=2);
        this.validate(trade2, tradeRepository.queryByCode(trade2.getCode()), new String[]{"create_time","update_time"});
        delete(trade2.getCode());
    }

    @Test
    public void updateMessageByCondition1() throws Exception {
        Trade.Builder builder = Trade.newBuilder(trade);
        this.updateBuilder(builder, 21);
        Trade trade2 = builder.build();
        delete(trade2.getCode());
        long count = tradeRepository.insertOrUpdateMessage(trade2);
        this.updateBuilder(builder, 11,new String[]{"code"});
        trade2 =builder.build();
        count += tradeRepository.updateMessageByCondition(trade2, new String[]{"create_time","update_time"},new String[]{"code"},new Object[]{trade2.getCode()},tableName );
        assertTrue(count>=2);
        this.validate(trade2, tradeRepository.queryByCode(trade2.getCode()), new String[]{"create_time","update_time"});
        delete(trade2.getCode());
    }

    @Test
    public void updateMessageByCondition2() throws Exception {
        Trade.Builder builder = Trade.newBuilder(trade);
        this.updateBuilder(builder, 22);
        Trade trade2 = builder.build();
        delete(trade2.getCode());
        long count = tradeRepository.insertOrUpdateMessage(trade2);
        this.updateBuilder(builder, 21,new String[]{"code"});
        trade2 =builder.build();
        count += tradeRepository.updateMessageByCondition(trade2,new String[]{"code"},new Object[]{trade2.getCode()},tableName );
        assertTrue(count>=2);
        this.validate(trade2, tradeRepository.queryByCode(trade2.getCode()));
        delete(trade2.getCode());
    }

    @Test
    public void updateMessageByCondition3() throws Exception {
        Trade.Builder builder = Trade.newBuilder(trade);
        this.updateBuilder(builder, 22);
        Trade trade2 = builder.build();
        delete(trade2.getCode());
        long count = tradeRepository.insertOrUpdateMessage(trade2);
        this.updateBuilder(builder, 21,new String[]{"code"});
        trade2 =builder.build();
        count += tradeRepository.updateMessageByCondition(trade2,new String[]{"code"},new Object[]{trade2.getCode()});
        assertTrue(count>=2);
        this.validate(trade2, tradeRepository.queryByCode(trade2.getCode()));
        delete(trade2.getCode());
    }


    private int delete(String code) {
        final int update = tradeRepository.update("delete from pay_trade_0 where code=?", code);
        System.out.print("delete count:" + update);
        return update;
    }
}