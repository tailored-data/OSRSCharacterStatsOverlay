/*
 * Copyright (c) 2026, Taylor Burks
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.tailoreddata.characterstats;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CharacterStatsConfigTest
{
	/**
	 * RuneLite hands plugins their config as a JDK dynamic proxy, and a proxy class
	 * lives in its own module. It therefore cannot reach a package-private type, so
	 * every type named in this interface's signatures has to be public — otherwise
	 * the first call to that getter throws IllegalAccessError, which surfaces as a
	 * "Client error" once an overlay reads the setting.
	 */
	@Test
	public void everyGetterIsCallableThroughADynamicProxy() throws Throwable
	{
		final CharacterStatsConfig config = (CharacterStatsConfig) Proxy.newProxyInstance(
			CharacterStatsConfig.class.getClassLoader(),
			new Class<?>[]{CharacterStatsConfig.class},
			(proxy, method, args) -> MethodHandles
				.privateLookupIn(CharacterStatsConfig.class, MethodHandles.lookup())
				.unreflectSpecial(method, CharacterStatsConfig.class)
				.bindTo(proxy)
				.invokeWithArguments(args == null ? new Object[0] : args));

		for (Method method : CharacterStatsConfig.class.getDeclaredMethods())
		{
			if (method.getParameterCount() != 0)
			{
				continue;
			}

			assertNotNull(method.getName() + "() returned null", method.invoke(config));
		}
	}

	/**
	 * The same constraint, stated directly against the return types so a new setting
	 * of a non-public type fails here with an obvious message.
	 */
	@Test
	public void everyReturnTypeIsPublic()
	{
		for (Method method : CharacterStatsConfig.class.getDeclaredMethods())
		{
			final Class<?> returnType = method.getReturnType();
			assertTrue(
				returnType.getName() + " (returned by " + method.getName() + "()) must be public"
					+ " so RuneLite's config proxy can access it",
				returnType.isPrimitive() || Modifier.isPublic(returnType.getModifiers()));
		}
	}
}
