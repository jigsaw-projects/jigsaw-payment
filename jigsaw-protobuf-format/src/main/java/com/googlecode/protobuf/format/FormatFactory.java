/**
 * Copyright 2000-2011 NeuStar, Inc. All rights reserved.
 * NeuStar, the Neustar logo and related names and logos are registered
 * trademarks, service marks or tradenames of NeuStar, Inc. All other
 * product names, company names, marks, logos and symbols may be trademarks
 * of their respective owners.
 */

package com.googlecode.protobuf.format;

public class FormatFactory {
	
	public FormatFactory() {}
	
	public enum Formatter {
		COUCHDB (CouchDBFormat.class),
		HTML (HtmlFormat.class),
		JAVA_PROPS (JavaPropsFormat.class),
		JSON (JsonFormat.class),
		XML (XmlFormat.class),
		JSON_JACKSON (JsonJacksonFormat.class),
		XML_JAVAX (XmlJavaxFormat.class);
		
		private Class<? extends ProtobufFormatter> formatterClass;
		Formatter(Class<? extends ProtobufFormatter> formatterClass) {
			this.formatterClass = formatterClass;
		}
		protected Class<? extends ProtobufFormatter> getFormatterClass() {
			return formatterClass;
		}
	}
	
	
	public ProtobufFormatter createFormatter(Formatter formatter) {
		try {
			return formatter.getFormatterClass().newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
