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
import com.liferay.vulcan.pagination.SingleModel;
import com.liferay.vulcan.uri.Path;

import java.util.Map;
import java.util.function.Function;

/**
 * Defines a type alias for a function that can be used to update a collection
 * item.
 *
 * @author Alejandro Hernández
 * @review
 */
@FunctionalInterface
public interface UpdateItemFunction<T>
	extends RequestFunction
		<Function<Path, Function<Map<String, Object>, SingleModel<T>>>> {
}