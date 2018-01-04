package com.googlecode.protobuf.format;
/* 
    Copyright (c) 2009, Orbitz World Wide
    All rights reserved.

    Redistribution and use in source and binary forms, with or without modification, 
    are permitted provided that the following conditions are met:

        * Redistributions of source code must retain the above copyright notice, 
          this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright notice, 
          this list of conditions and the following disclaimer in the documentation 
          and/or other materials provided with the distribution.
        * Neither the name of the Orbitz World Wide nor the names of its contributors 
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


import static com.googlecode.protobuf.format.util.TextUtils.digitValue;
import static com.googlecode.protobuf.format.util.TextUtils.isHex;
import static com.googlecode.protobuf.format.util.TextUtils.isOctal;
import static com.googlecode.protobuf.format.util.TextUtils.unsignedToString;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;

/**
 * Provide ascii text parsing and formatting support for proto2 instances. The implementation
 * largely follows google/protobuf/text_format.cc.
 * <p>
 * (c) 2009-10 Orbitz World Wide. All Rights Reserved.
 *
 * @author eliran.bivas@gmail.com Eliran Bivas
 * @author aantonov@orbitz.com Alex Antonov
 *         <p>
 *         Based on the original code by:
 * @author wenboz@google.com Wenbo Zhu
 * @author kenton@google.com Kenton Varda
 */
public final class XmlFormat extends AbstractCharBasedFormatter {

    /**
     * Outputs a textual representation of the Protocol Message supplied into the parameter output.
     * (This representation is the new version of the classic "ProtocolPrinter" output from the
     * original Protocol Buffer system)
     */
    public void print(final Message message, Appendable output) throws IOException {
        XmlGenerator generator = new XmlGenerator(output);
        final String messageName = message.getDescriptorForType().getName();
        generator.print("<");
        generator.print(messageName);
        generator.print(">");
        print(message, generator);
        generator.print("</");
        generator.print(messageName);
        generator.print(">");
    }

    /**
     * Outputs a textual representation of {@code fields} to {@code output}.
     */
    public void print(final UnknownFieldSet fields, Appendable output) throws IOException {
        XmlGenerator generator = new XmlGenerator(output);
        generator.print("<message>");
        printUnknownFields(fields, generator);
        generator.print("</message>");
    }


    private void print(Message message, XmlGenerator generator) throws IOException {

        for (Map.Entry<FieldDescriptor, Object> field : message.getAllFields().entrySet()) {
            printField(field.getKey(), field.getValue(), generator);
        }
        printUnknownFields(message.getUnknownFields(), generator);
    }

    public void printField(FieldDescriptor field, Object value, XmlGenerator generator) throws IOException {

        if (field.isRepeated()) {
            // Repeated field. Print each element.
            for (Object element : (List<?>) value) {
                printSingleField(field, element, generator);
            }
        } else {
            printSingleField(field, value, generator);
        }
    }

    private void printSingleField(FieldDescriptor field, Object value, XmlGenerator generator) throws IOException {
        if (field.isExtension()) {
            generator.print("<extension type=\"");
            // We special-case MessageSet elements for compatibility with
            // proto1.
            if (field.getContainingType().getOptions().getMessageSetWireFormat()
                && (field.getType() == FieldDescriptor.Type.MESSAGE) && (field.isOptional())
                // object equality
                && (field.getExtensionScope() == field.getMessageType())) {
                generator.print(field.getMessageType().getFullName());
            } else {
                generator.print(field.getFullName());
            }
            generator.print("\">");
        } else {
            generator.print("<");
            if (field.getType() == FieldDescriptor.Type.GROUP) {
                // Groups must be serialized with their original capitalization.
                generator.print(field.getMessageType().getName());
            } else {
                generator.print(field.getName());
            }
            generator.print(">");
        }

        printFieldValue(field, value, generator);

        if (!field.isExtension()) {
            generator.print("</");
            if (field.getType() == FieldDescriptor.Type.GROUP) {
                // Groups must be serialized with their original capitalization.
                generator.print(field.getMessageType().getName());
            } else {
                generator.print(field.getName());
            }
            generator.print(">");
        } else {
            generator.print("</extension>");
        }

    }

