package com.googlecode.protobuf.format;


import java.io.IOException;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet;

/**
 * Created by IntelliJ IDEA.
 * User: aantonov
 * Date: Mar 16, 2010
 * Time: 4:06:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class CouchDBFormat extends JsonFormat {

    /**
     * Outputs a textual representation of the Protocol Message supplied into the parameter output.
     * (This representation is the new version of the classic "ProtocolPrinter" output from the
     * original Protocol Buffer system)
     */
    public void print(final Message message, Appendable output) throws IOException {
        CouchDBGenerator generator = new CouchDBGenerator(output);
        generator.print("{");
        print(message, generator);
        generator.print("}");
    }

    /**
     * Outputs a textual representation of {@code fields} to {@code output}.
     */
    public void print(final UnknownFieldSet fields, Appendable output) throws IOException {
        CouchDBGenerator generator = new CouchDBGenerator(output);
        generator.print("{");
        printUnknownFields(fields, generator);
        generator.print("}");
    }


    /**
     * Parse a text-format message from {@code input} and merge the contents into {@code builder}.
     * Extensions will be recognized if they are registered in {@code extensionRegistry}.
     */
    public void merge(CharSequence input,
                             ExtensionRegistry extensionRegistry,
                             Message.Builder builder) throws ParseException {
        Tokenizer tokenizer = new Tokenizer(input);

        // Based on the state machine @ http://json.org/

        tokenizer.consume("{"); // Needs to happen when the object starts.
        while (!tokenizer.tryConsume("}")) { // Continue till the object is done
            mergeField(tokenizer, extensionRegistry, builder);
        }
    }

    protected static class Tokenizer extends JsonFormat.Tokenizer {

        /**
         * Construct a tokenizer that parses tokens from the given text.
         */
        public Tokenizer(CharSequence text) {
            super(text);
        }

        @Override
        public String consumeIdentifier() throws ParseException {
            String id = super.consumeIdentifier();
            if ("_id".equals(id)) {
                return "id";
            } else if ("_rev".equals(id)) {
                return "rev";
            }
            return id;
        }
    }

    protected static class CouchDBGenerator extends JsonFormat.JsonGenerator {

        public CouchDBGenerator(Appendable output) {
            super(output);
        }

        @Override
        public void print(CharSequence text) throws IOException {
            if ("id".equals(text)) {
                super.print("_id");
            } else if ("rev".equals(text)) {
                super.print("_rev");
            } else {
                super.print(text);
            }
        }
    }
}
