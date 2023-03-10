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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.grigoriliev.jsampler.juife.swing.EnhancedDialog;
import com.grigoriliev.jsampler.juife.swing.JuifeUtils;

import com.grigoriliev.jsampler.CC;
import com.grigoriliev.jsampler.JSI18n;
import com.grigoriliev.jsampler.JSUtils;
import com.grigoriliev.jsampler.LSConsoleModel;
import com.grigoriliev.jsampler.Prefs;
import com.grigoriliev.jsampler.swing.view.SHF;
import com.grigoriliev.jsampler.swing.view.std.JSAdvancedGeneralPropsDlg;
import com.grigoriliev.jsampler.swing.view.std.JSColorButton;
import com.grigoriliev.jsampler.swing.view.std.JSConnectionPropsPane;
import com.grigoriliev.jsampler.swing.view.std.JSDefaultsPropsPane;
import com.grigoriliev.jsampler.swing.view.std.JSGeneralProps;
import com.grigoriliev.jsampler.swing.view.std.JSLSConsolePropsPane;

import static com.grigoriliev.jsampler.JSPrefs.*;
import static com.grigoriliev.jsampler.classic.view.ClassicI18n.i18n;
import static com.grigoriliev.jsampler.classic.view.ClassicPrefs.preferences;


/**
 *
 * @author Grigor Iliev
 */
public class PrefsDlg extends EnhancedDialog {
	private final JTabbedPane tabbedPane = new JTabbedPane();
	
	private final GeneralPane genPane = new GeneralPane(this);
	private final ViewPane viewPane = new ViewPane();
	private final ConsolePane consolePane = new ConsolePane();
	private final JSConnectionPropsPane connectionPane = new JSConnectionPropsPane();
	private final JSDefaultsPropsPane defaultsPane;
	
	private final JButton btnApply = new JButton(i18n.getButtonLabel("apply"));
	private final JButton btnClose = new JButton(i18n.getButtonLabel("close"));
	
	
	public
	PrefsDlg(Frame frm) {
		super(frm, i18n.getLabel("PrefsDlg"), true);
		
		defaultsPane = new JSDefaultsPropsPane(this, Res.iconEdit16);
		
		initPrefsDlg();
		installListeners();
		
		setLocation(JuifeUtils.centerLocation(this, frm));
		
		int i = preferences().getIntProperty("PrefsDlg.tabIndex");
		
		if(i >= 0 && i < tabbedPane.getTabCount()) tabbedPane.setSelectedIndex(i);
	}
	
	private void
	initPrefsDlg() {
		JTabbedPane tp = tabbedPane;
		tp.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		tp.addTab(i18n.getLabel("PrefsDlg.tabGeneral"), genPane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabView"), viewPane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabConsole"), consolePane);
		
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		p.add(connectionPane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabBackend"), p);
		tp.addTab(i18n.getLabel("PrefsDlg.tabDefaults"), defaultsPane);
		
		tp.setAlignmentX(RIGHT_ALIGNMENT);
		
		// Set preferred size for Apply & Exit buttons
		Dimension d = JuifeUtils.getUnionSize(btnApply, btnClose);
		btnApply.setPreferredSize(d);
		btnClose.setPreferredSize(d);

		JPanel btnPane = new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		btnPane.add(btnApply);
		btnPane.add(Box.createRigidArea(new Dimension(5, 0)));
		btnPane.add(btnClose);
		btnPane.setAlignmentX(RIGHT_ALIGNMENT);
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		mainPane.add(tp);
		mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
		mainPane.add(btnPane);
		mainPane.setBorder(BorderFactory.createEmptyBorder(11, 12, 12, 12));
		
		getContentPane().add(mainPane);
		
		pack();
		setResizable(false);
	}
	
	private void
	installListeners() {
		btnApply.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onApply(); }
		});
		
		btnClose.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onExit(); }
		});
	}
	
	protected void
	onOk() {  onApply(); }
	
	protected void
	onCancel() { onExit(); }
	
	private void
	onApply() {
		genPane.apply();
		viewPane.apply();
		consolePane.apply();
		connectionPane.apply();
		defaultsPane.apply();
		
		preferences().setIntProperty("PrefsDlg.tabIndex", tabbedPane.getSelectedIndex());
		
		setVisible(false);
	}
	
	private void
	onExit() { setVisible(false); }
}

