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


import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import static com.googlecode.protobuf.format.util.TextUtils.*;


/**
 * Provide ascii html formatting support for proto2 instances.
 * <p>
 * (c) 2009-10 Orbitz World Wide. All Rights Reserved.
 * 
 * @author eliran.bivas@gmail.com Eliran Bivas
 * @version $HtmlFormat.java Mar 12, 2009 4:00:33 PM$
 */
public final class HtmlFormat extends AbstractCharBasedFormatter {

    private static final String META_CONTENT = "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />";
    private static final String MAIN_DIV_STYLE = "color: black; font-size: 14px; font-family: sans-serif; font-weight: bolder; margin-bottom: 10px;";
    private static final String FIELD_NAME_STYLE = "font-weight: bold; color: #669966;font-size: 14px; font-family: sans-serif;";
    private static final String FIELD_VALUE_STYLE = "color: #3300FF;font-size: 13px; font-family: sans-serif;";

    
    public void print(final Message message, Appendable output) throws IOException {
    	HtmlGenerator generator = new HtmlGenerator(output);
        printTitle(message, generator);
        print(message, generator);
        generator.print("</body></html>");
    }
    
	public void print(final UnknownFieldSet fields, Appendable output) throws IOException {
		HtmlGenerator generator = new HtmlGenerator(output);
        generator.print("<html>");
        generator.print(META_CONTENT);
        generator.print("</head><body>");
        printUnknownFields(fields, generator);
        generator.print("</body></html>");
	}
	
	@Override
	public void merge(CharSequence input, ExtensionRegistry extensionRegistry,
			Builder builder) throws IOException {
		throw new UnsupportedOperationException();
	}
    

    private void printTitle(final Message message, final HtmlGenerator generator) throws IOException {
        generator.print("<html><head>");
        generator.print(META_CONTENT);
        generator.print("<title>");
        generator.print(message.getDescriptorForType().getFullName());
        generator.print("</title></head><body>");
        generator.print("<div style=\"");
        generator.print(MAIN_DIV_STYLE);
        generator.print("\">message : ");
        generator.print(message.getDescriptorForType().getFullName());
        generator.print("</div>");
    }


    private void print(Message message, HtmlGenerator generator) throws IOException {

        for (Map.Entry<FieldDescriptor, Object> field : message.getAllFields().entrySet()) {
            printField(field.getKey(), field.getValue(), generator);
        }
        printUnknownFields(message.getUnknownFields(), generator);
    }

    public void printField(FieldDescriptor field, Object value, HtmlGenerator generator) throws IOException {

        if (field.isRepeated()) {
            // Repeated field. Print each element.
            for (Object element : (List<?>) value) {
                printSingleField(field, element, generator);
            }
        } else {
            printSingleField(field, value, generator);
        }
    }

    private void printSingleField(FieldDescriptor field,
                                         Object value,
                                         HtmlGenerator generator) throws IOException {
        if (field.isExtension()) {
            generator.print("[<span style=\"");
            generator.print(FIELD_NAME_STYLE);
            generator.print("\">");
            // We special-case MessageSet elements for compatibility with proto1.
            if (field.getContainingType().getOptions().getMessageSetWireFormat()
                            && (field.getType() == FieldDescriptor.Type.MESSAGE) && (field.isOptional())
                            // object equality
                            && (field.getExtensionScope() == field.getMessageType())) {
                generator.print(field.getMessageType().getFullName());
            } else {
                generator.print(field.getFullName());
            }
            generator.print("</span>]");
        } else {
            generator.print("<span style=\"");
            generator.print(FIELD_NAME_STYLE);
            generator.print("\">");
            if (field.getType() == FieldDescriptor.Type.GROUP) {
                // Groups must be serialized with their original capitalization.
                generator.print(field.getMessageType().getName());
            } else {
                generator.print(field.getName());
            }
            generator.print("</span>");
        }

        if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
            generator.print(" <span style=\"color: red;\">{</span><br/>");
            generator.indent();
        } else {
            generator.print(": ");
        }

