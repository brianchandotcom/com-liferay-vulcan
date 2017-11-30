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

package com.liferay.vulcan.documentation;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alejandro Hernández
 */
public class DocumentationTest {

	@Test
	public void testDocumentationWithEmptyValuesReturnEmpty() {
		Documentation documentation = new Documentation(
			__ -> Optional.empty(), __ -> Optional.empty());

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Optional<String> optionalTitle =
			documentation.getTitleFunction().apply(httpServletRequest);

		Optional<String> optionalDescription =
			documentation.getDescriptionFunction().apply(httpServletRequest);

		assertThat(optionalTitle, is(emptyOptional()));
		assertThat(optionalDescription, is(emptyOptional()));
	}

	@Test
	public void testDocumentationWithNonEmptyValuesReturnThem() {
		Documentation documentation = new Documentation(
			__ -> Optional.of(() -> "A"), __ -> Optional.of(() -> "B"));

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Optional<String> optionalTitle =
			documentation.getTitleFunction().apply(httpServletRequest);

		Optional<String> optionalDescription =
			documentation.getDescriptionFunction().apply(httpServletRequest);

		assertThat(optionalTitle, is(optionalWithValue(equalTo("A"))));
		assertThat(optionalDescription, is(optionalWithValue(equalTo("B"))));
	}

}