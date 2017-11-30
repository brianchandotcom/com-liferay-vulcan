/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.vulcan.test.writer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.liferay.vulcan.documentation.Documentation;
import com.liferay.vulcan.message.json.DocumentationMessageMapper;
import com.liferay.vulcan.request.RequestInfo;
import com.liferay.vulcan.writer.DocumentationWriter;

import java.util.Optional;

import javax.ws.rs.core.HttpHeaders;

/**
 * This class provides methods that can be used for testing documentation
 * message mappers.
 *
 * <p>
 * This class shouldn't be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 * @review
 */
public class MockDocumentationWriter {

	/**
	 * Returns a {@link RequestInfo} with the provided {@link HttpHeaders}, a
	 * mock {@link com.liferay.vulcan.url.ServerURL} and a mock {@link
	 * javax.servlet.http.HttpServletRequest}.
	 *
	 * @param  httpHeaders the HTTP headers
	 * @return the request info
	 * @review
	 */
	public static RequestInfo getRequestInfo(HttpHeaders httpHeaders) {
		return RequestInfo.create(
			builder -> builder.httpHeaders(
				httpHeaders
			).httpServletRequest(
				null
			).serverURL(
				() -> "localhost"
			).build());
	}

	/**
	 * Writes a documentation.
	 *
	 * @param  httpHeaders the HTTP headers from the request
	 * @param  documentationMessageMapper the message mapper to use for writing
	 *         the json object
	 * @review
	 */
	public static JsonObject write(
		HttpHeaders httpHeaders,
		DocumentationMessageMapper documentationMessageMapper) {

		RequestInfo requestInfo = getRequestInfo(httpHeaders);

		Documentation documentation = new Documentation(
			__ -> Optional.of(() -> "Title"),
			__ -> Optional.of(() -> "Description"));

		DocumentationWriter documentationWriter = DocumentationWriter.create(
			builder -> builder.documentation(
				documentation
			).documentationMessageMapper(
				documentationMessageMapper
			).requestInfo(
				requestInfo
			).build());

		return new Gson().fromJson(
			documentationWriter.write(), JsonObject.class);
	}

	private MockDocumentationWriter() {
		throw new UnsupportedOperationException();
	}

}