    private void printFieldValue(FieldDescriptor field, Object value, XmlGenerator generator) throws IOException {
        switch (field.getType()) {
            case INT32:
            case INT64:
            case SINT32:
            case SINT64:
            case SFIXED32:
            case SFIXED64:
            case FLOAT:
            case DOUBLE:
            case BOOL:
                // Good old toString() does what we want for these types.
                generator.print(value.toString());
                break;

            case UINT32:
            case FIXED32:
                generator.print(unsignedToString((Integer) value));
                break;

            case UINT64:
            case FIXED64:
                generator.print(unsignedToString((Long) value));
                break;

            case STRING:
                generator.print(escapeText((String) value));
                break;

            case BYTES: {
                generator.print(escapeBytes((ByteString) value));
                break;
            }

            case ENUM: {
                generator.print(((EnumValueDescriptor) value).getName());
                break;
            }

            case MESSAGE:
            case GROUP:
                print((Message) value, generator);
                break;
        }
    }

    private void printUnknownFields(UnknownFieldSet unknownFields, XmlGenerator generator) throws IOException {
        for (Map.Entry<Integer, UnknownFieldSet.Field> entry : unknownFields.asMap().entrySet()) {
            UnknownFieldSet.Field field = entry.getValue();

            final String key = entry.getKey().toString();
            for (long value : field.getVarintList()) {
                printUnknownField(key, unsignedToString(value), generator);
            }
            for (int value : field.getFixed32List()) {
                printUnknownField(key, String.format((Locale) null, "0x%08x", value), generator);
            }
            for (long value : field.getFixed64List()) {
                printUnknownField(key, String.format((Locale) null, "0x%016x", value), generator);
            }
            for (ByteString value : field.getLengthDelimitedList()) {
                printUnknownField(key, escapeBytes(value), generator);
            }
            for (UnknownFieldSet value : field.getGroupList()) {
                generator.print("<unknown-field index=\"");
                generator.print(key);
                generator.print("\">");
                printUnknownFields(value, generator);
                generator.print("</unknown-field>");
            }
        }
    }

    private void printUnknownField(CharSequence fieldKey,
                                          CharSequence fieldValue,
                                          XmlGenerator generator) throws IOException {
        generator.print("<unknown-field index=\"");
        generator.print(fieldKey);
        generator.print("\">");
        generator.print(fieldValue);
        generator.print("</unknown-field>");
    }

   

    /**
     * An inner class for writing text to the output stream.
     */
    private static final class XmlGenerator {

        Appendable output;

        public XmlGenerator(Appendable output) {
            this.output = output;
        }

        /**
         * Print text to the output stream.
         */
        public void print(CharSequence text) throws IOException {
            int size = text.length();
            int pos = 0;

            write(text.subSequence(pos, size), size - pos);
        }

        private void write(CharSequence data, int size) throws IOException {
            if (size == 0) {
                return;
            }
            output.append(data);
        }
    }

    // =================================================================
    // Parsing

    /**
     * Represents a stream of tokens parsed from a {@code String}.
     * <p>
     * <p>
     * The Java standard library provides many classes that you might think would be useful for
     * implementing this, but aren't. For example:
     * <p>
     * <ul>
     * <li>{@code java.io.StreamTokenizer}: This almost does what we want -- or, at least, something
     * that would get us close to what we want -- except for one fatal flaw: It automatically
     * un-escapes strings using Java escape sequences, which do not include all the escape sequences
     * we need to support (e.g. '\x').
     * <li>{@code java.util.Scanner}: This seems like a great way at least to parse regular
     * expressions out of a stream (so we wouldn't have to load the entire input into a single
     * string before parsing). Sadly, {@code Scanner} requires that tokens be delimited with some
     * delimiter. Thus, although the text "foo:" should parse to two tokens ("foo" and ":"), {@code
     * Scanner} would recognize it only as a single token. Furthermore, {@code Scanner} provides no
     * way to inspect the contents of delimiters, making it impossible to keep track of line and
     * column numbers.
     * </ul>
     * <p>
     * <p>
     * Luckily, Java's regular expression support does manage to be useful to us. (Barely: We need
     * {@code Matcher.usePattern()}, which is new in Java 1.5.) So, we can use that, at least.
     * Unfortunately, this implies that we need to have the entire input in one contiguous string.
     */
    private static final class Tokenizer {

