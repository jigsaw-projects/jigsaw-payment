/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.qiyi.knowledge.thrift.server;

import com.google.protobuf.DescriptorProtos.FieldOptions;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

import com.iqiyi.pay.sdk.ColumnFieldOption;
import com.iqiyi.pay.sdk.ColumnType;
import com.iqiyi.pay.sdk.Pageable;
import com.iqiyi.pay.sdk.TableMessageOption;
import com.iqiyi.pay.sdk.Taglib;
import com.qiyi.knowledge.thrift.utils.ProtoBinder;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 基础数据访问层，将结果转换为protobuf
 *
 * @author Li Hengjun<lihengjun@qiyi.com>
 * @time 2016/5/11
 */
public class BaseProtobufRepository<ID, M extends Message> {
	private final static String ORDER_BY_KEY = " order by ";
	private final static String SELECT_COUNT = "select count(1) from ";

	protected Class<M> messageClass;
	protected Descriptors.Descriptor descriptor;
	protected JdbcTemplate jdbcTemplate;
	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	protected TableMessageOption tableMessageOption;
	protected String tableName;

	/**
	 * 构造函数中直接传入messageClass和tableName
	 *
	 * @param messageClass 类
	 * @param tableName 表名
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings("unchecked")
	public BaseProtobufRepository(Class<M> messageClass, String tableName) {
		this.messageClass = messageClass;
		this.tableName = tableName;
		this.descriptor = ProtoBinder.getDescriptor((Class<Message>) messageClass);
	}

	@SuppressWarnings("unchecked")
	public BaseProtobufRepository(Class<M> messageClass) {
		this.messageClass = messageClass;
		this.descriptor = ProtoBinder.getDescriptor((Class<Message>) messageClass);
	}

	@SuppressWarnings("unchecked")
	public BaseProtobufRepository() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		for (Type type : params) {// 查找 message类
			if (type instanceof Class && Message.class.isAssignableFrom((Class) type)) {
				messageClass = (Class) type;
				break;
			}
		}
		descriptor = ProtoBinder.getDescriptor((Class<Message>) messageClass);
		tableMessageOption = descriptor.getOptions().getExtension(Taglib.tableMessageOption);
		// 默认从protobuf配置中查询表名
		if (tableMessageOption != null && StringUtils.isNotBlank(tableMessageOption.getTableName())) {
			tableName = tableMessageOption.getTableName();
		}
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 查询单个记录
	 *
	 * @param sql
	 * @param args
	 * @return
	 */
	public M queryForObject(String sql, Object... args) {
		try {
			M m = jdbcTemplate.queryForObject(sql, new ProtobufMessageRowMapper<M>(), args);
			return m;
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}

	/**
	 * 查询多个记录
	 *
	 * @param sql
	 * @param args
	 * @return
	 */
	public List<M> queryForList(String sql, Object... args) {
		return jdbcTemplate.query(sql, new ProtobufMessageRowMapper<M>(), args);
	}

	/**
	 * find page
	 *
	 * @param sql
	 * @param pageableBuilder
	 * @param args
	 * @return
	 */
	public List<M> findPage(String sql, Pageable.Builder pageableBuilder, Object... args) {
		sql = sql.toLowerCase();
		if (StringUtils.contains(sql, " limit ")) {
			throw new IllegalArgumentException("sql key[limit] is forbidden:" + sql);
		}
		if (StringUtils.contains(sql, ORDER_BY_KEY)) {
			throw new IllegalArgumentException("sql key[order by] is forbidden:" + sql);
		}

		if (pageableBuilder.getAutoCount()) {
			StringBuilder countSql = new StringBuilder(SELECT_COUNT);
			String whereSql = StringUtils.substringAfter(sql, "from");
			countSql.append(whereSql);
			int totalCount = jdbcTemplate.queryForObject(countSql.toString(), int.class, args);
			pageableBuilder.setTotalCount(totalCount);
		}
		this.handlePageable(pageableBuilder);

		StringBuilder newSqlBuilder = new StringBuilder(sql);

		int startIndex = (pageableBuilder.getPageNumber() - 1) * pageableBuilder.getPageSize();
		newSqlBuilder.append(ORDER_BY_KEY).append(pageableBuilder.getPageSort()).append(" limit ").append(startIndex)
				.append(",").append(pageableBuilder.getPageSize());

		List<M> list = this.queryForList(newSqlBuilder.toString(), args);
		pageableBuilder.setCurrentPageCount(list == null ? 0 : list.size());

		return list;
	}

	private void handlePageable(Pageable.Builder pageableBuilder) {
		int totalCount = pageableBuilder.getTotalCount();
		int pageSize = pageableBuilder.getPageSize();
		int pageNumber = pageableBuilder.getPageNumber();
		if (pageNumber < 1) {
			pageNumber = 1;
		}
		pageableBuilder.setPageNumber(pageNumber);
		pageableBuilder.setPageSize(pageSize);

		int totalPages = -1;
		if (totalCount >= 0) {
			totalPages = totalCount / pageSize;
			if (totalCount % pageSize > 0) {
				totalPages++;
			}
		}
		pageableBuilder.setTotalPages(totalPages);

		if (pageNumber < totalPages) {
			pageableBuilder.setHasNext(true);
		} else {
			pageableBuilder.setHasNext(false);
		}
		if (pageNumber > 1) {
			pageableBuilder.setHasPrevious(true);
		} else {
			pageableBuilder.setHasPrevious(false);
		}

	}

	/**
	 * update method
	 */
	public int update(String sql, Object... args) {
		return jdbcTemplate.update(sql, args);
	}

	/**
	 * update by preparedStatement
	 *
	 * @author HuXia<xiahu@qiyi.com>
	 * @param sql
	 * @param args
	 * @return
	 */
	public int update(final String sql, final List<Object> args) {
		return jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql);
				ps = setPSParams(ps, args);
				return ps;
			}

		});
	}

	/**
	 * set preparedstatement params
	 *
	 * @param ps
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	protected PreparedStatement setPSParams(PreparedStatement ps, List args) throws SQLException {
		for (int i = 0; i < args.size(); i++) {
			Object o = args.get(i);
			if (o instanceof Descriptors.EnumValueDescriptor){
                ps.setInt(i+1, ((Descriptors.EnumValueDescriptor) o).getNumber());
            }else {
				ps.setObject(i+1,o);
			}
		}
		return ps;
	}

	/**
	 * insert object to db, return auto id
	 *
	 * @author HuXia<xiahu@qiyi.com>
	 */
	public long insert(final String sql, final List<Object> args) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		int ret = jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
				ps = setPSParams(ps, args);
				return ps;
			}
		}, keyHolder);
		return ret > 0 && keyHolder.getKey()!=null ? (long) keyHolder.getKey() : ret;
	}

	/**
	 * 数据库行映射到 protobuf builder对象
	 */
	public class ProtobufBuilderRowMapper<B extends Message.Builder> implements RowMapper<B> {
		@Override
		public B mapRow(ResultSet rs, int rowNum) throws SQLException {
			return resultSetToBuilder(rs, rowNum);
		}
	}

	/**
	 * 数据库行映射到 protobuf message对象
	 */
	public class ProtobufMessageRowMapper<M extends Message> implements RowMapper<M> {
		@Override
		public M mapRow(ResultSet rs, int rowNum) throws SQLException {
			M.Builder builder;
			builder = resultSetToBuilder(rs, rowNum);
			return (M) builder.build();
		}
	}

	/**
	 * convert db resultset to message.builder
	 *
	 * @param rs
	 *            resultset
	 * @param rowNum
	 * @param <B>
	 * @return
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	private <B extends Message.Builder> B resultSetToBuilder(ResultSet rs, int rowNum) throws SQLException {
		B builder = (B) ProtoBinder.newBuilder(messageClass);
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();// 列个数
		String columnLabel = null;// 列名
		Object columnValue = null;// 列值
		FieldDescriptor fieldDescriptor = null;
		for (int i = 1; i <= columnCount; i++) {
			columnLabel = metaData.getColumnLabel(i);
			columnValue = rs.getObject(i);
			if (columnValue == null)
				continue;// 如果为空，继续下一个
			fieldDescriptor = descriptor.findFieldByName(columnLabel);
			if (fieldDescriptor == null)
				continue;// 如果为空，继续下一个
			// 转换为相应的类型 ，会自动将 date 类型转换为long
            if (fieldDescriptor.getType().equals(FieldDescriptor.Type.ENUM)){
                if (columnValue instanceof Integer) {
                    columnValue = fieldDescriptor.getEnumType().findValueByNumber((int) columnValue);
                } else if (columnValue instanceof String) {
                    columnValue = fieldDescriptor.getEnumType().findValueByName((String) columnValue);
                }
            }else {
                columnValue = ConvertUtils.convert(columnValue, fieldDescriptor.getDefaultValue().getClass());
            }
            if(columnValue==null) {
				continue;
			}
			builder.setField(fieldDescriptor, columnValue);
		}
		return builder;
	}

	/**
	 * get connection
	 *
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return jdbcTemplate.getDataSource().getConnection();
	}

	/**
	 * insert message to db
	 *
	 * @author HuXia<xiahu@qiyi.com>
	 * @param message
	 * @return
	 */
	protected long insertMessage(M message) {
		return insertMessage(message, this.tableName);
	}

    /**
     * 插入或者更新，在遇到唯一索引冲突下会更新数据
     *
     * @param message                信息
     * @param columnsWithDBLocalTime 需要使用mysql本地时间字段，e.g.   create_time=now(),update_time=now()
     */
    public long insertMessage(M message, String[] columnsWithDBLocalTime) {
        return this.insertMessage(message, columnsWithDBLocalTime, this.tableName);
    }

    /**
     * 插入数据
     *
     * @param message   数据
     * @param tableName 表名
     */
    public long insertMessage(M message, String tableName) {
        return this.insertMessage(message, null, tableName);
    }

    /**
     * 插入数据
     *
     * @param message                数据
     * @param tableName              表名
     * @param columnsWithDBLocalTime 需要使用mysql本地时间字段，e.g.   create_time=now(),update_time=now()
     */
    protected long insertMessage(M message, String[] columnsWithDBLocalTime, String tableName) {
        StringBuilder insertSql = new StringBuilder("insert ignore into ");
        insertSql.append(tableName).append("(");
        StringBuilder fields = new StringBuilder("");
        StringBuilder values = new StringBuilder("");
        List<Object> args = new ArrayList<Object>();
        Map<FieldDescriptor, Object> fieldMap = message.getAllFields();

        Set<String> columnsWithDBLocalTimeSet = null;
        if (columnsWithDBLocalTime != null
                && columnsWithDBLocalTime.length > 0) {
            columnsWithDBLocalTimeSet = new HashSet<>(columnsWithDBLocalTime.length);
            columnsWithDBLocalTimeSet.addAll(Arrays.asList(columnsWithDBLocalTime));
        }

        for (Entry<FieldDescriptor, Object> entry : fieldMap.entrySet()) {
            FieldDescriptor fieldDescriptor = entry.getKey();
            FieldOptions fieldOptions = fieldDescriptor.getOptions();
            ColumnFieldOption columnFieldOption = fieldOptions.getExtension(Taglib.columnFieldOption);
            String fieldName = fieldDescriptor.getName();
            Object value = entry.getValue();

            if (columnFieldOption.getColumnType() == ColumnType.DATETIME
                    || columnFieldOption.getColumnType() == ColumnType.TIMESTAMP) {// datetime类型

                if (columnsWithDBLocalTimeSet != null
                        && columnsWithDBLocalTimeSet.contains(fieldName)) {
                    //使用数据库本地时间 now()
                    fields.append(fieldName).append(",");
                    values.append("now(), ");
                } else if (value != null && (long) value > 0) {
                    fields.append(fieldName).append(",");
                    values.append("?, ");
                    args.add(new Timestamp((long) value));
                }
            } else {
                fields.append(fieldName).append(",");
                values.append("?, ");
                args.add(value);
            }
        }
        int tmpIndex = fields.lastIndexOf(",");
        insertSql.append(fields.substring(0, tmpIndex)).append(") values(");
        tmpIndex = values.lastIndexOf(",");
        insertSql.append(values.substring(0, tmpIndex)).append(")");
        return insert(insertSql.toString(), args);
    }

    /**
     * 插入或者更新，在遇到唯一索引冲突下会更新数据
     *
     * @param message 信息
     */
    public long insertOrUpdateMessage(M message) {
        return this.insertOrUpdateMessage(message, this.tableName);
    }

    /**
     * 插入或者更新，在遇到唯一索引冲突下会更新数据
     *
     * @param message                信息
     * @param columnsWithDBLocalTime 需要使用mysql本地时间字段，e.g.   create_time=now(),update_time=now()
     */
    public long insertOrUpdateMessage(M message, String[] columnsWithDBLocalTime) {
        return this.insertOrUpdateMessage(message, columnsWithDBLocalTime, this.tableName);
    }

    /**
     * 插入或者更新，在遇到唯一索引冲突下会更新数据
     *
     * @param message   信息
     * @param tableName 表
     */
    public long insertOrUpdateMessage(M message, String tableName) {
        return this.insertOrUpdateMessage(message, null, tableName);
    }

    /**
     * 插入或者更新，在遇到唯一索引冲突下会更新数据
     *
     * @param message                数据
     * @param tableName              表名
     * @param columnsWithDBLocalTime 需要使用mysql本地时间字段，e.g.   create_time=now(),update_time=now()
     */
    protected long insertOrUpdateMessage(M message, String[] columnsWithDBLocalTime, String tableName) {
        StringBuilder insertSql = new StringBuilder("insert into ");
        insertSql.append(tableName).append("(");
        StringBuilder fields = new StringBuilder("");
        StringBuilder values = new StringBuilder("");
        StringBuilder toUpdate = new StringBuilder("");
        List<Object> args = new ArrayList<Object>();
        Map<Descriptors.FieldDescriptor, Object> fieldMap = message.getAllFields();
        Set<String> columnsWithDBLocalTimeSet = null;
        if (columnsWithDBLocalTime != null
                && columnsWithDBLocalTime.length > 0) {
            columnsWithDBLocalTimeSet = new HashSet<>(columnsWithDBLocalTime.length);
            columnsWithDBLocalTimeSet.addAll(Arrays.asList(columnsWithDBLocalTime));
        }
        List<Object> argsToUpdate = new ArrayList<Object>();

        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : fieldMap.entrySet()) {
            Descriptors.FieldDescriptor fieldDescriptor = entry.getKey();
            FieldOptions fieldOptions = fieldDescriptor.getOptions();
            ColumnFieldOption columnFieldOption = fieldOptions.getExtension(Taglib.columnFieldOption);
            String fieldName = fieldDescriptor.getName();
            Object value = entry.getValue();

            if (columnFieldOption.getColumnType() == ColumnType.DATETIME
                    || columnFieldOption.getColumnType() == ColumnType.TIMESTAMP) {// datetime类型
                if (columnsWithDBLocalTimeSet != null
                        && columnsWithDBLocalTimeSet.contains(fieldName)) {
                    //使用数据库本地时间 now()
                    fields.append(fieldName).append(",");
                    values.append("now(), ");
                    toUpdate.append(fieldName).append("=now(), ");
                } else if (value != null && (long) value > 0) {
                    fields.append(fieldName).append(",");
                    values.append("?, ");
                    toUpdate.append(fieldName).append("=?, ");
                    args.add(new Timestamp((long) value));
                    argsToUpdate.add(new Timestamp((long) value));
                }
            } else {
                fields.append(fieldName).append(",");
                values.append("?, ");
                toUpdate.append(fieldName).append("=?, ");
                args.add(value);
                argsToUpdate.add(value);
            }
        }
        int tmpIndex = fields.lastIndexOf(",");
        insertSql.append(fields.substring(0, tmpIndex)).append(") values(");
        tmpIndex = values.lastIndexOf(",");
        insertSql.append(values.substring(0, tmpIndex)).append(")");
        insertSql.append(" on duplicate key update ");
        tmpIndex = toUpdate.lastIndexOf(",");
        insertSql.append(toUpdate.substring(0, tmpIndex));
        args.addAll(argsToUpdate);
        return insert(insertSql.toString(), args);
    }

    /**
     * update message in db
     * @param message                数据
     * @param conditionFields        条件字段
     * @param conditionParams        条件值
     */
    protected int updateMessageByCondition(M message, String[] conditionFields, Object[] conditionParams) {
        return updateMessageByCondition(message, conditionFields, conditionParams, this.tableName);
    }
    /**
     * 更新数据库数据
     *
     * @param message                数据
     * @param columnsWithDBLocalTime 需要使用mysql本地时间字段，e.g.   create_time=now(),update_time=now()
     * @param conditionFields        条件字段
     * @param conditionParams        条件值
     */
    protected int updateMessageByCondition(M message, String[] columnsWithDBLocalTime, String[] conditionFields, Object[] conditionParams){
      return this.updateMessageByCondition(message,columnsWithDBLocalTime,conditionFields,conditionParams,this.tableName);
    }
    /**
     * update message in db
     *
     * @param message                数据
     * @param conditionFields        条件字段
     * @param conditionParams        条件值
     */
    protected int updateMessageByCondition(M message, String[] conditionFields, Object[] conditionParams,
                                           String tableName) {
        return this.updateMessageByCondition(message, null, conditionFields, conditionParams, tableName);
    }

    /**
     * 更新数据库数据
     *
     * @param message                数据
     * @param columnsWithDBLocalTime 需要使用mysql本地时间字段，e.g.   create_time=now(),update_time=now()
     * @param conditionFields        条件字段
     * @param conditionParams        条件值
     * @param tableName              表名
     */
    protected int updateMessageByCondition(M message, String[] columnsWithDBLocalTime, String[] conditionFields, Object[] conditionParams,
                                           String tableName) {
        if (conditionFields.length != conditionParams.length) {
            throw new IllegalArgumentException("conditionFields length should equal to conditionParams length");
        }
        StringBuilder updateSql = new StringBuilder("update ");
        updateSql.append(tableName).append(" set ");
        StringBuilder options = new StringBuilder("");
        List<Object> args = new ArrayList<Object>();
        Map<FieldDescriptor, Object> fieldMap = message.getAllFields();
        Set<String> columnsWithDBLocalTimeSet = null;
        if (columnsWithDBLocalTime != null
                && columnsWithDBLocalTime.length > 0) {
            columnsWithDBLocalTimeSet = new HashSet<>(columnsWithDBLocalTime.length);
            columnsWithDBLocalTimeSet.addAll(Arrays.asList(columnsWithDBLocalTime));
        }
        for (Entry<FieldDescriptor, Object> entry : fieldMap.entrySet()) {
            FieldDescriptor fieldDescriptor = entry.getKey();

            FieldOptions fieldOptions = fieldDescriptor.getOptions();
            ColumnFieldOption columnFieldOption = fieldOptions.getExtension(Taglib.columnFieldOption);
            String fieldName = fieldDescriptor.getName();
            Object value = entry.getValue();
            if (columnFieldOption.getColumnType() == ColumnType.DATETIME
                    || columnFieldOption.getColumnType() == ColumnType.TIMESTAMP) {// datetime类型
                if (columnsWithDBLocalTimeSet != null
                        && columnsWithDBLocalTimeSet.contains(fieldName)) {
                    //使用数据库本地时间 now()
                    options.append(fieldName).append("=now(), ");
                } else if (value != null && (long) value > 0) {
                    options.append(fieldName).append("=?, ");
                    args.add(new Timestamp((long) value));
                }
            } else {
                options.append(fieldName).append("=?, ");
                args.add(value);
            }

        }
        int tmpIndex = options.lastIndexOf(",");
        updateSql.append(options.substring(0, tmpIndex)).append(" where 1=1 ");
        StringBuilder condition = new StringBuilder();

        for (int i = 0; i < conditionFields.length; i++) {
            condition.append("AND ").append(conditionFields[i]).append("=? ");
            args.add(conditionParams[i]);
        }
        updateSql.append(condition);
        return update(updateSql.toString(), args);

    }

	/**
	 * 根据条件查询单个对象
	 *
	 * @param conditionFields
	 * @param conditionParams
	 * @return
	 */
	public M queryObjectByCondition(String[] conditionFields, Object[] conditionParams) {
		return queryObjectByCondition(conditionFields, conditionParams, tableName);
	}

	public M queryObjectByCondition(String[] conditionFields, Object[] conditionParams, String tableName) {
		StringBuilder querySql = new StringBuilder("select ");
		String fieldStr = getQueryFieldStr();
		List<Object> args = new ArrayList<Object>();
		querySql.append(fieldStr).append(" from ").append(tableName).append(" where 1=1 ");
		for (int i = 0; i < conditionFields.length; i++) {
			querySql.append("AND ").append(conditionFields[i]).append("=? ");
			args.add(conditionParams[i]);
		}
		return queryForObject(querySql.toString(), args.toArray());
	}

	/**
	 * 根据条件查询列表，输出所有字段
	 *
	 * @param conditionFields
	 * @param conditionParams
	 * @return
	 */
	public List<M> queryListByCondition(String[] conditionFields, Object[] conditionParams) {
		return queryListByCondition(conditionFields, conditionParams, this.tableName);
	}

	/**
	 * 根据条件查询列表，输出所有字段
	 *
	 * @param conditionFields
	 * @param conditionParams
	 * @param tableName
	 * @return
	 */
	public List<M> queryListByCondition(String[] conditionFields, Object[] conditionParams, String tableName) {
		StringBuilder querySql = new StringBuilder("select ");
		String fieldStr = getQueryFieldStr();
		List<Object> args = new ArrayList<Object>();
		querySql.append(fieldStr).append(" from ").append(tableName).append(" where 1=1 ");
		for (int i = 0; i < conditionFields.length; i++) {
			querySql.append("AND ").append(conditionFields[i]).append("=? ");
			args.add(conditionParams[i]);
		}
		return this.queryForList(querySql.toString(), args.toArray());
	}

	/**
	 * 获取查询的属性
	 *
	 * @return
	 */
	protected String getQueryFieldStr() {
		StringBuilder fieldStr = new StringBuilder();
		List<FieldDescriptor> fields = descriptor.getFields();
		for (int i = 0; i < fields.size(); i++) {
			if (i == fields.size() - 1) {
				fieldStr.append(fields.get(i).getName());
			} else {
				fieldStr.append(fields.get(i).getName()).append(",");
			}
		}
		return fieldStr.toString();
	}
}
