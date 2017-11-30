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

package com.liferay.vulcan.resource;

import aQute.bnd.annotation.ProviderType;

import com.liferay.vulcan.alias.routes.CreateItemFunction;
import com.liferay.vulcan.alias.routes.DeleteItemConsumer;
import com.liferay.vulcan.alias.routes.GetItemFunction;
import com.liferay.vulcan.alias.routes.GetPageFunction;
import com.liferay.vulcan.alias.routes.UpdateItemFunction;

import java.util.Optional;

/**
 * Holds information about the routes supported for a {@link
 * CollectionResource}.
 *
 * <p>
 * This interface's methods return functions to get the different endpoints of
 * the collection resource. You should always use a {@link
 * com.liferay.vulcan.resource.builder.RoutesBuilder} to create instances of
 * this interface.
 * </p>
 *
 * @author Alejandro Hernández
 * @see    com.liferay.vulcan.resource.builder.RoutesBuilder
 */
@ProviderType
public interface Routes<T> {

	/**
	 * Returns the function that is used to create the single model of a {@link
	 * CollectionResource}, if the endpoint was added through the {@link
	 * com.liferay.vulcan.resource.builder.RoutesBuilder} and the function
	 * therefore exists. Returns {@code Optional#empty()} otherwise.
	 *
	 * @return the function that uses a POST request to create the single model,
	 *         if the function exists; {@code Optional#empty()} otherwise
	 */
	public Optional<CreateItemFunction<T>> getCreateItemFunctionOptional();

	/**
	 * Returns the function used to remove a single model of a {@link
	 * CollectionResource}, if the endpoint was added through the {@link
	 * com.liferay.vulcan.resource.builder.RoutesBuilder} and the function
	 * therefore exists. Returns {@code Optional#empty()} otherwise.
	 *
	 * @return the function used to remove a single model, if the function
	 *         exists; {@code Optional#empty()} otherwise
	 */
	public Optional<DeleteItemConsumer> getDeleteConsumerOptional();

	/**
	 * Returns the function used to obtain the page of a {@link
	 * CollectionResource}, if the endpoint was added through the {@link
	 * com.liferay.vulcan.resource.builder.RoutesBuilder} and the function
	 * therefore exists. Returns {@code Optional#empty()} otherwise.
	 *
	 * @return the function used to create the page, if the function exists;
	 *         {@code Optional#empty()} otherwise
	 */
	public Optional<GetPageFunction<T>> getGetPageFunctionOptional();

	/**
	 * Returns the function used to retrieve the single model of a {@link
	 * CollectionResource}, if the endpoint was added through the {@link
	 * com.liferay.vulcan.resource.builder.RoutesBuilder} and the function
	 * therefore exists. Returns {@code Optional#empty()} otherwise.
	 *
	 * @return the function that uses a GET request to retrieve the single
	 *         model, if the function exists; {@code Optional#empty()} otherwise
	 */
	public Optional<GetItemFunction<T>> getItemFunctionOptional();

	/**
	 * Returns the function used to update the single model of a {@link
	 * CollectionResource}, if the endpoint was added through the {@link
	 * com.liferay.vulcan.resource.builder.RoutesBuilder} and the function
	 * therefore exists. Returns {@code Optional#empty()} otherwise.
	 *
	 * @return the function used to update the single model, if the function
	 *         exists; {@code Optional#empty()} otherwise
	 */
	public Optional<UpdateItemFunction<T>> getUpdateItemFunctionOptional();

}