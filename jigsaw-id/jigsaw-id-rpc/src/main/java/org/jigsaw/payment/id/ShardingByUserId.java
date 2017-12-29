/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jigsaw.payment.id;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月22日
 */
public class ShardingByUserId {
	
	
	public static class Builder{
		// 数据库分片的最大数；
		private int maxShardingDatabaseCount = 128;

		// 每个分片的表个数
		private int shardingTableCount = 10;
		
		public Builder maxShardingDatabaseCount(int count){
			this.maxShardingDatabaseCount= count;
			return this;	
		}
		public Builder shardingTableCount(int count){
			this.shardingTableCount = count;
			return this;
		}
		public ShardingByUserId build(){
			return new ShardingByUserId(this);
		}
	}
	
	private Builder builder;
	
	private ShardingByUserId(Builder builder){
		this.builder = builder;
	}
	
	public static Builder newBuilder(){
		return new Builder();
	}
	
	public int getDatabaseIndex(long userId){
		return (int)(userId / builder.shardingTableCount
				% builder.maxShardingDatabaseCount);
	}
	
	public int getTableIndex(long userId){
		return (int)(userId % builder.shardingTableCount);
	}

	public int getMaxShardingDatabaseCount() {
		return builder.maxShardingDatabaseCount;
	}

	public int getShardingTableCount() {
		return builder.shardingTableCount;
	}	
	
	
}
