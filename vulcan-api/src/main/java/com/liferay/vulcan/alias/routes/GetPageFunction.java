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

package com.liferay.vulcan.alias.routes;

import com.liferay.vulcan.alias.RequestFunction;
import com.liferay.vulcan.pagination.Page;
import com.liferay.vulcan.resource.identifier.Identifier;
import com.liferay.vulcan.uri.Path;

import java.util.function.Function;

/**
 * Defines a type alias for a function that can be used to obtain a collection
 * page.
 *
 * @author Alejandro Hernández
 * @review
 */
@FunctionalInterface
public interface GetPageFunction<T>
	extends RequestFunction<Function<Path, Function<Identifier, Page<T>>>> {
}