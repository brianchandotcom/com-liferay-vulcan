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

package com.liferay.vulcan.wiring.osgi.internal.resource.builder;

import com.liferay.vulcan.alias.RequestFunction;
import com.liferay.vulcan.consumer.DecaConsumer;
import com.liferay.vulcan.consumer.EnneaConsumer;
import com.liferay.vulcan.consumer.HeptaConsumer;
import com.liferay.vulcan.consumer.HexaConsumer;
import com.liferay.vulcan.consumer.OctaConsumer;
import com.liferay.vulcan.consumer.PentaConsumer;
import com.liferay.vulcan.consumer.TetraConsumer;
import com.liferay.vulcan.consumer.TriConsumer;
import com.liferay.vulcan.error.VulcanDeveloperError.MustHavePathIdentifierMapper;
import com.liferay.vulcan.error.VulcanDeveloperError.MustHaveProvider;
import com.liferay.vulcan.error.VulcanDeveloperError.MustUseSameIdentifier;
import com.liferay.vulcan.function.DecaFunction;
import com.liferay.vulcan.function.EnneaFunction;
import com.liferay.vulcan.function.HeptaFunction;
import com.liferay.vulcan.function.HexaFunction;
import com.liferay.vulcan.function.OctaFunction;
import com.liferay.vulcan.function.PentaFunction;
import com.liferay.vulcan.function.TetraFunction;
import com.liferay.vulcan.function.TriFunction;
import com.liferay.vulcan.function.UndecaFunction;
import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.pagination.SingleModel;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.resource.builder.RoutesBuilder;
import com.liferay.vulcan.resource.identifier.Identifier;
import com.liferay.vulcan.uri.Path;
import com.liferay.vulcan.wiring.osgi.internal.pagination.PageImpl;
import com.liferay.vulcan.wiring.osgi.internal.resource.RoutesImpl;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Alejandro Hernández
 */
