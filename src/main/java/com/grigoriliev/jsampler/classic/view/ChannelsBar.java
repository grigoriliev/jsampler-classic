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

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToolBar;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.grigoriliev.jsampler.CC;

import com.grigoriliev.jsampler.event.SamplerAdapter;
import com.grigoriliev.jsampler.event.SamplerEvent;
import com.grigoriliev.jsampler.swing.view.std.StdUtils;

import static com.grigoriliev.jsampler.classic.view.A4n.a4n;
import static com.grigoriliev.jsampler.classic.view.ClassicI18n.i18n;
import static com.grigoriliev.jsampler.classic.view.ClassicPrefs.preferences;
import static com.grigoriliev.jsampler.JSPrefs.*;


/**
 *
 * @author Grigor Iliev
 */
public class ChannelsBar extends JToolBar {
	private final JButton btnNew = new ToolbarButton(A4n.newChannel);
	private final JButton btnDuplicate = new ToolbarButton(a4n.duplicateChannels);
	private final JButton btnUp = new ToolbarButton(a4n.moveChannelsUp);
	private final JButton btnDown = new ToolbarButton(a4n.moveChannelsDown);
	private final JButton btnRemove = new ToolbarButton(a4n.removeChannels);
	
	private final JButton btnNewTab = new ToolbarButton(A4n.newChannelsTab);
	private final JButton btnRemoveTab = new ToolbarButton(A4n.closeChannelsTab);
	private final JButton btnTabMoveLeft = new ToolbarButton(A4n.moveTab2Left);
	private final JButton btnTabMoveRight = new ToolbarButton(A4n.moveTab2Right);
	
	private final JLabel lVolume = new JLabel(Res.iconVolume22);
	private final JSlider slVolume = StdUtils.createVolumeSlider();
	
	
	/**
	 * Creates a new instance of ChannelsBar
	 */
	public ChannelsBar() {
		super(i18n.getLabel("ChanelsBar.name"));
		setFloatable(false);
		
		add(lVolume);
		
		Dimension d = new Dimension(200, slVolume.getPreferredSize().height);
		slVolume.setPreferredSize(d);
		slVolume.setMaximumSize(d);
		slVolume.setOpaque(false);
		add(slVolume);
		
		addSeparator();
		
		add(btnNew);
		add(btnDuplicate);
		add(btnRemove);
		add(btnUp);
		add(btnDown);
		
		addSeparator();
		
		add(btnNewTab);
		add(btnRemoveTab);
		add(btnTabMoveLeft);
		add(btnTabMoveRight);
		
		int i = preferences().getIntProperty(MAXIMUM_MASTER_VOLUME);
		slVolume.setMaximum(i);
		String s = MAXIMUM_MASTER_VOLUME;
		preferences().addPropertyChangeListener(s, new PropertyChangeListener() {
			public void
			propertyChange(PropertyChangeEvent e) {
				int j = preferences().getIntProperty(MAXIMUM_MASTER_VOLUME);
				slVolume.setMaximum(j);
			}
		});
		
		slVolume.addChangeListener(new ChangeListener() {
			public void
			stateChanged(ChangeEvent e) { setVolume(); }
		});
		
		CC.getSamplerModel().addSamplerListener(new SamplerAdapter() {
			public void
			volumeChanged(SamplerEvent e) { updateVolume(); }
		});
		
		updateVolume();
	}
	
	private void
	setVolume() {
		int volume = slVolume.getValue();
		String s = i18n.getLabel("ChannelsBar.volume", volume);
		
		if(slVolume.getValueIsAdjusting()) return;
		
		int vol = (int)(CC.getSamplerModel().getVolume() * 100);
		
		if(vol == slVolume.getValue()) return;
		
		/*
		 * If the model's volume is not equal to the slider
		 * value we assume that the change is due to user input.
		 * So we must update the volume at the backend too.
		 */
		float v = slVolume.getValue();
		v /= 100;
		CC.getSamplerModel().setBackendVolume(v);
	}
	
	private void
	updateVolume() {
		slVolume.setValue((int)(CC.getSamplerModel().getVolume() * 100));
	}
}
