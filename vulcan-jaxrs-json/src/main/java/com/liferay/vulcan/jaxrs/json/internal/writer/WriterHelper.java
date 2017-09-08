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

package com.liferay.vulcan.jaxrs.json.internal.writer;

import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.vulcan.alias.BinaryFunction;
import com.liferay.vulcan.function.TriConsumer;
import com.liferay.vulcan.identifier.Identifier;
import com.liferay.vulcan.jaxrs.json.internal.JSONObjectBuilderImpl;
import com.liferay.vulcan.jaxrs.json.internal.StringFunctionalList;
import com.liferay.vulcan.list.FunctionalList;
import com.liferay.vulcan.message.json.ErrorMessageMapper;
import com.liferay.vulcan.message.json.JSONObjectBuilder;
import com.liferay.vulcan.pagination.Page;
import com.liferay.vulcan.pagination.SingleModel;
import com.liferay.vulcan.provider.ServerURLProvider;
import com.liferay.vulcan.resource.RelatedCollection;
import com.liferay.vulcan.resource.RelatedModel;
import com.liferay.vulcan.response.control.Embedded;
import com.liferay.vulcan.response.control.Fields;
import com.liferay.vulcan.result.APIError;
import com.liferay.vulcan.uri.CollectionResourceURITransformer;
import com.liferay.vulcan.wiring.osgi.manager.ResourceManager;

import java.net.URI;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provide methods to help {@link javax.ws.rs.ext.MessageBodyWriter} write
 * Hypermedia resources.
 *
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true, service = WriterHelper.class)
public class WriterHelper {

	/**
	 * Helper method to write an {@code APIError} into a JSON object.
	 *
	 * @param  errorMessageMapper the correct {@code ErrorMessageMapper} for
	 *         this combination of {@code APIError}/{@code HttpHeaders}.
	 * @param  apiError an instance of the apiError.
	 * @param  httpHeaders the HTTP headers of the current request.
	 * @return the apiError written in a JSON string.
	 */
	public static String writeError(
		ErrorMessageMapper errorMessageMapper, APIError apiError,
		HttpHeaders httpHeaders) {

		JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilderImpl();

		errorMessageMapper.onStart(jsonObjectBuilder, apiError, httpHeaders);

		Optional<String> optional = apiError.getDescription();

		optional.ifPresent(
			description -> errorMessageMapper.mapDescription(
				jsonObjectBuilder, description));

		errorMessageMapper.mapStatusCode(
			jsonObjectBuilder, apiError.getStatusCode());
		errorMessageMapper.mapTitle(jsonObjectBuilder, apiError.getTitle());
		errorMessageMapper.mapType(jsonObjectBuilder, apiError.getType());
		errorMessageMapper.onFinish(jsonObjectBuilder, apiError, httpHeaders);

		JSONObject jsonObject = jsonObjectBuilder.build();

		return jsonObject.toString();
	}

	/**
	 * Returns the absolute URL from a relative URI.
	 *
	 * @param  httpServletRequest the actual HTTP servlet request.
	 * @param  relativeURI a relative URI.
	 * @return the absolute URL.
	 */
	public String getAbsoluteURL(
		HttpServletRequest httpServletRequest, String relativeURI) {

		String serverURL = _serverURLProvider.getServerURL(httpServletRequest);

		UriBuilder uriBuilder = UriBuilder.fromPath(serverURL);

		uriBuilder = uriBuilder.clone();

		uriBuilder.path(relativeURI);

		URI uri = uriBuilder.build();

		return uri.toString();
	}

