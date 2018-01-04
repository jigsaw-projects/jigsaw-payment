package com.googlecode.protobuf.format;
/* 
   Copyright (c) 2011 NeuStar, Inc. All Rights Reserved.

   Redistribution and use in source and binary forms, with or without modification, 
    are permitted provided that the following conditions are met:

        * Redistributions of source code must retain the above copyright notice, 
          this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright notice, 
          this list of conditions and the following disclaimer in the documentation 
          and/or other materials provided with the distribution.
        * Neither the name of the NeuStar, Inc. nor the names of its contributors 
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


import static com.googlecode.protobuf.format.util.TextUtils.unsignedLong;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;
import com.googlecode.protobuf.format.util.TextUtils;

/**
 * Provide ascii text parsing and formatting support for proto2 instances. The implementation
 * largely follows google/protobuf/text_format.cc.
 * <p>
 * (c) 2011 Neustar, Inc. All Rights Reserved.
 *
 * @author jeffrey.damick@neustar.biz Jeffrey Damick
 *         Based on the original code by:
 * @author eliran.bivas@gmail.com Eliran Bivas
 * @author aantonov@orbitz.com Alex Antonov
 *         <p>
 * @author wenboz@google.com Wenbo Zhu
 * @author kenton@google.com Kenton Varda
 */
public class JsonJacksonFormat extends ProtobufFormatter {
    private static JsonFactory jsonFactory = new JsonFactory();
    private static final long MAX_UINT_VALUE = (((long) Integer.MAX_VALUE) << 1) + 1;
    private static final BigInteger MAX_ULONG_VALUE =
            BigInteger.valueOf(Long.MAX_VALUE).shiftLeft(1).add(BigInteger.ONE);

    /**
     * Outputs a Smile representation of the Protocol Message supplied into the parameter output.
     * (This representation is the new version of the classic "ProtocolPrinter" output from the
     * original Protocol Buffer system)
     */
    public void print(final Message message, OutputStream output, Charset cs) throws IOException {
        JsonGenerator generator = createGenerator(output);
    	print(message, generator);
    	generator.close();
    }

    /**
     * Outputs a Smile representation of the Protocol Message supplied into the parameter output.
     * (This representation is the new version of the classic "ProtocolPrinter" output from the
     * original Protocol Buffer system)
     */
    public void print(Message message, JsonGenerator generator) throws IOException {
    	generator.writeStartObject();
    	printMessage(message, generator);
        generator.writeEndObject();
        generator.flush();
    }

    /**
     * Outputs a Smile representation of {@code fields} to {@code output}.
     */
    public void print(final UnknownFieldSet fields, OutputStream output, Charset cs) throws IOException {
    	JsonGenerator generator = createGenerator(output);
    	generator.writeStartObject();
    	printUnknownFields(fields, generator);
        generator.writeEndObject();
        generator.close();
    }


