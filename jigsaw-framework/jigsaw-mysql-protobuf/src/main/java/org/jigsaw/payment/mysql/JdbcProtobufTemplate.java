package org.jigsaw.payment.mysql;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.jigsaw.payment.model.ColumnFieldOption;
import org.jigsaw.payment.model.ColumnType;
import org.jigsaw.payment.model.TableMessageOption;
import org.jigsaw.payment.model.Taglib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import com.google.protobuf.DescriptorProtos.FieldOptions;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

/**
 * 存取Protobuf message 数据对象到JDBC数据库中， 是对<code>JdbcTemplate</code>的一个封装。
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月9日
 * @param <ID>
 *            ID的数据类型
 * @param <M>
 *            message的数据类型
 */
public class JdbcProtobufTemplate<ID, M extends Message> {
	
	private static Logger logger = LoggerFactory.getLogger(JdbcProtobufTemplate.class);
	/**
	 * 数据库行映射到 protobuf message对象
	 */
	public class ProtobufMessageRowMapper<N extends Message> implements
			RowMapper<N> {
		@SuppressWarnings("unchecked")
		@Override
		public N mapRow(ResultSet rs, int rowNum) throws SQLException {
			Message.Builder builder = newBuilder(messageClass);
			populate(rs, builder);
			return (N) builder.build();
		}
	}

	protected Class<M> messageClass;
	protected Descriptors.Descriptor descriptor;
	protected JdbcTemplate jdbcTemplate;
	protected String tableName;

	public JdbcProtobufTemplate(JdbcTemplate jdbcTemplate) {
		this(jdbcTemplate, null);
	}

	public JdbcProtobufTemplate(JdbcTemplate jdbcTemplate, Class<M> messageClass) {
		this(jdbcTemplate, messageClass, null);
	}

	public JdbcProtobufTemplate(JdbcTemplate jdbcTemplate,
			Class<M> messageClass, String tableName) {
		this.jdbcTemplate = jdbcTemplate;
		if (messageClass == null) {
			this.messageClass = this.parseMessageClass();
		} else {
			this.messageClass = messageClass;
		}
		this.descriptor = this.getDescriptor(messageClass);
		if (tableName == null) {
			this.tableName = this.parseTableName();
		} else {
			this.tableName = tableName;
		}

	}