class GeneralPane extends JPanel {
	private final JCheckBox checkWindowSizeAndLocation =
		new JCheckBox(i18n.getLabel("GeneralPane.checkWindowSizeAndLocation"));
	
	private final JCheckBox checkLeftPaneState =
		new JCheckBox(i18n.getLabel("GeneralPane.checkLeftPaneState"));
	
	private final JCheckBox checkShowLSConsoleWhenRunScript =
		new JCheckBox(i18n.getLabel("GeneralPane.checkShowLSConsoleWhenRunScript"));
	
	private final JCheckBox checkShowVolumesInDecibels =
		new JCheckBox(i18n.getLabel("GeneralPane.checkShowVolumesInDecibels"));
	
	private final JSGeneralProps.MaxVolumePane maxVolPane = new JSGeneralProps.MaxVolumePane();
	
	private final JSGeneralProps.JSamplerHomePane jSamplerHomePane =
		new JSGeneralProps.JSamplerHomePane();
	
	private final RecentScriptsPane recentScriptsPane = new RecentScriptsPane();
	
	private final JButton btnAdvanced = new JButton(i18n.getButtonLabel("GeneralPane.btnAdvanced"));
	
	private final Dialog owner;
	
	public
	GeneralPane(Dialog owner) {
		this.owner = owner;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		checkWindowSizeAndLocation.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		checkWindowSizeAndLocation.setSelected(ClassicPrefs.getSaveWindowProperties());
		checkWindowSizeAndLocation.addItemListener(new ItemListener() {
			public void
			itemStateChanged(ItemEvent e) {
				boolean b = e.getStateChange() == e.SELECTED;
				//checkWindowSizeAndLocation.setEnabled(b);
			}
		});
		
		add(checkWindowSizeAndLocation);
		
		checkLeftPaneState.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		checkLeftPaneState.setSelected(ClassicPrefs.getSaveLeftPaneState());
		add(checkLeftPaneState);
		
		checkShowLSConsoleWhenRunScript.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		boolean b = preferences().getBoolProperty(SHOW_LS_CONSOLE_WHEN_RUN_SCRIPT);
		checkShowLSConsoleWhenRunScript.setSelected(b);
		
		add(checkShowLSConsoleWhenRunScript);
		
		b = preferences().getBoolProperty(VOL_MEASUREMENT_UNIT_DECIBEL);
		checkShowVolumesInDecibels.setSelected(b);
		
		add(checkShowVolumesInDecibels);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		add(maxVolPane);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		add(jSamplerHomePane);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		add(recentScriptsPane);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		p.add(p2);
		p.add(btnAdvanced);
		p.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		add(p);
		
		add(Box.createGlue());
		
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		btnAdvanced.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { showAdvancedProperties(); }
		});
	}
	
	private void
	showAdvancedProperties() {
		new JSAdvancedGeneralPropsDlg(owner).setVisible(true);
	}
	
	protected void
	apply() {
		maxVolPane.apply();
		
		ClassicPrefs.setSaveWindowProperties(checkWindowSizeAndLocation.isSelected());
		ClassicPrefs.setSaveLeftPaneState(checkLeftPaneState.isSelected());
		
		boolean b = checkShowLSConsoleWhenRunScript.isSelected();
		preferences().setBoolProperty(SHOW_LS_CONSOLE_WHEN_RUN_SCRIPT, b);
		
		b = checkShowVolumesInDecibels.isSelected();
		preferences().setBoolProperty(VOL_MEASUREMENT_UNIT_DECIBEL, b);
		
		int size = recentScriptsPane.getRecentScriptsSize();
		preferences().setIntProperty(RECENT_LSCP_SCRIPTS_SIZE, size);
		((MainFrame)SHF.getMainFrame()).updateRecentScriptsMenu();
		
		String s = jSamplerHomePane.getJSamplerHome();
		if(s.length() > 0 && !s.equals(CC.getJSamplerHome())) {
			JSUtils.changeJSamplerHome(s);
		}
	}
	
	private class RecentScriptsPane extends JSGeneralProps.RecentScriptsPane {
		@Override
		protected void
		clearRecentScripts() {
			((MainFrame)SHF.getMainFrame()).clearRecentScripts();
		}
	}
}