	/**
	 * Returns the page collection URL. If a {@link
	 * com.liferay.vulcan.resource.Resource} for that model class cannot be
	 * found, returns <code>Optional#empty()</code>.
	 *
	 * @param  page the page of the {@link com.liferay.vulcan.resource.Resource}
	 *         collection.
	 * @param  httpServletRequest the actual HTTP servlet request.
	 * @return the page collection URL if a {@link
	 *         com.liferay.vulcan.resource.Resource} for the model class can be
	 *         found; <code>Optional#empty()</code> otherwise.
	 */
	public <T> Optional<String> getCollectionURLOptional(
		Page<T> page, HttpServletRequest httpServletRequest) {

		Optional<String> optional = _resourceManager.getPath(
			page.getModelClass());

		return optional.map(
			path -> {
				Identifier identifier = page.getIdentifier();

				return "/p" + identifier.asURI() + "/" + path;
			}
		).map(
			_getTransformURIFunction(
				(uri, transformer) -> transformer.transformPageURI(uri, page))
		).map(
			uri -> getAbsoluteURL(httpServletRequest, uri)
		);
	}

	/**
	 * Returns the URL to the resource of a certain model. If a {@link
	 * com.liferay.vulcan.resource.Resource} for that model class cannot be
	 * found, returns {@code Optional#empty()}.
	 *
	 * @param  singleModel a single model.
	 * @param  httpServletRequest the actual HTTP servlet request.
	 * @return the single URL for the {@code Resource} if present; {@code
	 *         Optional#empty()} otherwise.
	 */
	public <T> Optional<String> getSingleURLOptional(
		SingleModel<T> singleModel, HttpServletRequest httpServletRequest) {

		Optional<Identifier> optional = _resourceManager.getIdentifierOptional(
			singleModel.getModelClass(), singleModel.getModel());

		return optional.map(
			identifier -> "/p/" + identifier.getType() + "/" +
				identifier.getId()
		).map(
			_getTransformURIFunction(
				(uri, transformer) ->
					transformer.transformCollectionItemSingleResourceURI(
						uri, singleModel))
		).map(
			uri -> getAbsoluteURL(httpServletRequest, uri)
		);
	}

	/**
	 * Helper method to write binary resources. It uses a bi consumer so each
	 * {@link javax.ws.rs.ext.MessageBodyWriter} can write each binary
	 * differently.
	 *
	 * @param binaryFunctions functions used to obtain the binaries.
	 * @param singleModel a single model.
	 * @param httpServletRequest the actual HTTP request.
	 * @param biConsumer the consumer that will be called to write each binary.
	 */
	public <T> void writeBinaries(
		Map<String, BinaryFunction<T>> binaryFunctions,
		SingleModel<T> singleModel, HttpServletRequest httpServletRequest,
		BiConsumer<String, Object> biConsumer) {

		Optional<Identifier> optional = _resourceManager.getIdentifierOptional(
			singleModel.getModelClass(), singleModel.getModel());

		optional.map(
			identifier -> "/b/" + identifier.asURI() + "/"
		).ifPresent(
			resourceURI -> {
				for (String binaryId : binaryFunctions.keySet()) {
					String binaryURI = resourceURI + binaryId;

					Function<String, String> transformURIFunction =
						_getTransformURIFunction(
							(uri, transformer) ->
								transformer.transformBinaryURI(
									uri, singleModel, binaryId));

					String transformedURI = transformURIFunction.apply(
						binaryURI);

					String url = getAbsoluteURL(
						httpServletRequest, transformedURI);

					biConsumer.accept(binaryId, url);
				}
			}
		);
	}

	/**
	 * Helper method to write a model fields. It uses a consumer so each {@link
	 * javax.ws.rs.ext.MessageBodyWriter} can write each field differently.
	 *
	 * @param model a model.
	 * @param modelClass a model class.
	 * @param fields the requested fields.
	 * @param biConsumer the consumer that will be called to write each field.
	 */
	public <T> void writeFields(
		T model, Class<T> modelClass, Fields fields,
		BiConsumer<String, Object> biConsumer) {

		Predicate<String> fieldsPredicate = _getFieldsPredicate(
			modelClass, fields);

		Map<String, Function<T, Object>> fieldFunctions =
			_resourceManager.getFieldFunctions(modelClass);

		for (String field : fieldFunctions.keySet()) {
			if (fieldsPredicate.test(field)) {
				Function<T, Object> fieldFunction = fieldFunctions.get(field);

				Object data = fieldFunction.apply(model);

				if (data != null) {
					biConsumer.accept(field, data);
				}
			}
		}
	}

