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

package com.liferay.vulcan.wiring.osgi.manager;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import com.liferay.vulcan.alias.RequestFunction;
import com.liferay.vulcan.consumer.TriConsumer;
import com.liferay.vulcan.documentation.APIDescription;
import com.liferay.vulcan.documentation.APITitle;
import com.liferay.vulcan.documentation.Documentation;
import com.liferay.vulcan.error.VulcanDeveloperError;
import com.liferay.vulcan.resource.CollectionResource;
import com.liferay.vulcan.resource.CollectionResourceInfo;
import com.liferay.vulcan.resource.RelatedCollection;
import com.liferay.vulcan.resource.Representor;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.resource.ScopedCollectionResource;
import com.liferay.vulcan.resource.builder.RoutesBuilder;
import com.liferay.vulcan.resource.identifier.Identifier;
import com.liferay.vulcan.result.Try;
import com.liferay.vulcan.wiring.osgi.internal.resource.builder.RoutesBuilderImpl;
import com.liferay.vulcan.wiring.osgi.util.GenericUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides methods to retrieve information provided by the different {@link
 * CollectionResource} instances. This information includes field functions,
 * types, identifier functions, and more.
 *
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 * @see    CollectionResource
 */
@Component(immediate = true, service = CollectionResourceManager.class)
public class CollectionResourceManager extends BaseManager<CollectionResource> {

	@Activate
	public void activate() {
		RequestFunction<Optional<APITitle>> apiTitleFunction =
			httpServletRequest -> _providerManager.provideOptional(
				APITitle.class, httpServletRequest);

		RequestFunction<Optional<APIDescription>> apiDescriptionFunction =
			httpServletRequest -> _providerManager.provideOptional(
				APIDescription.class, httpServletRequest);

		_documentation = new Documentation(
			apiTitleFunction, apiDescriptionFunction);
	}

	public Documentation getDocumentation() {
		return _documentation;
	}

	/**
	 * Returns the resource name's model class.
	 *
	 * @param  name the resource name
	 * @return the resource name's model class
	 */
	@SuppressWarnings("unchecked")
	public <T> Optional<Class<T>> getModelClassOptional(String name) {
		Optional<? extends Class<?>> optional = Optional.ofNullable(
			_classes.get(name));

		return optional.map(clazz -> (Class<T>)clazz);
	}

	/**
	 * Returns the name of a collection resource that matches the specified
	 * class name.
	 *
	 * @param  className the collection resource's class name
	 * @return the collection resource's name
	 */
	public Optional<String> getNameOptional(String className) {
		Optional<CollectionResource> optional = _getCollectionResourceOptional(
			className);

		return optional.map(CollectionResource::getName);
	}

	/**
	 * Returns the representor of the collection resource's model class, if that
	 * representor exists. Returns {@code Optional#empty()} otherwise.
	 *
	 * @param  modelClass the collection resource's model class
	 * @return the model class's representor, if present; {@code
	 *         Optional#empty()} otherwise
	 */
	public <T, U extends Identifier> Optional<Representor<T, U>>
		getRepresentorOptional(Class<T> modelClass) {

		Optional<CollectionResourceInfo<T, U>> optional =
			getResourceInfoOptional(modelClass);

		return optional.map(CollectionResourceInfo::getRepresentor);
	}

	/**
	 * Returns the {@code CollectionResourceInfo} of the collection resource's
	 * model class, if that info exists. Returns {@code Optional#empty()}
	 * otherwise.
	 *
	 * @param  modelClass the collection resource's model class
	 * @return the model class's {@code CollectionResourceInfo}, if present;
	 *         {@code Optional#empty()} otherwise
	 * @review
	 */
	@SuppressWarnings("unchecked")
	public <T, U extends Identifier> Optional<CollectionResourceInfo<T, U>>
		getResourceInfoOptional(Class<T> modelClass) {

		Optional<CollectionResourceInfo> optional = Optional.ofNullable(
			_collectionResourceInfoMap.get(modelClass.getName()));

		return optional.map(
			collectionResourceInfo ->
				(CollectionResourceInfo<T, U>)collectionResourceInfo);
	}

	/**
	 * Returns the root collection resource's list of names.
	 *
	 * @return the root collection resource's list of names
	 */
	public List<String> getRootCollectionResourceNames() {
		return _rootCollectionResourceNames;
	}

	/**
	 * Returns the model class's routes for the collection resource's name.
	 *
	 * @param  name the collection resource's name
	 * @return the model class's routes
	 */
	public <T> Optional<Routes<T>> getRoutesOptional(String name) {
		Optional<Class<T>> optional = getModelClassOptional(name);

		return optional.flatMap(
			this::getResourceInfoOptional
		).map(
			CollectionResourceInfo::getRoutes
		);
	}

