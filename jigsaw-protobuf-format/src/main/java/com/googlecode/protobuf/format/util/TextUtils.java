/**
 * Copyright 2000-2011 NeuStar, Inc. All rights reserved.
 * NeuStar, the Neustar logo and related names and logos are registered
 * trademarks, service marks or tradenames of NeuStar, Inc. All other
 * product names, company names, marks, logos and symbols may be trademarks
 * of their respective owners.
 */

package com.googlecode.protobuf.format.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import com.google.protobuf.ByteString;

/**
 * Utilities for coercing types
 * largely follows google/protobuf/text_format.cc.
 */
public class TextUtils {
    private static final Pattern DOUBLE_INFINITY = 
            Pattern.compile("-?inf(inity)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern FLOAT_INFINITY = 
          Pattern.compile("-?inf(inity)?f?", Pattern.CASE_INSENSITIVE);
    private static final Pattern FLOAT_NAN = 
          Pattern.compile("nanf?", Pattern.CASE_INSENSITIVE);
    private static final Pattern DIGITS = 
            Pattern.compile("[0-9]", Pattern.CASE_INSENSITIVE);
    
	/**
     * Convert an unsigned 64-bit integer to a string.
     */
	 public static String unsignedToString(final long value) {
        if (value >= 0) {
            return Long.toString(value);
        } else {
            // Pull off the most-significant bit so that BigInteger doesn't think
            // the number is negative, then set it again using setBit().
            return BigInteger.valueOf(value & 0x7FFFFFFFFFFFFFFFL).setBit(63).toString();
        }
    }
	 
    /**
     * Convert an unsigned 32-bit integer to a string.
     */
    public static String unsignedToString(final int value) {
        if (value >= 0) {
            return Integer.toString(value);
        } else {
            return Long.toString((value) & 0x00000000FFFFFFFFL);
        }
    }

    /**
     * Convert an unsigned 64-bit integer to a {@link BigInteger}.
     */
    public static BigInteger unsignedLong(long value) {
        if (value < 0) {
            // Pull off the most-significant bit so that BigInteger doesn't think
            // the number is negative, then set it again using setBit().
            return BigInteger.valueOf(value & 0x7FFFFFFFFFFFFFFFL).setBit(63);
        }
        return BigInteger.valueOf(value);
    }

    /** 
     * Is this a hex digit? 
     */
    public static boolean isHex(final char c) {
      return ('0' <= c && c <= '9') ||
             ('a' <= c && c <= 'f') ||
             ('A' <= c && c <= 'F');
    }
    
    /** 
     * Is this an octal digit? 
     */
    public static boolean isOctal(final char c) {
      return '0' <= c && c <= '7';
    }

    /**
     * Interpret a character as a digit (in any base up to 36) and return the
     * numeric value.  This is like {@code Character.digit()} but we don't accept
     * non-ASCII digits.
     */
    public static int digitValue(final char c) {
      if ('0' <= c && c <= '9') {
        return c - '0';
      } else if ('a' <= c && c <= 'z') {
        return c - 'a' + 10;
      } else {
        return c - 'A' + 10;
      }
    }
    
    public static boolean isDigits(final String text) {
        return DIGITS.matcher(text).matches();
    }
    
    private static final int BUFFER_SIZE = 4096;

    // TODO(chrisn): See if working around java.io.Reader#read(CharBuffer)
    // overhead is worthwhile
    public static StringBuilder toStringBuilder(Readable input) throws IOException {
        StringBuilder text = new StringBuilder();
        CharBuffer buffer = CharBuffer.allocate(BUFFER_SIZE);
        while (true) {
            int n = input.read(buffer);
            if (n == -1) {
                break;
            }
            buffer.flip();
            text.append(buffer, 0, n);
        }
        return text;
    }
    
    public static InputStream toInputStream(String input) {
    	return toInputStream(input, Charset.defaultCharset());
    }
    
    public static InputStream toInputStream(String input, Charset cs) {
        return new ByteArrayInputStream(input.getBytes(cs));
    }
    
    
    
    /**
     * If the next token is a double and return its value.
     * Otherwise, throw a {@link NumberFormatException}.
     */
    public static double parseDouble(final String text) throws NumberFormatException {
      // We need to parse infinity and nan separately because
      // Double.parseDouble() does not accept "inf", "infinity", or "nan".
      if (DOUBLE_INFINITY.matcher(text).matches()) {
        final boolean negative = text.startsWith("-");
        return negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
      }
      if (text.equalsIgnoreCase("nan")) {
        return Double.NaN;
      }

      final double result = Double.parseDouble(text);
      return result;
    }

    /**
     * Parse a float and return its value.
     * Otherwise, throw a {@link NumberFormatException}.
     */
    public static float parseFloat(final String text) throws NumberFormatException {
      // We need to parse infinity and nan separately because
      // Float.parseFloat() does not accept "inf", "infinity", or "nan".
      if (FLOAT_INFINITY.matcher(text).matches()) {
        final boolean negative = text.startsWith("-");
        return negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
      }
      if (FLOAT_NAN.matcher(text).matches()) {
        return Float.NaN;
      }

      final float result = Float.parseFloat(text);
      return result;
    }
    
    /**
     * Parse a boolean and return its value.
     * Otherwise, throw a {@link IllegalArgumentException}.
     */
    public static boolean parseBoolean(final String text) throws IllegalArgumentException {
      if (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("t") ||
              text.equals("1")) {
        return true;
      } else if (text.equalsIgnoreCase("false") || text.equalsIgnoreCase("f") ||
              text.equals("0")) {
        return false;
      } else {
        throw new IllegalArgumentException("Expected \"true\" or \"false\".");
      }
    }

    
    /**
     * Parse a 32-bit signed integer from the text.  Unlike the Java standard
     * {@code Integer.parseInt()}, this function recognizes the prefixes "0x"
     * and "0" to signify hexidecimal and octal numbers, respectively.
     */
    public static int parseInt32(final String text) throws NumberFormatException {
      return (int) parseInteger(text, true, false);
    }

    /**
     * Parse a 32-bit unsigned integer from the text.  Unlike the Java standard
     * {@code Integer.parseInt()}, this function recognizes the prefixes "0x"
     * and "0" to signify hexidecimal and octal numbers, respectively.  The
     * result is coerced to a (signed) {@code int} when returned since Java has
     * no unsigned integer type.
     */
    public static int parseUInt32(final String text) throws NumberFormatException {
      return (int) parseInteger(text, false, false);
    }

    /**
     * Parse a 64-bit signed integer from the text.  Unlike the Java standard
     * {@code Integer.parseInt()}, this function recognizes the prefixes "0x"
     * and "0" to signify hexidecimal and octal numbers, respectively.
     */
    public static long parseInt64(final String text) throws NumberFormatException {
      return parseInteger(text, true, true);
    }

    /**
     * Parse a 64-bit unsigned integer from the text.  Unlike the Java standard
     * {@code Integer.parseInt()}, this function recognizes the prefixes "0x"
     * and "0" to signify hexidecimal and octal numbers, respectively.  The
     * result is coerced to a (signed) {@code long} when returned since Java has
     * no unsigned long type.
     */
    public static long parseUInt64(final String text) throws NumberFormatException {
      return parseInteger(text, false, true);
    }

    public static long parseInteger(final String text,
            final boolean isSigned, final boolean isLong) throws NumberFormatException {
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

      final String numberText = text.substring(pos);

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
            if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
              throw new NumberFormatException(
                "Number out of range for 32-bit signed integer: " + text);
            }
          } else {
            if (result >= (1L << 32) || result < 0) {
              throw new NumberFormatException(
                "Number out of range for 32-bit unsigned integer: " + text);
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
              throw new NumberFormatException(
                "Number out of range for 32-bit signed integer: " + text);
            }
          } else {
            if (bigValue.bitLength() > 32) {
              throw new NumberFormatException(
                "Number out of range for 32-bit unsigned integer: " + text);
            }
          }
        } else {
          if (isSigned) {
            if (bigValue.bitLength() > 63) {
              throw new NumberFormatException(
                "Number out of range for 64-bit signed integer: " + text);
            }
          } else {
            if (bigValue.bitLength() > 64) {
              throw new NumberFormatException(
                "Number out of range for 64-bit unsigned integer: " + text);
            }
          }
        }

        result = bigValue.longValue();
      }

      return result;
    }
    
    /**
     * Escapes bytes in the format used in protocol buffer text format, which is the same as the
     * format used for C string literals. All bytes that are not printable 7-bit ASCII characters
     * are escaped, as well as backslash, single-quote, and double-quote characters. Characters for
     * which no defined short-hand escape sequence is defined will be escaped using 3-digit octal
     * sequences.
     */
    public static String escapeBytes(final ByteString input) {
        final StringBuilder builder = new StringBuilder(input.size());
        for (int i = 0; i < input.size(); i++) {
          final byte b = input.byteAt(i);
          switch (b) {
            // Java does not recognize \a or \v, apparently.
            case 0x07: builder.append("\\a" ); break;
            case '\b': builder.append("\\b" ); break;
            case '\f': builder.append("\\f" ); break;
            case '\n': builder.append("\\n" ); break;
            case '\r': builder.append("\\r" ); break;
            case '\t': builder.append("\\t" ); break;
            case 0x0b: builder.append("\\v" ); break;
            case '\\': builder.append("\\\\"); break;
            case '\'': builder.append("\\\'"); break;
            case '"' : builder.append("\\\""); break;
            default:
              // Note:  Bytes with the high-order bit set should be escaped.  Since
              //   bytes are signed, such bytes will compare less than 0x20, hence
              //   the following line is correct.
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
     * {@link #escapeBytes(ByteString)}.  Two-digit hex escapes (starting with
     * "\x") are also recognized.
     */
    public static ByteString unescapeBytes(final CharSequence charString) {
      // First convert the Java characater sequence to UTF-8 bytes.
      ByteString input = ByteString.copyFromUtf8(charString.toString());
      // Then unescape certain byte sequences introduced by ASCII '\\'.  The valid
      // escapes can all be expressed with ASCII characters, so it is safe to
      // operate on bytes here.
      //
      // Unescaping the input byte array will result in a byte sequence that's no
      // longer than the input.  That's because each escape sequence is between
      // two and four bytes long and stands for a single byte.
      final byte[] result = new byte[input.size()];
      int pos = 0;
      for (int i = 0; i < input.size(); i++) {
        byte c = input.byteAt(i);
        if (c == '\\') {
          if (i + 1 < input.size()) {
            ++i;
            c = input.byteAt(i);
            if (isOctal((char)c)) {
              // Octal escape.
              int code = digitValue((char) c);
              if (i + 1 < input.size() && isOctal((char) input.byteAt(i + 1))) {
                ++i;
                code = code * 8 + digitValue((char) input.byteAt(i));
              }
              if (i + 1 < input.size() && isOctal((char) input.byteAt(i + 1))) {
                ++i;
                code = code * 8 + digitValue((char) input.byteAt(i));
              }
              // TODO: Check that 0 <= code && code <= 0xFF.
              result[pos++] = (byte)code;
            } else {
              switch (c) {
                case 'a' : result[pos++] = 0x07; break;
                case 'b' : result[pos++] = '\b'; break;
                case 'f' : result[pos++] = '\f'; break;
                case 'n' : result[pos++] = '\n'; break;
                case 'r' : result[pos++] = '\r'; break;
                case 't' : result[pos++] = '\t'; break;
                case 'v' : result[pos++] = 0x0b; break;
                case '\\': result[pos++] = '\\'; break;
                case '\'': result[pos++] = '\''; break;
                case '"' : result[pos++] = '\"'; break;

                case 'x':
                  // hex escape
                  int code = 0;
                  if (i + 1 < input.size() && isHex((char) input.byteAt(i + 1))) {
                    ++i;
                    code = digitValue((char) input.byteAt(i));
                  } else {
                    throw new IllegalArgumentException(
                        "Invalid escape sequence: '\\x' with no digits");
                  }
                  if (i + 1 < input.size() && isHex((char) input.byteAt(i + 1))) {
                    ++i;
                    code = code * 16 + digitValue((char) input.byteAt(i));
                  }
                  result[pos++] = (byte)code;
                  break;

                default:
                  throw new IllegalArgumentException(
                      "Invalid escape sequence: '\\" + (char)c + '\'');
              }
            }
          } else {
            throw new IllegalArgumentException(
                "Invalid escape sequence: '\\' at end of string.");
          }
        } else {
          result[pos++] = c;
        }
      }

      return ByteString.copyFrom(result, 0, pos);
    }

}