class ViewPane extends JPanel {
	private final JLabel lIfaceLanguage =
		new JLabel(i18n.getLabel("ViewPane.lIfaceLanguage"));
	private final JComboBox cbIfaceLanguage = new JComboBox();
	
	private final JLabel lIfaceFont =
		new JLabel(i18n.getLabel("ViewPane.lIfaceFont"));
	private final JComboBox cbIfaceFont = new JComboBox();
	
	private final JCheckBox checkBorderColor =
		new JCheckBox(i18n.getLabel("ViewPane.channelBorderColor"));
	private final JSColorButton btnBorderColor = new JSColorButton(Color.WHITE);
	
	private final JCheckBox checkHlChnBorderColor =
		new JCheckBox(i18n.getLabel("ViewPane.checkHlChnBorderColor"));
	private final JSColorButton btnHlChnBorderColor = new JSColorButton(Color.WHITE);
	
	private final JCheckBox checkSelChnBgColor =
		new JCheckBox(i18n.getLabel("ViewPane.checkSelChnBgColor"));
	private final JSColorButton btnSelChnBgColor = new JSColorButton(Color.WHITE);
	
	private final JCheckBox checkHlChnBgColor =
		new JCheckBox(i18n.getLabel("ViewPane.checkHlChnBgColor"));
	private final JSColorButton btnHlChnBgColor = new JSColorButton(Color.WHITE);
	
	public
	ViewPane() { initViewPane(); }
	
	private void
	initViewPane() {
		cbIfaceLanguage.setMaximumSize (
			new Dimension(Short.MAX_VALUE, cbIfaceLanguage.getPreferredSize().height)
		);
		
		for(Locale l : JSI18n.getAvailableLocales()) {
			LocaleBox box = new LocaleBox(l);
			cbIfaceLanguage.addItem(box);
			if (	l.getLanguage().equals(Prefs.getInterfaceLanguage()) &&
				l.getCountry().equals(Prefs.getInterfaceCountry())
			) cbIfaceLanguage.setSelectedItem(box);
		}
		
		cbIfaceFont.setMaximumSize (
			new Dimension(Short.MAX_VALUE, cbIfaceFont.getPreferredSize().height)
		);
		
		cbIfaceFont.addItem("[Default]");
		
		String[] fontS =
		GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		for(String f : fontS) cbIfaceFont.addItem(f);
		
		if(Prefs.getInterfaceFont() == null) cbIfaceFont.setSelectedItem("[Default]");
		else cbIfaceFont.setSelectedItem(Prefs.getInterfaceFont());
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel ifacePane = new JPanel();
		ifacePane.setLayout(new BoxLayout(ifacePane, BoxLayout.X_AXIS));
		ifacePane.add(lIfaceLanguage);
		ifacePane.add(Box.createRigidArea(new Dimension(5, 0)));
		ifacePane.add(cbIfaceLanguage);
		
		add(ifacePane);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		JPanel fontPane = new JPanel();
		fontPane.setLayout(new BoxLayout(fontPane, BoxLayout.X_AXIS));
		fontPane.add(lIfaceFont);
		fontPane.add(Box.createRigidArea(new Dimension(5, 0)));
		fontPane.add(cbIfaceFont);
		
		add(fontPane);
		add(Box.createRigidArea(new Dimension(0, 6)));
		add(createCustomColorsPane());
		
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
	}
	