	@Reference(cardinality = MULTIPLE, policy = DYNAMIC, policyOption = GREEDY)
	protected void setServiceReference(
		ServiceReference<CollectionResource> serviceReference) {

		Optional<Class<Object>> optional = addService(serviceReference);

		optional.ifPresent(this::_addModelClassMaps);
	}

	@SuppressWarnings("unused")
	protected void unsetServiceReference(
		ServiceReference<CollectionResource> serviceReference) {

		Optional<Class<Object>> optional = removeService(serviceReference);

		optional.ifPresent(this::_removeModelClassMaps);

		optional.filter(
			modelClass -> {
				Optional<CollectionResource> collectionResourceOptional =
					_getCollectionResourceOptional(modelClass.getName());

				return collectionResourceOptional.isPresent();
			}
		).ifPresent(
			this::_addModelClassMaps
		);
	}

	@SuppressWarnings("unchecked")
	private <T, U extends Identifier> void _addModelClassMaps(
		Class<T> modelClass) {

		Optional<CollectionResource> optional = _getCollectionResourceOptional(
			modelClass.getName());

		optional.map(
			collectionResource -> (CollectionResource<T, U>)collectionResource
		).ifPresent(
			collectionResource -> {
				String name = collectionResource.getName();

				_classes.put(name, modelClass);

				if (!(collectionResource instanceof ScopedCollectionResource)) {
					_rootCollectionResourceNames.add(name);
				}

				Class<U> identifierClass = _getIdentifierClass(
					collectionResource);

				Supplier<List<RelatedCollection<T, ?>>>
					relatedCollectionSupplier =
						() -> (List)_relatedCollections.get(
							modelClass.getName());

				Representor<T, U> representor =
					collectionResource.buildRepresentor(
						new Representor.Builder<>(
							identifierClass,
							_addRelatedCollectionTriConsumer(modelClass),
							relatedCollectionSupplier));

				RequestFunction<Function<Class<?>, Optional<?>>>
					provideClassFunction =
						httpServletRequest -> clazz ->
							_providerManager.provideOptional(
								clazz, httpServletRequest);

				RoutesBuilder<T, U> routesBuilder = new RoutesBuilderImpl<>(
					modelClass, identifierClass, provideClassFunction,
					_pathIdentifierMapperManager::map);

				Routes<T> routes = collectionResource.routes(routesBuilder);

				CollectionResourceInfo<T, U> collectionResourceInfo =
					new CollectionResourceInfo<>(name, representor, routes);

				_collectionResourceInfoMap.put(
					modelClass.getName(), collectionResourceInfo);
			}
		);
	}

	private <T> TriConsumer<String, Class<?>, Function<Object, Identifier>>
		_addRelatedCollectionTriConsumer(Class<T> relatedModelClass) {

		return (key, modelClass, identifierFunction) -> {
			List<RelatedCollection<?, ?>> relatedCollections =
				_relatedCollections.computeIfAbsent(
					modelClass.getName(), className -> new ArrayList<>());

			relatedCollections.add(
				new RelatedCollection<>(
					key, relatedModelClass, identifierFunction));
		};
	}

	private Optional<CollectionResource> _getCollectionResourceOptional(
		String modelClassName) {

		return getServiceOptional(modelClassName);
	}

	private <T, U extends Identifier> Class<U> _getIdentifierClass(
		CollectionResource<T, U> collectionResource) {

		Class<? extends CollectionResource> resourceClass =
			collectionResource.getClass();

		Try<Class<U>> classTry = GenericUtil.getGenericTypeArgumentTry(
			resourceClass, 1);

		return classTry.orElseThrow(
			() -> new VulcanDeveloperError.MustHaveValidGenericType(
				resourceClass));
	}

	private <T> void _removeModelClassMaps(Class<T> modelClass) {
		Collection<Class<?>> classes = _classes.values();

		classes.removeIf(next -> next.equals(modelClass));

		Optional<String> optional = getNameOptional(modelClass.getName());

		optional.ifPresent(_rootCollectionResourceNames::remove);

		_relatedCollections.forEach(
			(className, relatedCollections) -> relatedCollections.removeIf(
				relatedCollection ->
					relatedCollection.getModelClass().equals(modelClass)));

		_collectionResourceInfoMap.remove(modelClass.getName());
	}

	private final Map<String, Class<?>> _classes = new ConcurrentHashMap<>();
	private final Map<String, CollectionResourceInfo>
		_collectionResourceInfoMap = new ConcurrentHashMap<>();
	private Documentation _documentation;

	@Reference
	private PathIdentifierMapperManager _pathIdentifierMapperManager;

	@Reference
	private ProviderManager _providerManager;

	private final Map<String, List<RelatedCollection<?, ?>>>
		_relatedCollections = new ConcurrentHashMap<>();
	private final List<String> _rootCollectionResourceNames = new ArrayList<>();

}