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

package com.liferay.vulcan.writer;

import com.google.gson.JsonObject;

import com.liferay.vulcan.alias.RequestFunction;
import com.liferay.vulcan.documentation.Documentation;
import com.liferay.vulcan.message.json.DocumentationMessageMapper;
import com.liferay.vulcan.message.json.JSONObjectBuilder;
import com.liferay.vulcan.request.RequestInfo;

import java.util.Optional;
import java.util.function.Function;

/**
 * An instance of this class can be used to write the documentation.
 *
 * @author Alejandro Hernández
 * @review
 */
public class DocumentationWriter {

	/**
	 * This method can be used to create a new {@code DocumentationWriter}
	 * object, without creating the builder.
	 *
	 * @param  function the function that transforms a builder into a {@code
	 *         DocumentationWriter}
	 * @return the {@code DocumentationWriter} instance
	 */
	public static DocumentationWriter create(
		Function<Builder, DocumentationWriter> function) {

		return function.apply(new Builder());
	}

	public DocumentationWriter(Builder builder) {
		_documentationMessageMapper = builder._documentationMessageMapper;
		_documentation = builder._documentation;
		_requestInfo = builder._requestInfo;
	}

	/**
	 * Write the handled {@link Documentation} to a String.
	 *
	 * @return the JSON representation of the {@code Documentation}
	 * @review
	 */
	public String write() {
		JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilder();

		RequestFunction<Optional<String>> titleFunction =
			_documentation.getTitleFunction();

		titleFunction.apply(
			_requestInfo.getHttpServletRequest()
		).ifPresent(
			title -> _documentationMessageMapper.mapTitle(
				jsonObjectBuilder, title)
		);

		RequestFunction<Optional<String>> descriptionFunction =
			_documentation.getDescriptionFunction();

		descriptionFunction.apply(
			_requestInfo.getHttpServletRequest()
		).ifPresent(
			description -> _documentationMessageMapper.mapDescription(
				jsonObjectBuilder, description)
		);

		_documentationMessageMapper.onStart(
			jsonObjectBuilder, _documentation, _requestInfo.getHttpHeaders());

		_documentationMessageMapper.onFinish(
			jsonObjectBuilder, _documentation, _requestInfo.getHttpHeaders());

		JsonObject jsonObject = jsonObjectBuilder.build();

		return jsonObject.toString();
	}

	/**
	 * Use instances of this builder to create {@link DocumentationWriter}
	 * instances.
	 *
	 * @review
	 */
	public static class Builder {

		/**
		 * Add information about the documentation being written to the builder.
		 *
		 * @param  documentation the documentation being written
		 * @return the updated builder
		 */
		public DocumentationMessageMapperStep documentation(
			Documentation documentation) {

			_documentation = documentation;

			return new DocumentationMessageMapperStep();
		}

		public class BuildStep {

			/**
			 * Constructs and returns a {@link DocumentationWriter} instance
			 * with the information provided to the builder.
			 *
			 * @return the {@code Documentation} instance
			 * @review
			 */
			public DocumentationWriter build() {
				return new DocumentationWriter(Builder.this);
			}

		}

		public class DocumentationMessageMapperStep {

			/**
			 * Add information about the {@code DocumentationMessageMapper} to
			 * the builder.
			 *
			 * @param  documentationMessageMapper the {@code
			 *         DocumentationMessageMapper} headers.
			 * @return the updated builder.
			 * @review
			 */
			public RequestInfoStep documentationMessageMapper(
				DocumentationMessageMapper documentationMessageMapper) {

				_documentationMessageMapper = documentationMessageMapper;

				return new RequestInfoStep();
			}

		}

		public class RequestInfoStep {

			/**
			 * Add information about the request info to the builder.
			 *
			 * @param  requestInfo the information obtained from the request. It
			 *         can be created by using a {@link RequestInfo.Builder}
			 * @return the updated builder.
			 * @review
			 */
			public BuildStep requestInfo(RequestInfo requestInfo) {
				_requestInfo = requestInfo;

				return new BuildStep();
			}

		}

		private Documentation _documentation;
		private DocumentationMessageMapper _documentationMessageMapper;
		private RequestInfo _requestInfo;

	}

	private final Documentation _documentation;
	private final DocumentationMessageMapper _documentationMessageMapper;
	private final RequestInfo _requestInfo;

}