	private JPanel
	createCustomColorsPane() {
		btnBorderColor.setColor(ClassicPrefs.getChannelBorderColor());
		btnBorderColor.setEnabled(ClassicPrefs.getCustomChannelBorderColor());
		
		checkBorderColor.setSelected(ClassicPrefs.getCustomChannelBorderColor());
		
		checkBorderColor.addItemListener(new ItemListener() {
			public void
			itemStateChanged(ItemEvent e) {
				boolean b = e.getStateChange() == e.SELECTED;
				btnBorderColor.setEnabled(b);
			}
		});
		
		btnHlChnBorderColor.setColor(ClassicPrefs.getChannelBorderHlColor());
		btnHlChnBorderColor.setEnabled(ClassicPrefs.getCustomChannelBorderHlColor());
		
		checkHlChnBorderColor.setSelected(ClassicPrefs.getCustomChannelBorderHlColor());
		
		checkHlChnBorderColor.addItemListener(new ItemListener() {
			public void
			itemStateChanged(ItemEvent e) {
				boolean b = e.getStateChange() == e.SELECTED;
				btnHlChnBorderColor.setEnabled(b);
			}
		});
		
		Color color = ClassicPrefs.getSelectedChannelBgColor();
		if(color == null) color = new Color(getBackground().getRGB());
		btnSelChnBgColor.setColor(color);
		btnSelChnBgColor.setEnabled(ClassicPrefs.getCustomSelectedChannelBgColor());
		
		checkSelChnBgColor.setSelected(ClassicPrefs.getCustomSelectedChannelBgColor());
		
		checkSelChnBgColor.addItemListener(new ItemListener() {
			public void
			itemStateChanged(ItemEvent e) {
				boolean b = e.getStateChange() == e.SELECTED;
				btnSelChnBgColor.setEnabled(b);
			}
		});
		
		color = ClassicPrefs.getHighlightedChannelBgColor();
		if(color == null) color = new Color(getBackground().getRGB());
		btnHlChnBgColor.setColor(color);
		btnHlChnBgColor.setEnabled(ClassicPrefs.getCustomHighlightedChannelBgColor());
		
		checkHlChnBgColor.setSelected(ClassicPrefs.getCustomHighlightedChannelBgColor());
		
		checkHlChnBgColor.addItemListener(new ItemListener() {
			public void
			itemStateChanged(ItemEvent e) {
				boolean b = e.getStateChange() == e.SELECTED;
				btnHlChnBgColor.setEnabled(b);
			}
		});
		
		JButton btnDefaults = new JButton("Reset to defaults");
		btnDefaults.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				ClassicPrefs.setChannelBorderColor(null);
				btnBorderColor.setColor(ClassicPrefs.getChannelBorderColor());
				
				ClassicPrefs.setChannelBorderHlColor(null);
				btnHlChnBorderColor.setColor(ClassicPrefs.getChannelBorderHlColor());
				
				ClassicPrefs.setSelectedChannelBgColor(null);
				btnSelChnBgColor.setColor(ClassicPrefs.getSelectedChannelBgColor());
				
				ClassicPrefs.setHighlightedChannelBgColor(null);
				btnHlChnBgColor.setColor(ClassicPrefs.getHighlightedChannelBgColor());
			}
		});
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel ccp = new JPanel();
		ccp.setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 3, 3, 3);
		gridbag.setConstraints(checkBorderColor, c);
		ccp.add(checkBorderColor); 
		
		
		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(btnBorderColor, c);
		ccp.add(btnBorderColor);
		
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(checkHlChnBorderColor, c);
		ccp.add(checkHlChnBorderColor);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(btnHlChnBorderColor, c);
		ccp.add(btnHlChnBorderColor);
		
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(checkSelChnBgColor, c);
		ccp.add(checkSelChnBgColor);
		
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(btnSelChnBgColor, c);
		ccp.add(btnSelChnBgColor);
		
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(checkHlChnBgColor, c);
		ccp.add(checkHlChnBgColor);
		
		c.gridx = 1;
		c.gridy = 3;
		gridbag.setConstraints(btnHlChnBgColor, c);
		ccp.add(btnHlChnBgColor);
		
		JPanel p = new JPanel();
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 6));
		p.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		p.add(Box.createGlue());
		p.add(btnDefaults);
		p.add(Box.createGlue());
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(p, c);
		ccp.add(p);
			
		ccp.setBorder (
			BorderFactory.createTitledBorder(i18n.getLabel("ViewPane.CustomColorsPane"))
		);
		
		ccp.setMaximumSize(new Dimension(Short.MAX_VALUE, ccp.getPreferredSize().height));
		
		return ccp;
	}
	
	private String
	getInterfaceLanguage() {
		LocaleBox box = (LocaleBox)cbIfaceLanguage.getSelectedItem();
		if(box == null) return null;
		return box.getLocale().getLanguage();
	}
	
	private String
	getInterfaceCountry() {
		LocaleBox box = (LocaleBox)cbIfaceLanguage.getSelectedItem();
		if(box == null) return null;
		return box.getLocale().getCountry();
	}
	
	private String
	getInterfaceFontName() { return cbIfaceFont.getSelectedItem().toString(); }
	
	protected void
	apply() {
		boolean b = Prefs.setInterfaceLanguage(getInterfaceLanguage());
		boolean b2 = Prefs.setInterfaceCountry(getInterfaceCountry());
		if (b || b2) JOptionPane.showMessageDialog (
			this,
			i18n.getMessage("PrefsDlg.ifaceChangeInfo", "JS Classic"),
			null,
			JOptionPane.INFORMATION_MESSAGE
		);
		
		b = false;
		String fontName = getInterfaceFontName();
		if(fontName.equals("[Default]")) {
			b = Prefs.setInterfaceFont(null);
		} else if(Prefs.setInterfaceFont(fontName)) {
			CC.getViewConfig().setUIDefaultFont(fontName);
			b = true;
		}
		
		if(b) JOptionPane.showMessageDialog (
			this, 
			i18n.getMessage("PrefsDlg.ifaceFontChangeInfo", "JS Classic"),
			null,
			JOptionPane.INFORMATION_MESSAGE
		);
		
		///***///
		
		b = checkBorderColor.isSelected();
		ClassicPrefs.setCustomChannelBorderColor(b);
		if(b) ClassicPrefs.setChannelBorderColor(btnBorderColor.getColor());
		
		Color c;
		if(b) c = ClassicPrefs.getChannelBorderColor();
		else c = ClassicPrefs.getDefaultChannelBorderColor();
		Channel.setBorderColor(c);
		
		b = checkHlChnBorderColor.isSelected();
		ClassicPrefs.setCustomChannelBorderHlColor(b);
		if(b) ClassicPrefs.setChannelBorderHlColor(btnHlChnBorderColor.getColor());
		
		if(b) c = ClassicPrefs.getChannelBorderHlColor();
		else c = ClassicPrefs.getDefaultChannelBorderHlColor();
		Channel.setBorderHighlightedColor(c);
		
		b = checkSelChnBgColor.isSelected();
		ClassicPrefs.setCustomSelectedChannelBgColor(b);
		if(b) ClassicPrefs.setSelectedChannelBgColor(btnSelChnBgColor.getColor());
		
		if(b) c = ClassicPrefs.getSelectedChannelBgColor();
		else c = new Color(getBackground().getRGB());
		if(c == null) c = new Color(getBackground().getRGB());
		Channel.setSelectedChannelBgColor(c);
		
		b = checkHlChnBgColor.isSelected();
		ClassicPrefs.setCustomHighlightedChannelBgColor(b);
		if(b) ClassicPrefs.setHighlightedChannelBgColor(btnHlChnBgColor.getColor());
		
		if(b) c = ClassicPrefs.getHighlightedChannelBgColor();
		else c = new Color(getBackground().getRGB());
		if(c == null) c = new Color(getBackground().getRGB());
		Channel.setHighlightedChannelBgColor(c);
	}
	
	class LocaleBox {
		private Locale locale;
		
		LocaleBox(Locale locale) { this.locale = locale; }
		
		public Locale
		getLocale() { return locale; }
		
		@Override
		public String
		toString() { return locale.getDisplayLanguage(JSI18n.i18n.getCurrentLocale()); }
	}
}

