/**
 * Copyright 2000-2011 NeuStar, Inc. All rights reserved.
 * NeuStar, the Neustar logo and related names and logos are registered
 * trademarks, service marks or tradenames of NeuStar, Inc. All other
 * product names, company names, marks, logos and symbols may be trademarks
 * of their respective owners.
 */

package com.googlecode.protobuf.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.UnknownFieldSet;
import com.googlecode.protobuf.format.ProtobufFormatter.ParseException;
import com.googlecode.protobuf.format.util.TextUtils;

public abstract class AbstractCharBasedFormatter extends ProtobufFormatter {

	@Override
	public void print(Message message, OutputStream output, Charset cs)
			throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(output, cs);
		print(message, writer);
		writer.flush();
	}
	
	abstract public void print(Message message, Appendable output) throws IOException;


	@Override
	public void print(UnknownFieldSet fields, OutputStream output, Charset cs)
			throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(output, cs);
		print(fields, writer);
		writer.flush();
	}
	
	abstract public void print(UnknownFieldSet fields, Appendable output) throws IOException;

	@Override
	public void merge(InputStream input, Charset cs, 
			ExtensionRegistry extensionRegistry, Builder builder) throws IOException {
		InputStreamReader reader = new InputStreamReader(input, cs);
		merge(reader, extensionRegistry, builder);
	}
	
	
	abstract public void merge(CharSequence input, ExtensionRegistry extensionRegistry,
            Message.Builder builder) throws IOException;
	
	/**
     * Parse a text-format message from {@code input} and merge the contents into {@code builder}.
     * Extensions will be recognized if they are registered in {@code extensionRegistry}.
     */
    public void merge(Readable input,
    		ExtensionRegistry extensionRegistry,
    		Message.Builder builder) throws IOException {
        // Read the entire input to a String then parse that.

        // If StreamTokenizer were not quite so crippled, or if there were a kind
        // of Reader that could read in chunks that match some particular regex,
        // or if we wanted to write a custom Reader to tokenize our stream, then
        // we would not have to read to one big String. Alas, none of these is
        // the case. Oh well.

		merge(TextUtils.toStringBuilder(input), extensionRegistry, builder);
    }
}
