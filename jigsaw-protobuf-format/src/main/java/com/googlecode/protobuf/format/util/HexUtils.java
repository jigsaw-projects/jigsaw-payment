/* 
	Copyright (c) 2009, protobuf-java-format
	All rights reserved.

	Redistribution and use in source and binary forms, with or without modification, 
	are permitted provided that the following conditions are met:

		* Redistributions of source code must retain the above copyright notice, 
		  this list of conditions and the following disclaimer.
		* Redistributions in binary form must reproduce the above copyright notice, 
		  this list of conditions and the following disclaimer in the documentation 
		  and/or other materials provided with the distribution.
		* Neither the name of the protobuf-java-format nor the names of its contributors 
		  may be used to endorse or promote products derived from this software 
		  without specific prior written permission.

	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
	"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
	LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
	A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
	OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
	SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
	LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
	DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
	THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
	OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.googlecode.protobuf.format.util;

import java.io.UnsupportedEncodingException;

/**
 * Provide hex utility for converting bytes to hex string
 * <p>
 * (c) 2009-10 protobuf-java-format. All Rights Reserved.
 *
 * @author eliran.bivas@gmail.com Eliran Bivas
 *         <p>
 *         Based on the original code by:
 * 		   http://rgagnon.com/javadetails/java-0596.html
 */
public final class HexUtils {

	static final byte[] HEX_CHARS = { (byte) '0', (byte) '1', (byte) '2',
			(byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
			(byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c',
			(byte) 'd', (byte) 'e', (byte) 'f' };
	
	public static String getHexString(byte raw, int minLength) {
		byte[] hex = new byte[2];
		int index = 0;
		int v = raw & 0xFF;
		hex[index++] = HEX_CHARS[v >>> 4];
		hex[index++] = HEX_CHARS[v & 0xF];
		try {
			String hexString = new String(hex, "ASCII");
			StringBuilder builder = new StringBuilder();
			if ( hexString.length() < minLength) {
				int hexLength = minLength - hexString.length();
				while ( hexLength > 0) {
					builder.append('0');
					hexLength--;
				}
			}
			return builder.append(hexString).toString();
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

}
