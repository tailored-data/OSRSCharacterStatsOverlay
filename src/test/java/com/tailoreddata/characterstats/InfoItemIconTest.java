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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * The sidebar loads its icons off the classpath by file name, so a typo or a
 * missing resource would only show up as a blank row in the client. These read
 * every icon the same way the panel does.
 */
public class InfoItemIconTest
{
	private static final int ROW_ICON_SIZE = 16;
	private static final int NAV_ICON_SIZE = 20;

	@Test
	public void everyInfoItemHasA16pxIcon() throws IOException
	{
		for (InfoItem item : InfoItem.values())
		{
			final String path = "icons/" + item.getIconFile();
			final BufferedImage icon = read(path);

			assertEquals(path + " should be " + ROW_ICON_SIZE + "px wide", ROW_ICON_SIZE, icon.getWidth());
			assertEquals(path + " should be " + ROW_ICON_SIZE + "px tall", ROW_ICON_SIZE, icon.getHeight());
		}
	}

	@Test
	public void theSidebarButtonHasAnIcon() throws IOException
	{
		final BufferedImage icon = read("icons/panel_icon.png");
		assertEquals(NAV_ICON_SIZE, icon.getWidth());
		assertEquals(NAV_ICON_SIZE, icon.getHeight());
	}

	private static BufferedImage read(String path) throws IOException
	{
		try (InputStream in = InfoItem.class.getResourceAsStream(path))
		{
			assertNotNull("missing icon resource " + path, in);

			final BufferedImage image = ImageIO.read(in);
			assertNotNull(path + " is not a readable image", image);
			return image;
		}
	}
}