    /**
     * Parse a text-format message from {@code input} and merge the contents into {@code builder}.
     * Extensions will be recognized if they are registered in {@code extensionRegistry}.
     * @throws IOException
     */
    public void merge(InputStream input, Charset cs,
    		ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException {

    	JsonParser parser = jsonFactory.createJsonParser(input);
    	merge(parser, extensionRegistry, builder);
    }

    /**
     * Parse a text-format message from {@code input} and merge the contents into {@code builder}.
     * Extensions will be recognized if they are registered in {@code extensionRegistry}.
     * @throws IOException
     */
    public void merge(JsonParser parser,
    						 ExtensionRegistry extensionRegistry,
                             Message.Builder builder) throws IOException {

        JsonToken token = parser.nextToken();
        if (token.equals(JsonToken.START_OBJECT)) {
        	token = parser.nextToken();
        }
        while (token != null && !token.equals(JsonToken.END_OBJECT)) {
        	mergeField(parser, extensionRegistry, builder);
        	token = parser.nextToken();
        }

        // Test to make sure the tokenizer has reached the end of the stream.
        if (parser.nextToken() != null) {
            throw new RuntimeException("Expecting the end of the stream, but there seems to be more data!  Check the input for a valid JSON format.");
        }
    }



    protected JsonGenerator createGenerator(OutputStream output) throws IOException {
    	JsonGenerator generator = jsonFactory.createJsonGenerator(output, JsonEncoding.UTF8);
    	generator.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    	return generator;
    }


    protected void printMessage(Message message, JsonGenerator generator) throws IOException {

        for (Iterator<Map.Entry<FieldDescriptor, Object>> iter = message.getAllFields().entrySet().iterator(); iter.hasNext();) {
            Map.Entry<FieldDescriptor, Object> field = iter.next();
            printField(field.getKey(), field.getValue(), generator);
        }
        printUnknownFields(message.getUnknownFields(), generator);
    }

    public void printField(FieldDescriptor field, Object value, JsonGenerator generator) throws IOException {

        printSingleField(field, value, generator);
    }

    private void printSingleField(FieldDescriptor field,
                                         Object value,
                                         JsonGenerator generator) throws IOException {
        if (field.isExtension()) {
            // We special-case MessageSet elements for compatibility with proto1.
            if (field.getContainingType().getOptions().getMessageSetWireFormat()
                && (field.getType() == FieldDescriptor.Type.MESSAGE) && (field.isOptional())
                // object equality
                && (field.getExtensionScope() == field.getMessageType())) {
                generator.writeFieldName(field.getMessageType().getFullName());
            } else {
            	// extensions will have '.' in them, while normal fields wont..
            	generator.writeFieldName(field.getFullName());
            }
        } else {
            if (field.getType() == FieldDescriptor.Type.GROUP) {
                // Groups must be serialized with their original capitalization.
                generator.writeFieldName(field.getMessageType().getName());
            } else {
                generator.writeFieldName(field.getName());
            }
        }

        // Done with the name, on to the value
        if (field.isRepeated()) {
            // Repeated field. Print each element.
            generator.writeStartArray();
            for (Iterator<?> iter = ((List<?>) value).iterator(); iter.hasNext();) {
                printFieldValue(field, iter.next(), generator);
            }
            generator.writeEndArray();
        } else {
            printFieldValue(field, value, generator);
        }
    }

    private void printFieldValue(FieldDescriptor field, Object value, JsonGenerator generator) throws IOException {
    	// TODO: look at using field.getType().getJavaType(), to simplify this..
    	switch (field.getType()) {
            case INT32:
            case SINT32:
            case SFIXED32:
            	generator.writeNumber((Integer)value);
            	break;

            case INT64:
            case SINT64:
            case SFIXED64:
            	generator.writeNumber((Long)value);
            	break;

            case FLOAT:
            	generator.writeNumber((Float)value);
            	break;

            case DOUBLE:
            	generator.writeNumber((Double)value);
            	break;

            case BOOL:
                // Good old toString() does what we want for these types.
                generator.writeBoolean((Boolean)value);
                break;

            case UINT32:
            case FIXED32:
                generator.writeNumber(Integer.toUnsignedLong((Integer)value));
                break;

            case UINT64:
            case FIXED64:
                generator.writeNumber(unsignedLong((Long) value));
                break;

            case STRING:
            	generator.writeString((String) value);
                break;

            case BYTES: {
            	// Here we break with JsonFormat - since there is an issue with non-utf8 bytes..
            	generator.writeBinary(((ByteString)value).toByteArray());
                break;
            }

            case ENUM: {
            	generator.writeString(((EnumValueDescriptor) value).getName());
                break;
            }

            case MESSAGE:
            case GROUP:
            	generator.writeStartObject();
                printMessage((Message) value, generator);
                generator.writeEndObject();
                break;
        }
    }

    protected void printUnknownFields(UnknownFieldSet unknownFields, JsonGenerator generator) throws IOException {
        for (Map.Entry<Integer, UnknownFieldSet.Field> entry : unknownFields.asMap().entrySet()) {
            UnknownFieldSet.Field field = entry.getValue();

            generator.writeArrayFieldStart(entry.getKey().toString());
            for (long value : field.getVarintList()) {
                generator.writeNumber(value);
            }
            for (int value : field.getFixed32List()) {
                generator.writeNumber(value);
            }
            for (long value : field.getFixed64List()) {
                generator.writeNumber(value);
            }
            for (ByteString value : field.getLengthDelimitedList()) {
            	// here we break with the JsonFormat to support non-utf8 bytes
            	generator.writeBinary(value.toByteArray());
            }
            for (UnknownFieldSet value : field.getGroupList()) {
                generator.writeStartObject();
                printUnknownFields(value, generator);
                generator.writeEndObject();
            }
            generator.writeEndArray();
        }
    }



    // =================================================================
    // Parsing


    /**
     * Parse a single field from {@code parser} and merge it into {@code builder}. If a ',' is
     * detected after the field ends, the next field will be parsed automatically
     * @throws IOException
     * @throws JsonParseException
     */
    protected void mergeField(JsonParser parser,
                                   ExtensionRegistry extensionRegistry,
                                   Message.Builder builder) throws JsonParseException, IOException {
        FieldDescriptor field = null;
        Descriptor type = builder.getDescriptorForType();
        boolean unknown = false;
        ExtensionRegistry.ExtensionInfo extension = null;
        JsonToken token = parser.getCurrentToken();

        if (token != null) {
            String name = parser.getCurrentName();

            if (name.contains(".")) {
            	// should be an extension
            	extension = extensionRegistry.findExtensionByName(name);
                if (extension == null) {
                    throw new RuntimeException("Extension \""
                    		+ name + "\" not found in the ExtensionRegistry.");
                } else if (extension.descriptor.getContainingType() != type) {
                    throw new RuntimeException("Extension \"" + name
                    		+ "\" does not extend message type \""
                    		+ type.getFullName() + "\".");
                }

            	field = extension.descriptor;
            } else {
            	field = type.findFieldByName(name);
            }

            // Group names are expected to be capitalized as they appear in the
            // .proto file, which actually matches their type names, not their field
            // names.
            if (field == null) {
                // Explicitly specify US locale so that this code does not break when
                // executing in Turkey.
                String lowerName = name.toLowerCase(Locale.US);
                field = type.findFieldByName(lowerName);
                // If the case-insensitive match worked but the field is NOT a group,
                if ((field != null) && (field.getType() != FieldDescriptor.Type.GROUP)) {
                    field = null;
                }
            }
            // Again, special-case group names as described above.
            if ((field != null) && (field.getType() == FieldDescriptor.Type.GROUP)
                && !field.getMessageType().getName().equals(name)
                && !field.getMessageType().getFullName().equalsIgnoreCase(name) /* extension */) {
                field = null;
            }

            // Last try to lookup by field-index if 'name' is numeric,
            // which indicates a possible unknown field
            if (field == null && TextUtils.isDigits(name)) {
                field = type.findFieldByNumber(Integer.parseInt(name));
                unknown = true;
            }

            // no throwing exceptions if field not found, since it could be a different version.
            if (field == null) {
            	UnknownFieldSet.Builder unknownsBuilder = UnknownFieldSet.newBuilder();
            	handleMissingField(name, parser, extensionRegistry, unknownsBuilder);
            	builder.setUnknownFields(unknownsBuilder.build());
            }
        }

        if (field != null) {
        	token = parser.nextToken();

            boolean array = token.equals(JsonToken.START_ARRAY);

            if (array) {
            	token = parser.nextToken();
                while (!token.equals(JsonToken.END_ARRAY)) {
                    handleValue(parser, extensionRegistry, builder, field, extension, unknown);
                    token = parser.nextToken();
                }
            } else {
                handleValue(parser, extensionRegistry, builder, field, extension, unknown);
            }
        }
    }

    private void handleMissingField(String fieldName, JsonParser parser,
                                           ExtensionRegistry extensionRegistry,
                                           UnknownFieldSet.Builder builder) throws IOException {

        JsonToken token = parser.nextToken();
        if (token.equals(JsonToken.START_OBJECT)) {
            // Message structure
        	token = parser.nextToken(); // skip name
        	while (token != null && !token.equals(JsonToken.END_OBJECT)) {
                handleMissingField(fieldName, parser, extensionRegistry, builder);
                token = parser.nextToken(); // get } or field name
            }
        } else if (token.equals(JsonToken.START_ARRAY)) {
            // Collection
            do {
                handleMissingField(fieldName, parser, extensionRegistry, builder);
                token = parser.getCurrentToken(); // got value or ]
            } while (token != null && !token.equals(JsonToken.END_ARRAY));
        } else {
            // Primitive value
        	// NULL, INT, BOOL, STRING
        	// nothing to do..
        }
    }

    private void handleValue(JsonParser parser,
                                    ExtensionRegistry extensionRegistry,
                                    Message.Builder builder,
                                    FieldDescriptor field,
                                    ExtensionRegistry.ExtensionInfo extension,
                                    boolean unknown) throws IOException {

        Object value = null;
        if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
            value = handleObject(parser, extensionRegistry, builder, field, extension, unknown);
        } else {
            value = handlePrimitive(parser, field);
        }
        if (value != null) {
            if (field.isRepeated()) {
                builder.addRepeatedField(field, value);
            } else {
                builder.setField(field, value);
            }
        }
    }

