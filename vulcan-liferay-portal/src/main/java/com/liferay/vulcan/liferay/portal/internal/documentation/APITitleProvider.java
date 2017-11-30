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

package com.liferay.vulcan.liferay.portal.internal.documentation;

import com.liferay.vulcan.documentation.APITitle;
import com.liferay.vulcan.provider.Provider;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * Lets resources provide {@link APITitle} as a parameter in the methods of
 * {@link com.liferay.vulcan.resource.builder.RoutesBuilder}.
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class APITitleProvider implements Provider<APITitle> {

	@Override
	public APITitle createContext(HttpServletRequest httpServletRequest) {
		return () -> "Liferay Portal API";
	}

}