class ConsolePane extends JSLSConsolePropsPane {
	@Override
	protected void
	clearConsoleHistory() {
		MainFrame frame = (MainFrame)SHF.getMainFrame();
		frame.getLSConsoleModel().clearCommandHistory();
	}
	
	@Override
	protected void
	apply() {
		super.apply();
		
		MainFrame frame = (MainFrame)SHF.getMainFrame();
		
		LSConsoleModel model = frame.getLSConsoleModel();
		model.setCommandHistorySize(preferences().getIntProperty(LS_CONSOLE_HISTSIZE));
		
		int c = preferences().getIntProperty(LS_CONSOLE_TEXT_COLOR);
		frame.setLSConsoleTextColor(new Color(c));
		
		c = preferences().getIntProperty(LS_CONSOLE_BACKGROUND_COLOR);
		frame.setLSConsoleBackgroundColor(new Color(c));
		
		c = preferences().getIntProperty(LS_CONSOLE_NOTIFY_COLOR);
		frame.setLSConsoleNotifyColor(new Color(c));
		
		c = preferences().getIntProperty(LS_CONSOLE_WARNING_COLOR);
		frame.setLSConsoleWarningColor(new Color(c));
		
		c = preferences().getIntProperty(LS_CONSOLE_ERROR_COLOR);
		frame.setLSConsoleErrorColor(new Color(c));
	}
}
