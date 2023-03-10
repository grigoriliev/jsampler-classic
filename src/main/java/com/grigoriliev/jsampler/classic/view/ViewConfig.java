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

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.grigoriliev.jsampler.JSPrefs;

import com.grigoriliev.jsampler.view.InstrumentsDbTableView;
import com.grigoriliev.jsampler.view.InstrumentsDbTreeView;
import com.grigoriliev.jsampler.view.BasicIconSet;


/**
 *
 * @author Grigor Iliev
 */
public class ViewConfig extends com.grigoriliev.jsampler.swing.view.ViewConfig {
	private InstrumentsDbTreeView instrumentsDbTreeView = new TreeView();
	private InstrumentsDbTableView instrumentsDbTableView = new TableView();
	private IconSet basicIconSet = new IconSet();
	
	/** Creates a new instance of <code>ViewConfig</code> */
	public
	ViewConfig() {
		
	}
	
	@Override
	public JSPrefs
	preferences() { return ClassicPrefs.preferences(); }
	
	@Override
	public InstrumentsDbTreeView
	getInstrumentsDbTreeView() { return instrumentsDbTreeView; }
	
	@Override
	public InstrumentsDbTableView
	getInstrumentsDbTableView() { return instrumentsDbTableView; }
	
	@Override
	public com.grigoriliev.jsampler.view.SamplerBrowserView
	getSamplerBrowserView() { return null; }
	
	@Override
	public BasicIconSet
	getBasicIconSet() { return basicIconSet; }
	
	private class TreeView implements InstrumentsDbTreeView {
		@Override
		public Icon
		getRootIcon() { return Res.iconDb16; }
		
		@Override
		public Icon
		getClosedIcon() { return Res.iconFolder16; }
	
		@Override
		public Icon
		getOpenIcon() { return Res.iconFolderOpen16; }
	
		@Override
		public Icon
		getInstrumentIcon() { return Res.iconInstrument16; }
	
		@Override
		public Icon
		getGigInstrumentIcon() { return Res.iconInstrument16; }
	}
	
	private static class TableView implements InstrumentsDbTableView {
		@Override
		public Icon
		getFolderIcon() { return Res.iconFolder16; }
	
		@Override
		public Icon
		getInstrumentIcon() { return Res.iconInstrument16; }
	
		@Override
		public Icon
		getGigInstrumentIcon() { return Res.iconInstrument16; }
	}
	
	private class IconSet implements BasicIconSet {
		@Override
		public ImageIcon
		getApplicationIcon() { return Res.appIcon; }
		
		@Override
		public Icon
		getBack16Icon() { return Res.iconBack16; }
	
		@Override
		public Icon
		getUp16Icon() { return Res.iconUp16; }
	
		@Override
		public Icon
		getForward16Icon() { return Res.iconForward16; }
		
		@Override
		public Icon
		getReload16Icon() { return Res.iconReload16; }
		
		@Override
		public Icon
		getPreferences16Icon() { return Res.iconPreferences16; }
		
		@Override
		public Icon
		getWarning32Icon() { return Res.iconWarning32; }
		
		@Override
		public Icon
		getQuestion32Icon() { return Res.iconQuestion32; }
	}
	
	@Override
	public boolean
	getInstrumentsDbSupport() { return true; }
}
