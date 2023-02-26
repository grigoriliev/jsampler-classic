/*
 *   JSampler - a front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2023 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software: you can redistribute it and/or modify it under
 *   the terms of the GNU General Public License as published by the Free
 *   Software Foundation, either version 3 of the License, or (at your option)
 *   any later version.
 *
 *   JSampler is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *   FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *   more details.
 *
 *   You should have received a copy of the GNU General Public License along
 *   with JSampler. If not, see <https://www.gnu.org/licenses/>. 
 */

package com.grigoriliev.jsampler.classic.view;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class manages the locale-specific data of JS Classic.
 * @author Grigor Iliev
 */
public class ClassicI18n extends com.grigoriliev.jsampler.juife.I18n {
	/** Provides the locale-specific data of JS Classic. */
	public static ClassicI18n i18n = new ClassicI18n();
	
	private
	ClassicI18n() {
		setButtonsBundle("com.grigoriliev.jsampler.classic.view.langprops.ButtonsLabelsBundle");
		setErrorsBundle("com.grigoriliev.jsampler.classic.view.langprops.ErrorsBundle");
		setLabelsBundle("com.grigoriliev.jsampler.classic.view.langprops.LabelsBundle");
		setMenusBundle("com.grigoriliev.jsampler.classic.view.langprops.MenuLabelsBundle");
		setMessagesBundle("com.grigoriliev.jsampler.classic.view.langprops.MessagesBundle");
	}

	@Override
	protected ResourceBundle getBundle(String baseName, Locale locale) {
		return ResourceBundle.getBundle(baseName, locale, ClassicI18n.class.getModule());
	}
}