	private Class<M> parseMessageClass() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		for (Type type : params) {// 查找 message类
			if (type instanceof Class
					&& Message.class.isAssignableFrom((Class) type)) {
				return (Class) type;
			}
		}
		return null;
	}

	private String parseTableName() {
		TableMessageOption tableMessageOption = descriptor.getOptions()
				.getExtension(Taglib.tableOption);
		// 默认从protobuf配置中查询表名
		if (tableMessageOption != null
				&& StringUtils.isNotBlank(tableMessageOption.getTableName())) {
			return tableMessageOption.getTableName();
		}
		return null;
	}

	private Descriptors.Descriptor getDescriptor(Class<M> messageClass) {
		try {
			return (Descriptors.Descriptor) MethodUtils.invokeStaticMethod(
					messageClass, "getDescriptor");
		} catch (NoSuchMethodException | IllegalAccessException
				| InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 查询单个记录
	 *
	 * @param sql
	 * @param args
	 * @return
	 */
	public M get(String sql, Object... args) {
		logger.debug(sql);
		try {
			return jdbcTemplate.queryForObject(sql,
					new ProtobufMessageRowMapper<M>(), args);
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
	public List<M> query(String sql, Object... args) {
		logger.debug(sql);
		return jdbcTemplate.query(sql, new ProtobufMessageRowMapper<M>(), args);
	}

	/**
	 * update method
	 */
	public int update(String sql, Object... args) {
		logger.debug(sql);
		return jdbcTemplate.update(sql, args);
	}

	/**
	 * 
	 * @param sql
	 * @param args
	 * @return
	 */
	public int update(final String sql, final List<?> args) {
		if(logger.isDebugEnabled()){
			StringBuilder builder = new StringBuilder();
			builder.append("{sql: \"").append(sql).append("\"; parameters:").append(args);
			logger.debug(builder.toString());
		}
		return jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql);
				populate(ps, args);
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
	private void populate(PreparedStatement ps, List<?> args)
			throws SQLException {
		for (int i = 0; i < args.size(); i++) {
			Object o = args.get(i);
			if (o instanceof Integer) {
				ps.setInt(i + 1, (int) o);
			} else if (o instanceof Long) {
				ps.setLong(i + 1, (long) o);
			} else if (o instanceof String) {
				ps.setString(i + 1, (String) o);
			} else if (o instanceof Date) {
				ps.setDate(i + 1, (Date) o);
			} else if (o instanceof Float) {
				ps.setFloat(i + 1, (Float) o);
			} else if (o instanceof Double) {
				ps.setDouble(i + 1, (Double) o);
			} else if (o instanceof Date) {
				ps.setDate(i + 1, (Date) o);
			} else if (o instanceof Timestamp) {
				ps.setTimestamp(i + 1, (Timestamp) o);
			} else if (o instanceof Descriptors.EnumValueDescriptor) {
				ps.setInt(i + 1,
						((Descriptors.EnumValueDescriptor) o).getNumber());
			} else if(o instanceof Boolean){
				ps.setBoolean(i+1, (Boolean)o);
			} else {
				ps.setObject(i+1, o);
			}
		}
	}

	/**
	 * 
	 * @param rs
	 * @param builder
	 * @throws SQLException
	 */
	private void populate(ResultSet rs, Message.Builder builder)
			throws SQLException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();// 列个数
		String columnLabel = null;// 列名
		Object columnValue = null;// 列值
		Descriptors.FieldDescriptor fieldDescriptor = null;
		for (int i = 1; i <= columnCount; i++) {
			columnLabel = metaData.getColumnLabel(i);
			columnValue = rs.getObject(i);
			if (columnValue == null)
				continue;// 如果为空，继续下一个
			fieldDescriptor = descriptor.findFieldByName(columnLabel);
			if (fieldDescriptor == null)
				continue;// 如果为空，继续下一个
			// 转换为相应的类型 ，会自动将 date 类型转换为long
			if (fieldDescriptor.getType().equals(FieldDescriptor.Type.ENUM)) {
				columnValue = fieldDescriptor.getEnumType().findValueByNumber(
						(int) columnValue);
			} else {
				columnValue = ConvertUtils.convert(columnValue, fieldDescriptor
						.getDefaultValue().getClass());
			}
			builder.setField(fieldDescriptor, columnValue);
		}
	}

	/**
	 * 插入对象到默认的表中
	 * 
	 * @param message
	 * @return
	 */
	public long insert(M message) {
		return insert(message, this.tableName);
	}

	/**
	 * 插入对象到给定的表中。注意，这里并不自动生成ID。
	 * 
	 * @param message
	 * @param tableName
	 * @return
	 */
	public long insert(M message, String tableName) {
		StringBuilder insertSql = new StringBuilder("insert into ");
		insertSql.append(tableName).append("(");
		StringBuilder fields = new StringBuilder("");
		StringBuilder values = new StringBuilder("");
		List<Object> args = new ArrayList<Object>();
		Map<FieldDescriptor, Object> fieldMap = message.getAllFields();

		for (Entry<FieldDescriptor, Object> entry : fieldMap.entrySet()) {
			FieldDescriptor fieldDescriptor = entry.getKey();
			FieldOptions fieldOptions = fieldDescriptor.getOptions();
			ColumnFieldOption columnFieldOption = fieldOptions
					.getExtension(Taglib.columnOption);
			String fieldName = fieldDescriptor.getName();
			Object value = entry.getValue();

			if (columnFieldOption.getColumnType() == ColumnType.DATETIME
					|| columnFieldOption.getColumnType() == ColumnType.TIMESTAMP) {// datetime类型
				if (value != null && (long) value > 0) {
					fields.append('`').append(fieldName).append("`,");
					values.append("?, ");
					args.add(new Timestamp((long) value));
				}
			} else {
				fields.append('`').append(fieldName).append("`,");
				values.append("?, ");
				args.add(value);
			}
		}
		int tmpIndex = fields.lastIndexOf(",");
		insertSql.append(fields.substring(0, tmpIndex)).append(") values(");
		tmpIndex = values.lastIndexOf(",");
		insertSql.append(values.substring(0, tmpIndex)).append(")");
		String sql = insertSql.toString();
		return update(sql, args);
	}

	/**
	 * 
	 * @param message
	 * @param conditionFields
	 * @param conditionParams
	 * @return
	 */
	protected int updateMessageByCondition(M message, String[] conditionFields,
			Object[] conditionParams) {
		return updateMessageByCondition(message, conditionFields,
				conditionParams, this.tableName);
	}

	/**
	 * 
	 * @param message
	 * @param conditionFields
	 * @param conditionParams
	 * @param tableName
	 * @return
	 */
	protected int updateMessageByCondition(M message, String[] conditionFields,
			Object[] conditionParams, String tableName) {
		StringBuilder updateSql = new StringBuilder("update ");
		updateSql.append(tableName).append(" set ");
		StringBuilder options = new StringBuilder("");
		List<Object> args = new ArrayList<Object>();
		Map<FieldDescriptor, Object> fieldMap = message.getAllFields();

		for (Entry<FieldDescriptor, Object> entry : fieldMap.entrySet()) {
			FieldDescriptor fieldDescriptor = entry.getKey();
			if (!Arrays.asList(conditionFields).contains(
					fieldDescriptor.getName())) {
				FieldOptions fieldOptions = fieldDescriptor.getOptions();
				ColumnFieldOption columnFieldOption = fieldOptions
						.getExtension(Taglib.columnOption);
				String fieldName = fieldDescriptor.getName();
				Object value = entry.getValue();
				if (columnFieldOption.getColumnType() == ColumnType.DATETIME
						|| columnFieldOption.getColumnType() == ColumnType.TIMESTAMP) {// datetime类型
					if (value != null && (long) value > 0) {
						options.append(fieldName).append("=?, ");
						args.add(new Timestamp((long) value));
					}
				} else {
					options.append(fieldName).append("=?, ");
					args.add(value);
				}
			}
		}
		int tmpIndex = options.lastIndexOf(",");
		updateSql.append(options.substring(0, tmpIndex)).append(" where 1=1 ");
		StringBuilder condition = new StringBuilder();
		if (conditionFields.length != conditionParams.length) {
			throw new IllegalArgumentException("condition error");
		} else {
			for (int i = 0; i < conditionFields.length; i++) {
				condition.append("AND ").append(conditionFields[i])
						.append("=? ");
				args.add(conditionParams[i]);
			}
			updateSql.append(condition);
			String sql = updateSql.toString();
			logger.debug(sql);
			return update(sql, args);
		}
	}

	/**
	 * 获取查询的属性
	 * 
	 * @return
	 */
	protected String buildSelectStatement() {
		StringBuilder statement = new StringBuilder();
		List<FieldDescriptor> fields = descriptor.getFields();
		for (int i = 0; i < fields.size(); i++) {
			if (i == fields.size() - 1) {
				statement.append(fields.get(i).getName());
			} else {
				statement.append(fields.get(i).getName()).append(",");
			}
		}
		return statement.toString();
	}

	/**
	 * get Builder from messageClass
	 *
	 * @param messageClass
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	public static <T extends Message> T.Builder newBuilder(Class<T> messageClass) {
		T.Builder builder = null;
		try {
			builder = (T.Builder) MethodUtils.invokeStaticMethod(messageClass,
					"newBuilder");
		} catch (NoSuchMethodException | IllegalAccessException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return builder;
	}
}