        printFieldValue(field, value, generator);

        if (field.getJavaType() == FieldDescriptor.JavaType.MESSAGE) {
            generator.outdent();
            generator.print("<span style=\"color: red;\">}</span>");
        }
        generator.print("<br/>");
    }

    private void printFieldValue(FieldDescriptor field, Object value, HtmlGenerator generator) throws IOException {
        generator.print("<span style=\"");
        generator.print(FIELD_VALUE_STYLE);
        generator.print("\">");
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
                generator.print("\"");
                generator.print(value.toString());
                generator.print("\"");
                break;

            case BYTES: {
                generator.print("\"");
                generator.print(escapeBytes((ByteString) value));
                generator.print("\"");
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
        generator.print("</span>");
    }

    private void printUnknownFields(UnknownFieldSet unknownFields, HtmlGenerator generator) throws IOException {
        for (Map.Entry<Integer, UnknownFieldSet.Field> entry : unknownFields.asMap().entrySet()) {
            UnknownFieldSet.Field field = entry.getValue();

            for (long value : field.getVarintList()) {
                generator.print(entry.getKey().toString());
                generator.print(": ");
                generator.print(unsignedToString(value));
                generator.print("<br/>");
            }
            for (int value : field.getFixed32List()) {
                generator.print(entry.getKey().toString());
                generator.print(": ");
                generator.print(String.format((Locale) null, "0x%08x", value));
                generator.print("<br/>");
            }
            for (long value : field.getFixed64List()) {
                generator.print(entry.getKey().toString());
                generator.print(": ");
                generator.print(String.format((Locale) null, "0x%016x", value));
                generator.print("<br/>");
            }
            for (ByteString value : field.getLengthDelimitedList()) {
                generator.print(entry.getKey().toString());
                generator.print(": \"");
                generator.print(escapeBytes(value));
                generator.print("\"<br/>");
            }
            for (UnknownFieldSet value : field.getGroupList()) {
                generator.print(entry.getKey().toString());
                generator.print(" <span style=\"color: red;\">{</span><br/>");
                generator.indent();
                printUnknownFields(value, generator);
                generator.outdent();
                generator.print("<span style=\"color: red;\">}</span><br/>");
            }
        }
    }

    

    /**
     * An inner class for writing text to the output stream.
     */
    static private final class HtmlGenerator {

        Appendable output;
        boolean atStartOfLine = true;

        public HtmlGenerator(Appendable output) {
            this.output = output;
        }

        /**
         * Indent text by two spaces. After calling Indent(), two spaces will be inserted at the
         * beginning of each line of text. Indent() may be called multiple times to produce deeper
         * indents.
         * 
         * @throws IOException
         */
        public void indent() throws IOException {
            print("<div style=\"margin-left: 25px\">");
        }

        /**
         * Reduces the current indent level by two spaces, or crashes if the indent level is zero.
         * 
         * @throws IOException
         */
        public void outdent() throws IOException {
            print("</div>");
        }

        /**
         * Print text to the output stream.
         */
        public void print(CharSequence text) throws IOException {
            int size = text.length();
            int pos = 0;

            for (int i = 0; i < size; i++) {
                if (text.charAt(i) == '\n') {
                    write("<br/>", i - pos + 1);
                    pos = i + 1;
                    atStartOfLine = true;
                }
            }
            write(text.subSequence(pos, size), size - pos);
        }

        private void write(CharSequence data, int size) throws IOException {
            if (size == 0) {
                return;
            }
            if (atStartOfLine) {
                atStartOfLine = false;
            }
            output.append(data);
        }
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
     * Thrown by {@link JsonFormat#unescapeBytes} and {@link JsonFormat#unescapeText} when an
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
            // No need to check for 64-bit numbers since they'd have to be 16 chars
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