        private final CharSequence text;
        private final Matcher matcher;
        private String currentToken;

        // The character index within this.text at which the current token begins.
        private int pos = 0;

        // The line and column numbers of the current token.
        private int line = 0;
        private int column = 0;

        // The line and column numbers of the previous token (allows throwing
        // errors *after* consuming).
        private int previousLine = 0;
        private int previousColumn = 0;

        // We use possesive quantifiers (*+ and ++) because otherwise the Java
        // regex matcher has stack overflows on large inputs.
        private static final Pattern WHITESPACE =
          Pattern.compile("(\\s|(#.*$))++", Pattern.MULTILINE);
        private static final Pattern TOKEN = Pattern.compile(
          "extension|" + "[a-zA-Z_\\s;@][0-9a-zA-Z_\\s;@+-]*+|" +        // an identifier with special handling for 'extension'
          "[.]?[0-9+-][0-9a-zA-Z_.+-]*+|" +             // a number
          "</|" +                                       // an '</' closing element marker
          "[\\\\0-9]++|" +                              // a \000 byte sequence for bytes handling
          "\"([^\"\n\\\\]|\\\\.)*+(\"|\\\\?$)|" +       // a double-quoted string
          "\'([^\'\n\\\\]|\\\\.)*+(\'|\\\\?$)",         // a single-quoted string
          Pattern.MULTILINE);

        private static Pattern DOUBLE_INFINITY = Pattern.compile("-?inf(inity)?",
                                                                 Pattern.CASE_INSENSITIVE);
        private static Pattern FLOAT_INFINITY = Pattern.compile("-?inf(inity)?f?",
                                                                Pattern.CASE_INSENSITIVE);
        private static Pattern FLOAT_NAN = Pattern.compile("nanf?", Pattern.CASE_INSENSITIVE);

        /**
         * Construct a tokenizer that parses tokens from the given text.
         */
        public Tokenizer(CharSequence text) {
            this.text = text;
            matcher = WHITESPACE.matcher(text);
            skipWhitespace();
            nextToken();
        }

        /**
         * Are we at the end of the input?
         */
        public boolean atEnd() {
            return currentToken.length() == 0;
        }

        /**
         * Advance to the next token.
         */
        public void nextToken() {
            previousLine = line;
            previousColumn = column;

            // Advance the line counter to the current position.
            while (pos < matcher.regionStart()) {
                if (text.charAt(pos) == '\n') {
                    ++line;
                    column = 0;
                } else {
                    ++column;
                }
                ++pos;
            }

            // Match the next token.
            if (matcher.regionStart() == matcher.regionEnd()) {
                // EOF
                currentToken = "";
            } else {
                matcher.usePattern(TOKEN);
                if (matcher.lookingAt()) {
                    currentToken = matcher.group();
                    matcher.region(matcher.end(), matcher.regionEnd());
                } else {
                    // Take one character.
                    currentToken = String.valueOf(text.charAt(pos));
                    matcher.region(pos + 1, matcher.regionEnd());
                }

                skipWhitespace();
            }
        }

        /**
         * Skip over any whitespace so that the matcher region starts at the next token.
         */
        private void skipWhitespace() {
            matcher.usePattern(WHITESPACE);
            if (matcher.lookingAt()) {
                matcher.region(matcher.end(), matcher.regionEnd());
            }
        }

        /**
         * If the next token exactly matches {@code token}, consume it and return {@code true}.
         * Otherwise, return {@code false} without doing anything.
         */
        public boolean tryConsume(String token) {
            if (currentToken.equals(token)) {
                nextToken();
                return true;
            } else {
                return false;
            }
        }

        /**
         * If the next token exactly matches {@code token}, consume it. Otherwise, throw a
         * {@link ParseException}.
         */
        public void consume(String token) throws ParseException {
            if (!tryConsume(token)) {
                throw parseException("Expected \"" + token + "\".");
            }
        }

        /**
         * Returns {@code true} if the next token is an integer, but does not consume it.
         */
        public boolean lookingAtInteger() {
            if (currentToken.length() == 0) {
                return false;
            }

            char c = currentToken.charAt(0);
            return (('0' <= c) && (c <= '9')) || (c == '-') || (c == '+');
        }