	/**
	 * Helper method to write a model linked related models. It uses a consumer
	 * so each {@link javax.ws.rs.ext.MessageBodyWriter} can write the related
	 * model differently.
	 *
	 * @param relatedModel the instance of the related model.
	 * @param parentSingleModel the parent single model.
	 * @param parentEmbeddedPathElements list of embedded path elements.
	 * @param httpServletRequest the actual HTTP servlet request.
	 * @param fields the requested fields.
	 * @param embedded the embedded resources info.
	 * @param biConsumer the consumer that will be called to write the related
	 *        model.
	 */
	public <T, U> void writeLinkedRelatedModel(
		RelatedModel<T, U> relatedModel, SingleModel<T> parentSingleModel,
		FunctionalList<String> parentEmbeddedPathElements,
		HttpServletRequest httpServletRequest, Fields fields, Embedded embedded,
		BiConsumer<String, FunctionalList<String>> biConsumer) {

		BiConsumer<SingleModel<U>, FunctionalList<String>> emptyConsumer =
			(singleModel, embeddedPathElements) -> {
			};

		writeRelatedModel(
			relatedModel, parentSingleModel, parentEmbeddedPathElements,
			httpServletRequest, fields, embedded, emptyConsumer,
			(url, embeddedPathElements, isEmbedded) -> biConsumer.accept(
				url, embeddedPathElements));
	}

	/**
	 * Helper method to write a model links. It uses a consumer so each {@link
	 * javax.ws.rs.ext.MessageBodyWriter} can write each link differently.
	 *
	 * @param modelClass the model class.
	 * @param fields the requested fields.
	 * @param biConsumer the consumer that will be called to write each link.
	 */
	public <T> void writeLinks(
		Class<T> modelClass, Fields fields,
		BiConsumer<String, String> biConsumer) {

		Predicate<String> fieldsPredicate = _getFieldsPredicate(
			modelClass, fields);

		Map<String, String> links = _resourceManager.getLinks(modelClass);

		for (String key : links.keySet()) {
			if (fieldsPredicate.test(key)) {
				biConsumer.accept(key, links.get(key));
			}
		}
	}

	/**
	 * Helper method to write a model related collection. It uses a consumer for
	 * writing the URL.
	 *
	 * @param relatedCollection the instance of the related collection.
	 * @param parentSingleModel the parent single model.
	 * @param parentEmbeddedPathElements list of embedded path elements.
	 * @param httpServletRequest the actual HTTP servlet request.
	 * @param fields the requested fields.
	 * @param biConsumer the consumer that will be called to write the related
	 *        collection URL.
	 */
	public <U, V> void writeRelatedCollection(
		RelatedCollection<U, V> relatedCollection,
		SingleModel<U> parentSingleModel,
		FunctionalList<String> parentEmbeddedPathElements,
		HttpServletRequest httpServletRequest, Fields fields,
		BiConsumer<String, FunctionalList<String>> biConsumer) {

		Predicate<String> fieldsPredicate = _getFieldsPredicate(
			parentSingleModel.getModelClass(), fields);

		String key = relatedCollection.getKey();

		if (!fieldsPredicate.test(key)) {
			return;
		}

		Optional<String> singleURLOptional = getSingleURLOptional(
			parentSingleModel, httpServletRequest);

		Class<V> modelClass = relatedCollection.getModelClass();

		Optional<String> pathOptional = _resourceManager.getPath(modelClass);

		pathOptional.flatMap(
			path -> singleURLOptional.map(singleURL -> singleURL + "/" + path)
		).ifPresent(
			url -> {
				FunctionalList<String> embeddedPathElements =
					new StringFunctionalList(parentEmbeddedPathElements, key);

				biConsumer.accept(url, embeddedPathElements);
			}
		);
	}