    private Object handlePrimitive(JsonParser parser, FieldDescriptor field) throws IOException {
        Object value = null;

        JsonToken token = parser.getCurrentToken();

        if (token.equals(JsonToken.VALUE_NULL)) {
            return value;
        }

        switch (field.getType()) {
            case INT32:
            case SINT32:
            case SFIXED32:
            	value = parser.getIntValue();
                break;

            case INT64:
            case SINT64:
            case SFIXED64:
            	value = parser.getLongValue();
                break;

            case UINT32:
            case FIXED32:
            	long valueLong = parser.getLongValue();
            	if (valueLong < 0 || valueLong > MAX_UINT_VALUE) {
            		throw new NumberFormatException("Number must be positive: " + valueLong);
            	}
            	value = (int) valueLong;
                break;

            case UINT64:
            case FIXED64:
            	BigInteger valueBigInt = parser.getBigIntegerValue();
                // valueBigInt < 0 || valueBigInt > MAX_ULONG_VALUE
            	if (valueBigInt.compareTo(BigInteger.ZERO) == -1 || valueBigInt.compareTo(MAX_ULONG_VALUE) == 1) {
            		throw new NumberFormatException("Number must be positive: " + valueBigInt);
            	}
            	value = valueBigInt.longValue();
                break;

            case FLOAT:
            	value = parser.getFloatValue();
                break;

            case DOUBLE:
            	value = parser.getDoubleValue();
                break;

            case BOOL:
            	value = parser.getBooleanValue();
                break;

            case STRING:
            	value = parser.getText();
                break;

            case BYTES:
            	value = ByteString.copyFrom(parser.getBinaryValue());
                break;

            case ENUM: {
                EnumDescriptor enumType = field.getEnumType();
                if (token.equals(JsonToken.VALUE_NUMBER_INT)) {
                    int number = parser.getIntValue();
                    value = enumType.findValueByNumber(number);
                    if (value == null) {
                        throw new RuntimeException("Enum type \""
                        		+ enumType.getFullName()
                        		+ "\" has no value with number "
                        		+ number + ".");
                    }
                } else {
                    String id = parser.getText();
                    value = enumType.findValueByName(id);
                    if (value == null) {
                    	throw new RuntimeException("Enum type \""
                    			+ enumType.getFullName()
                    			+ "\" has no value named \""
                    			+ id + "\".");
                    }
                }
                break;
            }

            case MESSAGE:
            case GROUP:
                throw new RuntimeException("Can't get here.");
        }
        return value;
    }


    private Object handleObject(JsonParser parser,
                                       ExtensionRegistry extensionRegistry,
                                       Message.Builder builder,
                                       FieldDescriptor field,
                                       ExtensionRegistry.ExtensionInfo extension,
                                       boolean unknown) throws IOException {

        Message.Builder subBuilder;
        if (extension == null) {
            subBuilder = builder.newBuilderForField(field);
        } else {
            subBuilder = extension.defaultInstance.newBuilderForType();
        }

        JsonToken token = parser.getCurrentToken();
        if (JsonToken.VALUE_NULL == token) {
            return null;
        }

        if (unknown) {
        	ByteString data = ByteString.copyFrom(parser.getBinaryValue());
            try {
                subBuilder.mergeFrom(data);
                return subBuilder.build();
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException("Failed to build " + field.getFullName() + " from " + data);
            }
        }

        //token = parser.nextToken();
        if (token.equals(JsonToken.START_OBJECT)) {
	        token = parser.nextToken();
	        while (token != null && !token.equals(JsonToken.END_OBJECT)) {
	            mergeField(parser, extensionRegistry, subBuilder);
	            token = parser.nextToken();
	        }
        }
        return subBuilder.build();
    }

}