        /**
         * If the next token is an identifier, consume it and return its value. Otherwise, throw a
         * {@link ParseException}.
         */
        public String consumeIdentifier() throws ParseException {
            for (int i = 0; i < currentToken.length(); i++) {
                char c = currentToken.charAt(i);
                if ((('a' <= c) && (c <= 'z')) || (('A' <= c) && (c <= 'Z'))
                    || (('0' <= c) && (c <= '9')) || (c == '_') || (c == '.') || (c == '"')) {
                    // OK
                } else {
                    throw parseException("Expected identifier. -" + c);
                }
            }

            String result = currentToken;
            // Need to clean-up result to remove quotes of any kind
            result = result.replaceAll("\"|'", "");
            nextToken();
            return result;
        }

        /**
         * If the next token is a 32-bit signed integer, consume it and return its value. Otherwise,
         * throw a {@link ParseException}.
         */
        public int consumeInt32() throws ParseException {
            try {
                int result = parseInt32(currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw integerParseException(e);
            }
        }

        /**
         * If the next token is a 32-bit unsigned integer, consume it and return its value.
         * Otherwise, throw a {@link ParseException}.
         */
        public int consumeUInt32() throws ParseException {
            try {
                int result = parseUInt32(currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw integerParseException(e);
            }
        }

        /**
         * If the next token is a 64-bit signed integer, consume it and return its value. Otherwise,
         * throw a {@link ParseException}.
         */
        public long consumeInt64() throws ParseException {
            try {
                long result = parseInt64(currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw integerParseException(e);
            }
        }

        /**
         * If the next token is a 64-bit unsigned integer, consume it and return its value.
         * Otherwise, throw a {@link ParseException}.
         */
        public long consumeUInt64() throws ParseException {
            try {
                long result = parseUInt64(currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw integerParseException(e);
            }
        }

        /**
         * If the next token is a double, consume it and return its value. Otherwise, throw a
         * {@link ParseException}.
         */
        public double consumeDouble() throws ParseException {
            // We need to parse infinity and nan separately because
            // Double.parseDouble() does not accept "inf", "infinity", or "nan".
            if (DOUBLE_INFINITY.matcher(currentToken).matches()) {
                boolean negative = currentToken.startsWith("-");
                nextToken();
                return negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }
            if (currentToken.equalsIgnoreCase("nan")) {
                nextToken();
                return Double.NaN;
            }
            try {
                double result = Double.parseDouble(currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw floatParseException(e);
            }
        }

        /**
         * If the next token is a float, consume it and return its value. Otherwise, throw a
         * {@link ParseException}.
         */
        public float consumeFloat() throws ParseException {
            // We need to parse infinity and nan separately because
            // Float.parseFloat() does not accept "inf", "infinity", or "nan".
            if (FLOAT_INFINITY.matcher(currentToken).matches()) {
                boolean negative = currentToken.startsWith("-");
                nextToken();
                return negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
            }
            if (FLOAT_NAN.matcher(currentToken).matches()) {
                nextToken();
                return Float.NaN;
            }
            try {
                float result = Float.parseFloat(currentToken);
                nextToken();
                return result;
            } catch (NumberFormatException e) {
                throw floatParseException(e);
            }
        }

        /**
         * If the next token is a boolean, consume it and return its value. Otherwise, throw a
         * {@link ParseException}.
         */
        public boolean consumeBoolean() throws ParseException {
            if (currentToken.equals("true")) {
                nextToken();
                return true;
            } else if (currentToken.equals("false")) {
                nextToken();
                return false;
            } else {
                throw parseException("Expected \"true\" or \"false\".");
            }
        }

        /**
         * If the next token is a string, consume it and return its (unescaped) value. Otherwise,
         * throw a {@link ParseException}.
         */
        public String consumeString() throws ParseException {
            return consumeByteString().toStringUtf8();
        }

        /**
         * If the next token is a string, consume it, unescape it as a
         * {@link com.googlecode.protobuf.format.ByteString}, and return it. Otherwise, throw a
         * {@link ParseException}.
         */
        public ByteString consumeByteString() throws ParseException {
            // In XML String values inside TEXT node don't need to be wrapped in quotes
            /*char quote = currentToken.length() > 0 ? currentToken.charAt(0) : '\0';
            if ((quote != '\"') && (quote != '\'')) {
                throw parseException("Expected string.");
            }

            if ((currentToken.length() < 2)
                || (currentToken.charAt(currentToken.length() - 1) != quote)) {
                throw parseException("String missing ending quote.");
            }*/

            try {
                String escaped = currentToken; //.substring(1, currentToken.length() - 1);
                ByteString result = unescapeBytes(escaped);
                nextToken();
                return result;
            } catch (InvalidEscapeSequence e) {
                throw parseException(e.getMessage());
            }
        }

        /**
         * Returns a {@link ParseException} with the current line and column numbers in the
         * description, suitable for throwing.
         */
        public ParseException parseException(String description) {
            // Note: People generally prefer one-based line and column numbers.
            return new ParseException((line + 1) + ":" + (column + 1) + ": " + description);
        }

        /**
         * Returns a {@link ParseException} with the line and column numbers of the previous token
         * in the description, suitable for throwing.
         */
        public ParseException parseExceptionPreviousToken(String description) {
            // Note: People generally prefer one-based line and column numbers.
            return new ParseException((previousLine + 1) + ":" + (previousColumn + 1) + ": "
                                      + description);
        }

        /**
         * Constructs an appropriate {@link ParseException} for the given {@code
         * NumberFormatException} when trying to parse an integer.
         */
        private ParseException integerParseException(NumberFormatException e) {
            return parseException("Couldn't parse integer: " + e.getMessage());
        }

        /**
         * Constructs an appropriate {@link ParseException} for the given {@code
         * NumberFormatException} when trying to parse a float or double.
         */
        private ParseException floatParseException(NumberFormatException e) {
            return parseException("Couldn't parse number: " + e.getMessage());
        }
    }

    
    /**
     * Parse a text-format message from {@code input} and merge the contents into {@code builder}.
     * Extensions will be recognized if they are registered in {@code extensionRegistry}.
     */
    public void merge(CharSequence input,
                             ExtensionRegistry extensionRegistry,
                             Message.Builder builder) throws ParseException {
        Tokenizer tokenizer = new Tokenizer(input);

        // Need to first consume the outer object name element
        consumeOpeningElement(tokenizer);

        while (!tokenizer.tryConsume("</")) { // Continue till the object is done
            mergeField(tokenizer, extensionRegistry, builder);
        }

        consumeClosingElement(tokenizer);
    }

    private String consumeOpeningElement(Tokenizer tokenizer) throws ParseException {
        tokenizer.consume("<");
        String openingElement = tokenizer.consumeIdentifier();
        tokenizer.consume(">");
        return openingElement;
    }

    private void consumeClosingElement(Tokenizer tokenizer) throws ParseException {
        tokenizer.tryConsume("</");
        //tokenizer.consume("/");
        tokenizer.nextToken();
        tokenizer.consume(">");
    }

    private String consumeExtensionIdentifier(Tokenizer tokenizer) throws ParseException {
        tokenizer.consume("type");
        tokenizer.consume("=");
        return tokenizer.consumeIdentifier();
    }

    /**
     * Parse a single field from {@code tokenizer} and merge it into {@code builder}. If a ',' is
     * detected after the field ends, the next field will be parsed automatically
     */
    private void mergeField(Tokenizer tokenizer,
                                   ExtensionRegistry extensionRegistry,
                                   Message.Builder builder) throws ParseException {
        FieldDescriptor field;
        Descriptors.Descriptor type = builder.getDescriptorForType();
        ExtensionRegistry.ExtensionInfo extension = null;

        tokenizer.consume("<"); // Needs to happen when the object starts.
                
        if (tokenizer.tryConsume("extension")) {
            // An extension.
            StringBuilder name = new StringBuilder(consumeExtensionIdentifier(tokenizer));
            while (tokenizer.tryConsume(".")) {
                name.append(".");
                name.append(tokenizer.consumeIdentifier());
            }

            extension = extensionRegistry.findExtensionByName(name.toString());

            if (extension == null) {
                throw tokenizer.parseExceptionPreviousToken("Extension \""
                                                            + name
                                                            + "\" not found in the ExtensionRegistry.");
            } else if (extension.descriptor.getContainingType() != type) {
                throw tokenizer.parseExceptionPreviousToken("Extension \"" + name
                                                            + "\" does not extend message type \""
                                                            + type.getFullName() + "\".");
            }

            field = extension.descriptor;
        } else {
            String name = tokenizer.consumeIdentifier();
            field = type.findFieldByName(name);

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
                && !field.getMessageType().getName().equals(name)) {
                field = null;
            }

            if (field == null) {
                throw tokenizer.parseExceptionPreviousToken("Message type \"" + type.getFullName()
                                                            + "\" has no field named \"" + name
                                                            + "\".");
            }
        }

        tokenizer.consume(">");

        Object value = handleValue(tokenizer, extensionRegistry, builder, field, extension);

        if (field.isRepeated()) {
            builder.addRepeatedField(field, value);
        } else {
            builder.setField(field, value);
        }

        // Need to consume the closing field element - </fieldName>
        consumeClosingElement(tokenizer);
    }

    private Object handleValue(Tokenizer tokenizer,
                                    ExtensionRegistry extensionRegistry,
                                    Message.Builder builder,
                                    FieldDescriptor field,
                                    ExtensionRegistry.ExtensionInfo extension) throws ParseException {

        Object value = null;
        if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
            value = handleObject(tokenizer, extensionRegistry, builder, field, extension);
        } else {
            value = handlePrimitive(tokenizer, field);
        }

        return value;
    }

    private Object handlePrimitive(Tokenizer tokenizer, FieldDescriptor field) throws ParseException {
        Object value = null;
        switch (field.getType()) {
            case INT32:
            case SINT32:
            case SFIXED32:
                value = tokenizer.consumeInt32();
                break;

            case INT64:
            case SINT64:
            case SFIXED64:
                value = tokenizer.consumeInt64();
                break;

            case UINT32:
            case FIXED32:
                value = tokenizer.consumeUInt32();
                break;

            case UINT64:
            case FIXED64:
                value = tokenizer.consumeUInt64();
                break;

            case FLOAT:
                value = tokenizer.consumeFloat();
                break;

            case DOUBLE:
                value = tokenizer.consumeDouble();
                break;

            case BOOL:
                value = tokenizer.consumeBoolean();
                break;

            case STRING:
                value = tokenizer.consumeString();
                break;

            case BYTES:
                value = tokenizer.consumeByteString();
                break;

            case ENUM: {
                Descriptors.EnumDescriptor enumType = field.getEnumType();

                if (tokenizer.lookingAtInteger()) {
                    int number = tokenizer.consumeInt32();
                    value = enumType.findValueByNumber(number);
                    if (value == null) {
                        throw tokenizer.parseExceptionPreviousToken("Enum type \""
                                                                    + enumType.getFullName()
                                                                    + "\" has no value with number "
                                                                    + number + ".");
                    }
                } else {
                    String id = tokenizer.consumeIdentifier();
                    value = enumType.findValueByName(id);
                    if (value == null) {
                        throw tokenizer.parseExceptionPreviousToken("Enum type \""
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

    private Object handleObject(Tokenizer tokenizer,
                                       ExtensionRegistry extensionRegistry,
                                       Message.Builder builder,
                                       FieldDescriptor field,
                                       ExtensionRegistry.ExtensionInfo extension) throws ParseException {

        Object value;
        Message.Builder subBuilder;
        if (extension == null) {
            subBuilder = builder.newBuilderForField(field);
        } else {
            subBuilder = extension.defaultInstance.newBuilderForType();
        }

        //tokenizer.consume("<");
        String endToken = "</";

        while (!tokenizer.tryConsume(endToken)) {
            if (tokenizer.atEnd()) {
                throw tokenizer.parseException("Expected \"" + endToken + "\".");
            }
            mergeField(tokenizer, extensionRegistry, subBuilder);
        }

        value = subBuilder.build();
        return value;
    }

    // =================================================================
    // Utility functions
    //
    // Some of these methods are package-private because Descriptors.java uses
    // them.

    /**
     * Escapes bytes in the format used in protocol buffer text format, which is the same as the
     * format used for C string literals. All bytes that are not printable 7-bit ASCII characters
     * are escaped, as well as backslash, single-quote, and double-quote characters. Characters for
     * which no defined short-hand escape sequence is defined will be escaped using 3-digit octal
     * sequences.
     */
    static String escapeBytes(ByteString input) {
        StringBuilder builder = new StringBuilder(input.size());
        for (int i = 0; i < input.size(); i++) {
            byte b = input.byteAt(i);
            switch (b) {
                // Java does not recognize \a or \v, apparently.
                case 0x07:
                    builder.append("\\a");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case 0x0b:
                    builder.append("\\v");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\'':
                    builder.append("\\\'");
                    break;
                case '"':
                    builder.append("\\\"");
                    break;
                default:
                    if (b >= 0x20) {
                        builder.append((char) b);
                    } else {
                        builder.append('\\');
                        builder.append((char) ('0' + ((b >>> 6) & 3)));
                        builder.append((char) ('0' + ((b >>> 3) & 7)));
                        builder.append((char) ('0' + (b & 7)));
                    }
                    break;
            }
        }
        return builder.toString();
    }

    /**
     * Un-escape a byte sequence as escaped using
     * {@link #escapeBytes(com.googlecode.protobuf.format.ByteString)}. Two-digit hex escapes (starting with
     * "\x") are also recognized.
     */
    static ByteString unescapeBytes(CharSequence input) throws InvalidEscapeSequence {
        byte[] result = new byte[input.length()];
        int pos = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '\\') {
                if (i + 1 < input.length()) {
                    ++i;
                    c = input.charAt(i);
                    if (isOctal(c)) {
                        // Octal escape.
                        int code = digitValue(c);
                        if ((i + 1 < input.length()) && isOctal(input.charAt(i + 1))) {
                            ++i;
                            code = code * 8 + digitValue(input.charAt(i));
                        }
                        if ((i + 1 < input.length()) && isOctal(input.charAt(i + 1))) {
                            ++i;
                            code = code * 8 + digitValue(input.charAt(i));
                        }
                        result[pos++] = (byte) code;
                    } else {
                        switch (c) {
                            case 'a':
                                result[pos++] = 0x07;
                                break;
                            case 'b':
                                result[pos++] = '\b';
                                break;
                            case 'f':
                                result[pos++] = '\f';
                                break;
                            case 'n':
                                result[pos++] = '\n';
                                break;
                            case 'r':
                                result[pos++] = '\r';
                                break;
                            case 't':
                                result[pos++] = '\t';
                                break;
                            case 'v':
                                result[pos++] = 0x0b;
                                break;
                            case '\\':
                                result[pos++] = '\\';
                                break;
                            case '\'':
                                result[pos++] = '\'';
                                break;
                            case '"':
                                result[pos++] = '\"';
                                break;

                            case 'x':
                                // hex escape
                                int code = 0;
                                if ((i + 1 < input.length()) && isHex(input.charAt(i + 1))) {
                                    ++i;
                                    code = digitValue(input.charAt(i));
                                } else {
                                    throw new InvalidEscapeSequence("Invalid escape sequence: '\\x' with no digits");
                                }
                                if ((i + 1 < input.length()) && isHex(input.charAt(i + 1))) {
                                    ++i;
                                    code = code * 16 + digitValue(input.charAt(i));
                                }
                                result[pos++] = (byte) code;
                                break;

                            default:
                                throw new InvalidEscapeSequence("Invalid escape sequence: '\\" + c
                                                                + "'");
                        }
                    }
                } else {
                    throw new InvalidEscapeSequence("Invalid escape sequence: '\\' at end of string.");
                }
            } else {
                result[pos++] = (byte) c;
            }
        }

        return ByteString.copyFrom(result, 0, pos);
    }

    /**
     * Thrown by {@link XmlFormat#unescapeBytes} and {@link XmlFormat#unescapeText} when an
     * invalid escape sequence is seen.
     */
    static class InvalidEscapeSequence extends IOException {

        private static final long serialVersionUID = 1L;

        public InvalidEscapeSequence(String description) {
            super(description);
        }
    }

    /**
     * Like {@link #escapeBytes(com.googlecode.protobuf.format.ByteString)}, but escapes a text string.
     * Non-ASCII characters are first encoded as UTF-8, then each byte is escaped individually as a
     * 3-digit octal escape. Yes, it's weird.
     */
    static String escapeText(String input) {
        return escapeBytes(ByteString.copyFromUtf8(input));
    }

    /**
     * Un-escape a text string as escaped using {@link #escapeText(String)}. Two-digit hex escapes
     * (starting with "\x") are also recognized.
     */
    static String unescapeText(String input) throws InvalidEscapeSequence {
        return unescapeBytes(input).toStringUtf8();
    }


    /**
     * Parse a 32-bit signed integer from the text. Unlike the Java standard {@code
     * Integer.parseInt()}, this function recognizes the prefixes "0x" and "0" to signify
     * hexidecimal and octal numbers, respectively.
     */
    static int parseInt32(String text) throws NumberFormatException {
        return (int) parseInteger(text, true, false);
    }

    /**
     * Parse a 32-bit unsigned integer from the text. Unlike the Java standard {@code
     * Integer.parseInt()}, this function recognizes the prefixes "0x" and "0" to signify
     * hexidecimal and octal numbers, respectively. The result is coerced to a (signed) {@code int}
     * when returned since Java has no unsigned integer type.
     */
    static int parseUInt32(String text) throws NumberFormatException {
        return (int) parseInteger(text, false, false);
    }

    /**
     * Parse a 64-bit signed integer from the text. Unlike the Java standard {@code
     * Integer.parseInt()}, this function recognizes the prefixes "0x" and "0" to signify
     * hexidecimal and octal numbers, respectively.
     */
    static long parseInt64(String text) throws NumberFormatException {
        return parseInteger(text, true, true);
    }

    /**
     * Parse a 64-bit unsigned integer from the text. Unlike the Java standard {@code
     * Integer.parseInt()}, this function recognizes the prefixes "0x" and "0" to signify
     * hexidecimal and octal numbers, respectively. The result is coerced to a (signed) {@code long}
     * when returned since Java has no unsigned long type.
     */
    static long parseUInt64(String text) throws NumberFormatException {
        return parseInteger(text, false, true);
    }

    private static long parseInteger(String text, boolean isSigned, boolean isLong) throws NumberFormatException {
        int pos = 0;

        boolean negative = false;
        if (text.startsWith("-", pos)) {
            if (!isSigned) {
                throw new NumberFormatException("Number must be positive: " + text);
            }
            ++pos;
            negative = true;
        }

        int radix = 10;
        if (text.startsWith("0x", pos)) {
            pos += 2;
            radix = 16;
        } else if (text.startsWith("0", pos)) {
            radix = 8;
        }

        String numberText = text.substring(pos);

        long result = 0;
        if (numberText.length() < 16) {
            // Can safely assume no overflow.
            result = Long.parseLong(numberText, radix);
            if (negative) {
                result = -result;
            }

            // Check bounds.
            // No need to check for 64-bit numbers since they'd have to be 16
            // chars
            // or longer to overflow.
            if (!isLong) {
                if (isSigned) {
                    if ((result > Integer.MAX_VALUE) || (result < Integer.MIN_VALUE)) {
                        throw new NumberFormatException("Number out of range for 32-bit signed integer: "
                                                        + text);
                    }
                } else {
                    if ((result >= (1L << 32)) || (result < 0)) {
                        throw new NumberFormatException("Number out of range for 32-bit unsigned integer: "
                                                        + text);
                    }
                }
            }
        } else {
            BigInteger bigValue = new BigInteger(numberText, radix);
            if (negative) {
                bigValue = bigValue.negate();
            }

            // Check bounds.
            if (!isLong) {
                if (isSigned) {
                    if (bigValue.bitLength() > 31) {
                        throw new NumberFormatException("Number out of range for 32-bit signed integer: "
                                                        + text);
                    }
                } else {
                    if (bigValue.bitLength() > 32) {
                        throw new NumberFormatException("Number out of range for 32-bit unsigned integer: "
                                                        + text);
                    }
                }
            } else {
                if (isSigned) {
                    if (bigValue.bitLength() > 63) {
                        throw new NumberFormatException("Number out of range for 64-bit signed integer: "
                                                        + text);
                    }
                } else {
                    if (bigValue.bitLength() > 64) {
                        throw new NumberFormatException("Number out of range for 64-bit unsigned integer: "
                                                        + text);
                    }
                }
            }

            result = bigValue.longValue();
        }

        return result;
    }
}