	/**
	 * Helper method to write a model related models. It uses two consumers (one
	 * for writing the model info, and another for writing its URL) so each
	 * {@link javax.ws.rs.ext.MessageBodyWriter} can write the related model
	 * differently.
	 *
	 * @param relatedModel the instance of the related model.
	 * @param parentSingleModel the parent single model.
	 * @param parentEmbeddedPathElements list of embedded path elements.
	 * @param httpServletRequest the actual HTTP servlet request.
	 * @param fields the requested fields.
	 * @param embedded the embedded resources info.
	 * @param modelBiConsumer the consumer that will be called to write the
	 *        related model info.
	 * @param urlTriConsumer the consumer that will be called to write the
	 *        related model URL.
	 */
	public <T, U> void writeRelatedModel(
		RelatedModel<T, U> relatedModel, SingleModel<T> parentSingleModel,
		FunctionalList<String> parentEmbeddedPathElements,
		HttpServletRequest httpServletRequest, Fields fields, Embedded embedded,
		BiConsumer<SingleModel<U>, FunctionalList<String>> modelBiConsumer,
		TriConsumer<String, FunctionalList<String>, Boolean> urlTriConsumer) {

		Predicate<String> fieldsPredicate = _getFieldsPredicate(
			parentSingleModel.getModelClass(), fields);

		String key = relatedModel.getKey();

		if (!fieldsPredicate.test(key)) {
			return;
		}

		Function<T, Optional<U>> modelFunction =
			relatedModel.getModelFunction();

		Optional<U> modelOptional = modelFunction.apply(
			parentSingleModel.getModel());

		if (!modelOptional.isPresent()) {
			return;
		}

		U model = modelOptional.get();

		Class<U> modelClass = relatedModel.getModelClass();

		SingleModel<U> singleModel = new SingleModel<>(model, modelClass);

		Predicate<String> embeddedPredicate = embedded.getEmbeddedPredicate();

		FunctionalList<String> embeddedPathElements = new StringFunctionalList(
			parentEmbeddedPathElements, key);

		Stream<String> stream = Stream.concat(
			Stream.of(embeddedPathElements.head()),
			embeddedPathElements.tailStream());

		String embeddedPath = String.join(
			".", stream.collect(Collectors.toList()));

		boolean isEmbedded = embeddedPredicate.test(embeddedPath);

		Optional<String> optional = getSingleURLOptional(
			singleModel, httpServletRequest);

		optional.ifPresent(
			url -> {
				urlTriConsumer.accept(url, embeddedPathElements, isEmbedded);

				if (isEmbedded) {
					modelBiConsumer.accept(singleModel, embeddedPathElements);
				}
			});
	}

	/**
	 * Helper method to write a model types. It uses a consumer so each {@link
	 * javax.ws.rs.ext.MessageBodyWriter} can write the types differently.
	 *
	 * @param modelClass the model class.
	 * @param consumer the consumer that will be called to write the types.
	 */
	public <U> void writeTypes(
		Class<U> modelClass, Consumer<List<String>> consumer) {

		List<String> types = _resourceManager.getTypes(modelClass);

		consumer.accept(types);
	}

	private <T> Predicate<String> _getFieldsPredicate(
		Class<T> modelClass, Fields fields) {

		List<String> types = _resourceManager.getTypes(modelClass);

		return fields.getFieldsPredicate(types);
	}

	private Function<String, String> _getTransformURIFunction(
		BiFunction<String, CollectionResourceURITransformer, String>
			biFunction) {

		return uri -> {
			Optional<CollectionResourceURITransformer>
				collectionResourceURITransformerOptional = Optional.ofNullable(
					_collectionResourceURITransformer);

			return collectionResourceURITransformerOptional.map(
				transformer -> biFunction.apply(uri, transformer)
			).orElse(
				uri
			);
		};
	}

	@Reference(cardinality = OPTIONAL, policyOption = GREEDY)
	private CollectionResourceURITransformer _collectionResourceURITransformer;

	@Reference
	private ResourceManager _resourceManager;

	@Reference
	private ServerURLProvider _serverURLProvider;

}