@SuppressWarnings("Duplicates")
public class RoutesBuilderImpl<T, U extends Identifier>
	implements RoutesBuilder<T, U> {

	public RoutesBuilderImpl(
		Class<T> modelClass, Class<U> singleModelIdentifierClass,
		RequestFunction<Function<Class<?>, Optional<?>>> provideClassFunction,
		BiFunction<Class<? extends Identifier>, Path,
			Optional<? extends Identifier>> identifierFunction) {

		_modelClass = modelClass;
		_singleModelIdentifierClass = singleModelIdentifierClass;
		_provideClassFunction = provideClassFunction;
		_identifierFunction = identifierFunction;
	}

	@Override
	public <V extends Identifier> RoutesBuilder<T, U> addCollectionPageGetter(
		BiFunction<Pagination, V, PageItems<T>> biFunction,
		Class<V> identifierClass) {

		_routesImpl.setGetPageFunction(
			httpServletRequest -> path -> identifier -> {
				Pagination pagination = _provideClass(
					httpServletRequest, Pagination.class);

				V v = _getIdentifier(identifier, identifierClass);

				PageItems<T> pageItems = biFunction.apply(pagination, v);

				return new PageImpl<>(
					_modelClass, pageItems.getItems(),
					pagination.getItemsPerPage(), pagination.getPageNumber(),
					pageItems.getTotalCount(), path);
			});

		return this;
	}

	@Override
	public <V extends Identifier, A, B, C, D, E, F, G, H> RoutesBuilder<T, U>
		addCollectionPageGetter(
			DecaFunction<Pagination, V, A, B, C, D, E, F, G, H,
				PageItems<T>> decaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass,
			Class<G> gClass, Class<H> hClass) {

		_routesImpl.setGetPageFunction(
			httpServletRequest -> path -> identifier -> {
				Pagination pagination = _provideClass(
					httpServletRequest, Pagination.class);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);
				H h = _provideClass(httpServletRequest, hClass);

				V v = _getIdentifier(identifier, identifierClass);

				PageItems<T> pageItems = decaFunction.apply(
					pagination, v, a, b, c, d, e, f, g, h);

				return new PageImpl<>(
					_modelClass, pageItems.getItems(),
					pagination.getItemsPerPage(), pagination.getPageNumber(),
					pageItems.getTotalCount(), path);
			});

		return this;
	}

	@Override
	public <V extends Identifier, A, B, C, D, E, F, G> RoutesBuilder<T, U>
		addCollectionPageGetter(
			EnneaFunction<Pagination, V, A, B, C, D, E, F, G, PageItems<T>>
				enneaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass,
			Class<G> gClass) {

		_routesImpl.setGetPageFunction(
			httpServletRequest -> path -> identifier -> {
				Pagination pagination = _provideClass(
					httpServletRequest, Pagination.class);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);

				V v = _getIdentifier(identifier, identifierClass);

				PageItems<T> pageItems = enneaFunction.apply(
					pagination, v, a, b, c, d, e, f, g);

				return new PageImpl<>(
					_modelClass, pageItems.getItems(),
					pagination.getItemsPerPage(), pagination.getPageNumber(),
					pageItems.getTotalCount(), path);
			});

		return this;
	}

	@Override
	public <V extends Identifier, A, B, C, D, E> RoutesBuilder<T, U>
		addCollectionPageGetter(
			HeptaFunction<Pagination, V, A, B, C, D, E, PageItems<T>>
				heptaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass, Class<D> dClass, Class<E> eClass) {

		_routesImpl.setGetPageFunction(
			httpServletRequest -> path -> identifier -> {
				Pagination pagination = _provideClass(
					httpServletRequest, Pagination.class);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);

				V v = _getIdentifier(identifier, identifierClass);

				PageItems<T> pageItems = heptaFunction.apply(
					pagination, v, a, b, c, d, e);

				return new PageImpl<>(
					_modelClass, pageItems.getItems(),
					pagination.getItemsPerPage(), pagination.getPageNumber(),
					pageItems.getTotalCount(), path);
			});

		return this;
	}

	@Override
	public <V extends Identifier, A, B, C, D> RoutesBuilder<T, U>
		addCollectionPageGetter(
			HexaFunction<Pagination, V, A, B, C, D, PageItems<T>> hexaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass, Class<D> dClass) {

		_routesImpl.setGetPageFunction(
			httpServletRequest -> path -> identifier -> {
				Pagination pagination = _provideClass(
					httpServletRequest, Pagination.class);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);

				V v = _getIdentifier(identifier, identifierClass);

				PageItems<T> pageItems = hexaFunction.apply(
					pagination, v, a, b, c, d);

				return new PageImpl<>(
					_modelClass, pageItems.getItems(),
					pagination.getItemsPerPage(), pagination.getPageNumber(),
					pageItems.getTotalCount(), path);
			});

		return this;
	}

	@Override
	public <V extends Identifier, A, B, C, D, E, F> RoutesBuilder<T, U>
		addCollectionPageGetter(
			OctaFunction<Pagination, V, A, B, C, D, E, F, PageItems<T>>
				octaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass, Class<D> dClass, Class<E> eClass,
			Class<F> fClass) {

		_routesImpl.setGetPageFunction(
			httpServletRequest -> path -> identifier -> {
				Pagination pagination = _provideClass(
					httpServletRequest, Pagination.class);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);

				V v = _getIdentifier(identifier, identifierClass);

				PageItems<T> pageItems = octaFunction.apply(
					pagination, v, a, b, c, d, e, f);

				return new PageImpl<>(
					_modelClass, pageItems.getItems(),
					pagination.getItemsPerPage(), pagination.getPageNumber(),
					pageItems.getTotalCount(), path);
			});

		return this;
	}

	@Override
	public <V extends Identifier, A, B, C> RoutesBuilder<T, U>
		addCollectionPageGetter(
			PentaFunction<Pagination, V, A, B, C, PageItems<T>> pentaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass) {

		_routesImpl.setGetPageFunction(
			httpServletRequest -> path -> identifier -> {
				Pagination pagination = _provideClass(
					httpServletRequest, Pagination.class);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);

				V v = _getIdentifier(identifier, identifierClass);

				PageItems<T> pageItems = pentaFunction.apply(
					pagination, v, a, b, c);

				return new PageImpl<>(
					_modelClass, pageItems.getItems(),
					pagination.getItemsPerPage(), pagination.getPageNumber(),
					pageItems.getTotalCount(), path);
			});

		return this;
	}

	@Override
	public <V extends Identifier, A, B> RoutesBuilder<T, U>
		addCollectionPageGetter(
			TetraFunction<Pagination, V, A, B, PageItems<T>> tetraFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass) {

		_routesImpl.setGetPageFunction(
			httpServletRequest -> path -> identifier -> {
				Pagination pagination = _provideClass(
					httpServletRequest, Pagination.class);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);

				V v = _getIdentifier(identifier, identifierClass);

				PageItems<T> pageItems = tetraFunction.apply(
					pagination, v, a, b);

				return new PageImpl<>(
					_modelClass, pageItems.getItems(),
					pagination.getItemsPerPage(), pagination.getPageNumber(),
					pageItems.getTotalCount(), path);
			});

		return this;
	}

	@Override
	public <V extends Identifier, A> RoutesBuilder<T, U>
		addCollectionPageGetter(
			TriFunction<Pagination, V, A, PageItems<T>> triFunction,
			Class<V> identifierClass, Class<A> aClass) {

		_routesImpl.setGetPageFunction(
			httpServletRequest -> path -> identifier -> {
				Pagination pagination = _provideClass(
					httpServletRequest, Pagination.class);
				A a = _provideClass(httpServletRequest, aClass);

				V v = _getIdentifier(identifier, identifierClass);

				PageItems<T> pageItems = triFunction.apply(pagination, v, a);

				return new PageImpl<>(
					_modelClass, pageItems.getItems(),
					pagination.getItemsPerPage(), pagination.getPageNumber(),
					pageItems.getTotalCount(), path);
			});

		return this;
	}

	@Override
	public <V extends Identifier> RoutesBuilder<T, U>
		addCollectionPageItemCreator(
			BiFunction<V, Map<String, Object>, T> biFunction,
			Class<V> identifierClass) {

		_routesImpl.setCreateItemFunction(
			httpServletRequest -> identifier -> body -> {
				V v = _getIdentifier(identifier, identifierClass);

				T t = biFunction.apply(v, body);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G, H, V extends Identifier> RoutesBuilder<T, U>
		addCollectionPageItemCreator(
			DecaFunction<V, Map<String, Object>, A, B, C, D, E, F, G, H, T>
				decaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass,
			Class<G> gClass, Class<H> hClass) {

		_routesImpl.setCreateItemFunction(
			httpServletRequest -> identifier -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);
				H h = _provideClass(httpServletRequest, hClass);

				V v = _getIdentifier(identifier, identifierClass);

				T t = decaFunction.apply(v, body, a, b, c, d, e, f, g, h);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G, V extends Identifier> RoutesBuilder<T, U>
		addCollectionPageItemCreator(
			EnneaFunction<V, Map<String, Object>, A, B, C, D, E, F, G, T>
				enneaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass,
			Class<G> gClass) {

		_routesImpl.setCreateItemFunction(
			httpServletRequest -> identifier -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);

				V v = _getIdentifier(identifier, identifierClass);

				T t = enneaFunction.apply(v, body, a, b, c, d, e, f, g);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, V extends Identifier> RoutesBuilder<T, U>
		addCollectionPageItemCreator(
			HeptaFunction<V, Map<String, Object>, A, B, C, D, E, T>
				heptaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass, Class<D> dClass, Class<E> eClass) {

		_routesImpl.setCreateItemFunction(
			httpServletRequest -> identifier -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);

				V v = _getIdentifier(identifier, identifierClass);

				T t = heptaFunction.apply(v, body, a, b, c, d, e);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, D, V extends Identifier> RoutesBuilder<T, U>
		addCollectionPageItemCreator(
			HexaFunction<V, Map<String, Object>, A, B, C, D, T> hexaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass, Class<D> dClass) {

		_routesImpl.setCreateItemFunction(
			httpServletRequest -> identifier -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);

				V v = _getIdentifier(identifier, identifierClass);

				T t = hexaFunction.apply(v, body, a, b, c, d);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, V extends Identifier> RoutesBuilder<T, U>
		addCollectionPageItemCreator(
			OctaFunction<V, Map<String, Object>, A, B, C, D, E, F, T>
				octaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass, Class<D> dClass, Class<E> eClass,
			Class<F> fClass) {

		_routesImpl.setCreateItemFunction(
			httpServletRequest -> identifier -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);

				V v = _getIdentifier(identifier, identifierClass);

				T t = octaFunction.apply(v, body, a, b, c, d, e, f);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, V extends Identifier> RoutesBuilder<T, U>
		addCollectionPageItemCreator(
			PentaFunction<V, Map<String, Object>, A, B, C, T> pentaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass) {

		_routesImpl.setCreateItemFunction(
			httpServletRequest -> identifier -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);

				V v = _getIdentifier(identifier, identifierClass);

				T t = pentaFunction.apply(v, body, a, b, c);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, V extends Identifier> RoutesBuilder<T, U>
		addCollectionPageItemCreator(
			TetraFunction<V, Map<String, Object>, A, B, T> tetraFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass) {

		_routesImpl.setCreateItemFunction(
			httpServletRequest -> identifier -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);

				V v = _getIdentifier(identifier, identifierClass);

				T t = tetraFunction.apply(v, body, a, b);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, V extends Identifier> RoutesBuilder<T, U>
		addCollectionPageItemCreator(
			TriFunction<V, Map<String, Object>, A, T> triFunction,
			Class<V> identifierClass, Class<A> aClass) {

		_routesImpl.setCreateItemFunction(
			httpServletRequest -> identifier -> body -> {
				A a = _provideClass(httpServletRequest, aClass);

				V v = _getIdentifier(identifier, identifierClass);

				T t = triFunction.apply(v, body, a);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G, H, I, V extends Identifier> RoutesBuilder<T, U>
		addCollectionPageItemCreator(
			UndecaFunction<V, Map<String, Object>, A, B, C, D, E, F, G, H, I, T>
				undecaFunction,
			Class<V> identifierClass, Class<A> aClass, Class<B> bClass,
			Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass,
			Class<G> gClass, Class<H> hClass, Class<I> iClass) {

		_routesImpl.setCreateItemFunction(
			httpServletRequest -> identifier -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);
				H h = _provideClass(httpServletRequest, hClass);
				I i = _provideClass(httpServletRequest, iClass);

				V v = _getIdentifier(identifier, identifierClass);

				T t = undecaFunction.apply(v, body, a, b, c, d, e, f, g, h, i);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A> RoutesBuilder<T, U> addCollectionPageItemGetter(
		BiFunction<U, A, T> biFunction, Class<A> aClass) {

		_routesImpl.setSingleModelFunction(
			httpServletRequest -> path -> {
				A a = _provideClass(httpServletRequest, aClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				return biFunction.andThen(
					_getCreateSingleModelFunction()
				).apply(
					u, a
				);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G, H, I> RoutesBuilder<T, U>
		addCollectionPageItemGetter(
			DecaFunction<U, A, B, C, D, E, F, G, H, I, T> decaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			Class<E> eClass, Class<F> fClass, Class<G> gClass, Class<H> hClass,
			Class<I> iClass) {

		_routesImpl.setSingleModelFunction(
			httpServletRequest -> path -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);
				H h = _provideClass(httpServletRequest, hClass);
				I i = _provideClass(httpServletRequest, iClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				return decaFunction.andThen(
					_getCreateSingleModelFunction()
				).apply(
					u, a, b, c, d, e, f, g, h, i
				);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G, H> RoutesBuilder<T, U>
		addCollectionPageItemGetter(
			EnneaFunction<U, A, B, C, D, E, F, G, H, T> enneaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			Class<E> eClass, Class<F> fClass, Class<G> gClass,
			Class<H> hClass) {

		_routesImpl.setSingleModelFunction(
			httpServletRequest -> path -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);
				H h = _provideClass(httpServletRequest, hClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				return enneaFunction.andThen(
					_getCreateSingleModelFunction()
				).apply(
					u, a, b, c, d, e, f, g, h
				);
			});

		return this;
	}

	@Override
	public RoutesBuilder<T, U> addCollectionPageItemGetter(
		Function<U, T> function) {

		_routesImpl.setSingleModelFunction(
			httpServletRequest -> path -> function.andThen(
				_getCreateSingleModelFunction()
			).apply(
				_convertIdentifier(path, _singleModelIdentifierClass)
			)
		);

		return this;
	}

	@Override
	public <A, B, C, D, E, F> RoutesBuilder<T, U> addCollectionPageItemGetter(
		HeptaFunction<U, A, B, C, D, E, F, T> heptaFunction, Class<A> aClass,
		Class<B> bClass, Class<C> cClass, Class<D> dClass, Class<E> eClass,
		Class<F> fClass) {

		_routesImpl.setSingleModelFunction(
			httpServletRequest -> path -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				return heptaFunction.andThen(
					_getCreateSingleModelFunction()
				).apply(
					u, a, b, c, d, e, f
				);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E> RoutesBuilder<T, U> addCollectionPageItemGetter(
		HexaFunction<U, A, B, C, D, E, T> hexaFunction, Class<A> aClass,
		Class<B> bClass, Class<C> cClass, Class<D> dClass, Class<E> eClass) {

		_routesImpl.setSingleModelFunction(
			httpServletRequest -> path -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				return hexaFunction.andThen(
					_getCreateSingleModelFunction()
				).apply(
					u, a, b, c, d, e
				);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G> RoutesBuilder<T, U>
		addCollectionPageItemGetter(
			OctaFunction<U, A, B, C, D, E, F, G, T> octaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			Class<E> eClass, Class<F> fClass, Class<G> gClass) {

		_routesImpl.setSingleModelFunction(
			httpServletRequest -> path -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				return octaFunction.andThen(
					_getCreateSingleModelFunction()
				).apply(
					u, a, b, c, d, e, f, g
				);
			});

		return this;
	}

	@Override
	public <A, B, C, D> RoutesBuilder<T, U> addCollectionPageItemGetter(
		PentaFunction<U, A, B, C, D, T> pentaFunction, Class<A> aClass,
		Class<B> bClass, Class<C> cClass, Class<D> dClass) {

		_routesImpl.setSingleModelFunction(
			httpServletRequest -> path -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				return pentaFunction.andThen(
					_getCreateSingleModelFunction()
				).apply(
					u, a, b, c, d
				);
			});

		return this;
	}

	@Override
	public <A, B, C> RoutesBuilder<T, U> addCollectionPageItemGetter(
		TetraFunction<U, A, B, C, T> tetraFunction, Class<A> aClass,
		Class<B> bClass, Class<C> cClass) {

		_routesImpl.setSingleModelFunction(
			httpServletRequest -> path -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				return tetraFunction.andThen(
					_getCreateSingleModelFunction()
				).apply(
					u, a, b, c
				);
			});

		return this;
	}

	@Override
	public <A, B> RoutesBuilder<T, U> addCollectionPageItemGetter(
		TriFunction<U, A, B, T> triFunction, Class<A> aClass, Class<B> bClass) {

		_routesImpl.setSingleModelFunction(
			httpServletRequest -> path -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				return triFunction.andThen(
					_getCreateSingleModelFunction()
				).apply(
					u, a, b
				);
			});

		return this;
	}

	@Override
	public <A> RoutesBuilder<T, U> addCollectionPageItemRemover(
		BiConsumer<U, A> biConsumer, Class<A> aClass) {

		_routesImpl.setDeleteItemConsumer(
			httpServletRequest -> path -> {
				U u = _convertIdentifier(path, _singleModelIdentifierClass);
				A a = _provideClass(httpServletRequest, aClass);

				biConsumer.accept(u, a);
			});

		return this;
	}

	@Override
	public RoutesBuilder<T, U> addCollectionPageItemRemover(
		Consumer<U> consumer) {

		_routesImpl.setDeleteItemConsumer(
			httpServletRequest -> path -> {
				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				consumer.accept(u);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G, H, I> RoutesBuilder<T, U>
		addCollectionPageItemRemover(
			DecaConsumer<U, A, B, C, D, E, F, G, H, I> decaConsumer,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			Class<E> eClass, Class<F> fClass, Class<G> gClass, Class<H> hClass,
			Class<I> iClass) {

		_routesImpl.setDeleteItemConsumer(
			httpServletRequest -> path -> {
				U u = _convertIdentifier(path, _singleModelIdentifierClass);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);
				H h = _provideClass(httpServletRequest, hClass);
				I i = _provideClass(httpServletRequest, iClass);

				decaConsumer.accept(u, a, b, c, d, e, f, g, h, i);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G, H> RoutesBuilder<T, U>
		addCollectionPageItemRemover(
			EnneaConsumer<U, A, B, C, D, E, F, G, H> enneaConsumer,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			Class<E> eClass, Class<F> fClass, Class<G> gClass,
			Class<H> hClass) {

		_routesImpl.setDeleteItemConsumer(
			httpServletRequest -> path -> {
				U u = _convertIdentifier(path, _singleModelIdentifierClass);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);
				H h = _provideClass(httpServletRequest, hClass);

				enneaConsumer.accept(u, a, b, c, d, e, f, g, h);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F> RoutesBuilder<T, U> addCollectionPageItemRemover(
		HeptaConsumer<U, A, B, C, D, E, F> heptaConsumer, Class<A> aClass,
		Class<B> bClass, Class<C> cClass, Class<D> dClass, Class<E> eClass,
		Class<F> fClass) {

		_routesImpl.setDeleteItemConsumer(
			httpServletRequest -> path -> {
				U u = _convertIdentifier(path, _singleModelIdentifierClass);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);

				heptaConsumer.accept(u, a, b, c, d, e, f);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E> RoutesBuilder<T, U> addCollectionPageItemRemover(
		HexaConsumer<U, A, B, C, D, E> hexaConsumer, Class<A> aClass,
		Class<B> bClass, Class<C> cClass, Class<D> dClass, Class<E> eClass) {

		_routesImpl.setDeleteItemConsumer(
			httpServletRequest -> path -> {
				U u = _convertIdentifier(path, _singleModelIdentifierClass);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);

				hexaConsumer.accept(u, a, b, c, d, e);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G> RoutesBuilder<T, U>
		addCollectionPageItemRemover(
			OctaConsumer<U, A, B, C, D, E, F, G> octaConsumer, Class<A> aClass,
			Class<B> bClass, Class<C> cClass, Class<D> dClass, Class<E> eClass,
			Class<F> fClass, Class<G> gClass) {

		_routesImpl.setDeleteItemConsumer(
			httpServletRequest -> path -> {
				U u = _convertIdentifier(path, _singleModelIdentifierClass);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);

				octaConsumer.accept(u, a, b, c, d, e, f, g);
			});

		return this;
	}

	@Override
	public <A, B, C, D> RoutesBuilder<T, U> addCollectionPageItemRemover(
		PentaConsumer<U, A, B, C, D> pentaConsumer, Class<A> aClass,
		Class<B> bClass, Class<C> cClass, Class<D> dClass) {

		_routesImpl.setDeleteItemConsumer(
			httpServletRequest -> path -> {
				U u = _convertIdentifier(path, _singleModelIdentifierClass);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);

				pentaConsumer.accept(u, a, b, c, d);
			});

		return this;
	}

	@Override
	public <A, B, C> RoutesBuilder<T, U> addCollectionPageItemRemover(
		TetraConsumer<U, A, B, C> tetraConsumer, Class<A> aClass,
		Class<B> bClass, Class<C> cClass) {

		_routesImpl.setDeleteItemConsumer(
			httpServletRequest -> path -> {
				U u = _convertIdentifier(path, _singleModelIdentifierClass);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);

				tetraConsumer.accept(u, a, b, c);
			});

		return this;
	}

	@Override
	public <A, B> RoutesBuilder<T, U> addCollectionPageItemRemover(
		TriConsumer<U, A, B> triConsumer, Class<A> aClass, Class<B> bClass) {

		_routesImpl.setDeleteItemConsumer(
			httpServletRequest -> path -> {
				U u = _convertIdentifier(path, _singleModelIdentifierClass);
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);

				triConsumer.accept(u, a, b);
			});

		return this;
	}

	@Override
	public RoutesBuilder<T, U> addCollectionPageItemUpdater(
		BiFunction<U, Map<String, Object>, T> biFunction) {

		_routesImpl.setUpdateItemFunction(
			httpServletRequest -> path -> body -> {
				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				T t = biFunction.apply(u, body);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G, H> RoutesBuilder<T, U>
		addCollectionPageItemUpdater(
			DecaFunction<U, Map<String, Object>, A, B, C, D, E, F, G, H, T>
				decaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			Class<E> eClass, Class<F> fClass, Class<G> gClass,
			Class<H> hClass) {

		_routesImpl.setUpdateItemFunction(
			httpServletRequest -> path -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);
				H h = _provideClass(httpServletRequest, hClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				T t = decaFunction.apply(u, body, a, b, c, d, e, f, g, h);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G> RoutesBuilder<T, U>
		addCollectionPageItemUpdater(
			EnneaFunction<U, Map<String, Object>, A, B, C, D, E, F, G, T>
				enneaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			Class<E> eClass, Class<F> fClass, Class<G> gClass) {

		_routesImpl.setUpdateItemFunction(
			httpServletRequest -> path -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				T t = enneaFunction.apply(u, body, a, b, c, d, e, f, g);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E> RoutesBuilder<T, U> addCollectionPageItemUpdater(
		HeptaFunction<U, Map<String, Object>, A, B, C, D, E, T> heptaFunction,
		Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
		Class<E> eClass) {

		_routesImpl.setUpdateItemFunction(
			httpServletRequest -> path -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				T t = heptaFunction.apply(u, body, a, b, c, d, e);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, D> RoutesBuilder<T, U> addCollectionPageItemUpdater(
		HexaFunction<U, Map<String, Object>, A, B, C, D, T> hexaFunction,
		Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass) {

		_routesImpl.setUpdateItemFunction(
			httpServletRequest -> path -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				T t = hexaFunction.apply(u, body, a, b, c, d);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F> RoutesBuilder<T, U> addCollectionPageItemUpdater(
		OctaFunction<U, Map<String, Object>, A, B, C, D, E, F, T> octaFunction,
		Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
		Class<E> eClass, Class<F> fClass) {

		_routesImpl.setUpdateItemFunction(
			httpServletRequest -> path -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				T t = octaFunction.apply(u, body, a, b, c, d, e, f);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C> RoutesBuilder<T, U> addCollectionPageItemUpdater(
		PentaFunction<U, Map<String, Object>, A, B, C, T> pentaFunction,
		Class<A> aClass, Class<B> bClass, Class<C> cClass) {

		_routesImpl.setUpdateItemFunction(
			httpServletRequest -> path -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				T t = pentaFunction.apply(u, body, a, b, c);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B> RoutesBuilder<T, U> addCollectionPageItemUpdater(
		TetraFunction<U, Map<String, Object>, A, B, T> tetraFunction,
		Class<A> aClass, Class<B> bClass) {

		_routesImpl.setUpdateItemFunction(
			httpServletRequest -> path -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				T t = tetraFunction.apply(u, body, a, b);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A> RoutesBuilder<T, U> addCollectionPageItemUpdater(
		TriFunction<U, Map<String, Object>, A, T> triFunction,
		Class<A> aClass) {

		_routesImpl.setUpdateItemFunction(
			httpServletRequest -> path -> body -> {
				A a = _provideClass(httpServletRequest, aClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				T t = triFunction.apply(u, body, a);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G, H, I> RoutesBuilder<T, U>
		addCollectionPageItemUpdater(
			UndecaFunction<U, Map<String, Object>, A, B, C, D, E, F, G, H, I, T>
				undecaFunction,
			Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
			Class<E> eClass, Class<F> fClass, Class<G> gClass, Class<H> hClass,
			Class<I> iClass) {

		_routesImpl.setUpdateItemFunction(
			httpServletRequest -> path -> body -> {
				A a = _provideClass(httpServletRequest, aClass);
				B b = _provideClass(httpServletRequest, bClass);
				C c = _provideClass(httpServletRequest, cClass);
				D d = _provideClass(httpServletRequest, dClass);
				E e = _provideClass(httpServletRequest, eClass);
				F f = _provideClass(httpServletRequest, fClass);
				G g = _provideClass(httpServletRequest, gClass);
				H h = _provideClass(httpServletRequest, hClass);
				I i = _provideClass(httpServletRequest, iClass);

				U u = _convertIdentifier(path, _singleModelIdentifierClass);

				T t = undecaFunction.apply(u, body, a, b, c, d, e, f, g, h, i);

				return _getCreateSingleModelFunction().apply(t);
			});

		return this;
	}

	@Override
	public Routes<T> build() {
		return _routesImpl;
	}

	private <V extends Identifier> V _convertIdentifier(
		Path path, Class<V> identifierClass) {

		Optional<? extends Identifier> optional = _identifierFunction.apply(
			identifierClass, path);

		return optional.map(
			convertedIdentifier -> (V)convertedIdentifier
		).orElseThrow(
			() -> new MustHavePathIdentifierMapper(identifierClass)
		);
	}

	private Function<T, SingleModel<T>> _getCreateSingleModelFunction() {
		return t -> new SingleModel<>(t, _modelClass);
	}

	private <V extends Identifier> V _getIdentifier(
		Identifier identifier, Class<V> identifierClass) {

		Class<? extends Identifier> clazz = identifier.getClass();

		if (!identifierClass.isAssignableFrom(clazz)) {
			throw new MustUseSameIdentifier(clazz, identifierClass);
		}

		return (V)identifier;
	}

	private <V> V _provideClass(
		HttpServletRequest httpServletRequest, Class<V> clazz) {

		Optional<?> optional = _provideClassFunction.apply(
			httpServletRequest
		).apply(
			clazz
		);

		return optional.map(
			provided -> (V)provided
		).orElseThrow(
			() -> new MustHaveProvider(clazz)
		);
	}

	private final BiFunction<Class<? extends Identifier>, Path,
		Optional<? extends Identifier>> _identifierFunction;
	private final Class<T> _modelClass;
	private final RequestFunction<Function<Class<?>, Optional<?>>>
		_provideClassFunction;
	private final RoutesImpl<T> _routesImpl = new RoutesImpl<>();
	private final Class<U> _singleModelIdentifierClass;

}