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
import java.awt.Dimension;
import java.awt.MediaTracker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.URL;

import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import com.grigoriliev.jsampler.juife.swing.InformationDialog;
import com.grigoriliev.jsampler.juife.swing.JuifeUtils;
import com.grigoriliev.jsampler.juife.swing.NavigationPage;

import com.grigoriliev.jsampler.AudioDeviceModel;
import com.grigoriliev.jsampler.CC;
import com.grigoriliev.jsampler.HF;

import com.grigoriliev.jsampler.event.AudioDeviceAdapter;
import com.grigoriliev.jsampler.event.AudioDeviceEvent;
import com.grigoriliev.jsampler.event.ListEvent;
import com.grigoriliev.jsampler.event.ListListener;
import com.grigoriliev.jsampler.event.ParameterEvent;
import com.grigoriliev.jsampler.event.ParameterListener;

import com.grigoriliev.jsampler.swing.view.NumberCellEditor;
import com.grigoriliev.jsampler.swing.view.ParameterTable;
import com.grigoriliev.jsampler.swing.view.SHF;

import com.grigoriliev.jsampler.jlscp.AudioOutputChannel;
import com.grigoriliev.jsampler.jlscp.AudioOutputDevice;
import com.grigoriliev.jsampler.jlscp.Parameter;

import static com.grigoriliev.jsampler.classic.view.ClassicI18n.i18n;
import static com.grigoriliev.jsampler.classic.view.AudioDevicesTableModel.*;


/**
 *
 * @author Grigor Iliev
 */
public class AudioDevicesPage extends NavigationPage {
	private final Action duplicateAudioDevice = new DuplicateAudioDevice();
	private final Action removeAudioDevice = new RemoveAudioDevice();
	private final Action audioDeviceProps = new AudioDeviceProps();
	
	private final ToolbarButton btnNewDevice = new ToolbarButton(A4n.addAudioDevice);
	private final ToolbarButton btnDuplicateDevice = new ToolbarButton(duplicateAudioDevice);
	private final ToolbarButton btnRemoveDevice = new ToolbarButton(removeAudioDevice);
	private final ToolbarButton btnDeviceProps = new ToolbarButton(audioDeviceProps);
	
	private final JTable devicesTable = new JTable(new AudioDevicesTableModel());
	
	private final JLabel lChannels = new JLabel(i18n.getLabel("AudioDevicesPage.lChannels"));
	private final JComboBox cbChannels = new JComboBox();
	
	ParameterTable channelParamTable = new ParameterTable();
	
	
	/** Creates a new instance of <code>AudioDevicesPage</code> */
	public
	AudioDevicesPage() {
		setTitle(i18n.getLabel("AudioDevicesPage.title"));
		
		cbChannels.setEnabled(false);
		
		TableColumn tc = devicesTable.getColumnModel().getColumn(ACTIVE_COLUMN_INDEX);
		tc.setPreferredWidth(tc.getMinWidth());
		
		NumberCellEditor nce = new NumberCellEditor();
		nce.setMinimum(0);
		nce.setMaximum(255);
		tc = devicesTable.getColumnModel().getColumn(CHANNELS_COLUMN_INDEX);
		tc.setCellEditor(nce);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JToolBar tb = new JToolBar();
		tb.setMaximumSize(new Dimension(Short.MAX_VALUE, tb.getPreferredSize().height));
		tb.setFloatable(false);
		tb.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
		
		tb.add(Box.createRigidArea(new Dimension(3, 0)));
		tb.add(new JLabel(Res.iconVol24));
		tb.add(Box.createRigidArea(new Dimension(3, 0)));
		tb.add(btnNewDevice);
		tb.add(btnDuplicateDevice);
		tb.add(btnRemoveDevice);
		tb.addSeparator();
		tb.add(btnDeviceProps);
		
		add(tb);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setContinuousLayout(true);
		
		devicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane sp = new JScrollPane(devicesTable);
		
		Dimension d;
		d = new Dimension(sp.getMinimumSize().width, sp.getPreferredSize().height);
		sp.setPreferredSize(d);
			
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(sp);
		p.add(Box.createRigidArea(new Dimension(0, 8)));
		
		splitPane.setTopComponent(p);
		
		JPanel channelsPane = new JPanel();
		channelsPane.setLayout(new BoxLayout(channelsPane, BoxLayout.Y_AXIS));
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(lChannels);
		p.add(Box.createRigidArea(new Dimension(5, 0)));
		p.add(cbChannels);
		p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		channelsPane.add(p);
		
		sp = new JScrollPane(channelParamTable);
		d = new Dimension(sp.getMinimumSize().width, sp.getPreferredSize().height);
		sp.setPreferredSize(d);
		
		p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		p.add(sp);
		channelsPane.add(p);
		
		channelsPane.setBorder (
			BorderFactory.createTitledBorder(i18n.getLabel("AudioDevicesPage.channels"))
		);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(Box.createRigidArea(new Dimension(0, 5)));
		p.add(channelsPane);
		
		splitPane.setBottomComponent(p);
		splitPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		splitPane.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
		splitPane.setDividerSize(3);
		add(splitPane);
		
		splitPane.setDividerLocation(150);
		
		cbChannels.addActionListener(getHandler());
		
		devicesTable.getSelectionModel().addListSelectionListener(getHandler());
		channelParamTable.getModel().addParameterListener(getHandler());
	}
	
	private AudioDeviceModel
	getSelectedAudioDeviceModel() {
		ListSelectionModel lsm = devicesTable.getSelectionModel();
		if(lsm.isSelectionEmpty()) return null;
		
		return ((AudioDevicesTableModel)devicesTable.getModel()).getAudioDeviceModel (
			lsm.getMinSelectionIndex()
		);
	}
	
	private final Handler handler = new Handler();
	
	private Handler
	getHandler() { return handler; }
	
	private class Handler extends AudioDeviceAdapter implements ActionListener,
							ListSelectionListener, ParameterListener {
		public void
		actionPerformed(ActionEvent e) {
			Object obj = cbChannels.getSelectedItem();
			if(obj == null) {
				channelParamTable.getModel().setParameters(new Parameter[0]);
				return;
			}
			
			AudioOutputChannel c = (AudioOutputChannel)obj;
			
			channelParamTable.getModel().setParameters(c.getAllParameters());
		}
		
		public void
		valueChanged(ListSelectionEvent e) {
			if(e.getValueIsAdjusting()) return;
			
			for(AudioDeviceModel m : CC.getSamplerModel().getAudioDevices()) {
				m.removeAudioDeviceListener(this);
			}
			
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			
			if(lsm.isSelectionEmpty()) {
				duplicateAudioDevice.setEnabled(false);
				removeAudioDevice.setEnabled(false);
				audioDeviceProps.setEnabled(false);
				
				cbChannels.removeAllItems();
				cbChannels.setEnabled(false);
				return;
			}
			
			duplicateAudioDevice.setEnabled(true);
			removeAudioDevice.setEnabled(true);
			audioDeviceProps.setEnabled(true);
				
			AudioDeviceModel m;
			m = ((AudioDevicesTableModel)devicesTable.getModel()).getAudioDeviceModel (
				lsm.getMinSelectionIndex()
			);
			
			cbChannels.removeAllItems();
			for(AudioOutputChannel c : m.getDeviceInfo().getAudioChannels()) {
				cbChannels.addItem(c);
			}
			cbChannels.setEnabled(true);
			
			m.addAudioDeviceListener(this);
		}
		
		/** Invoked when when the settings of a particular audio device have changed. */
		public void
		settingsChanged(AudioDeviceEvent e) {
			AudioOutputDevice d = e.getAudioDeviceModel().getDeviceInfo();
			
			int idx = cbChannels.getSelectedIndex();
			cbChannels.removeAllItems();
			for(AudioOutputChannel c : d.getAudioChannels()) cbChannels.addItem(c);
			
			if(idx >= cbChannels.getModel().getSize()) idx = 0;
			
			if(cbChannels.getModel().getSize() > 0) cbChannels.setSelectedIndex(idx);
		}
		
		/** Invoked when when the value of a particular parameter is changed. */
		public void
		parameterChanged(ParameterEvent e) {
			AudioDeviceModel m = getSelectedAudioDeviceModel();
			if(m == null) {
				CC.getLogger().warning("Unexpected null AudioDeviceModel!");
				return;
			}
			
			int c = cbChannels.getSelectedIndex();
			if(c == -1) {
				CC.getLogger().warning("There is no audio channel selected!");
				return;
			}
			
			m.setBackendChannelParameter(c, e.getParameter());
		}
	}
	
	private class DuplicateAudioDevice extends AbstractAction {
		DuplicateAudioDevice() {
			super("");
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttDuplicateAudioDevice"));
			
			try {
				final ImageIcon icon = new ImageIcon(
					Res.getUrl("com/grigoriliev/jsampler/classic/view/res/icons/Copy16.gif")
				);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog (
				SHF.getMainFrame(), "Not implemented yet",
				"",
				JOptionPane.INFORMATION_MESSAGE
			);
			
			AudioDeviceModel m = getSelectedAudioDeviceModel();
			if(m == null) {
				CC.getLogger().warning("No selected audio device to duplicate!");
				return;
			}
			
			
		}
	}
	
	private class RemoveAudioDevice extends AbstractAction {
		RemoveAudioDevice() {
			super("");
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttRemoveAudioDevice"));
			
			try {
				final ImageIcon icon = new ImageIcon(
					Res.getUrl("com/grigoriliev/jsampler/classic/view/res/icons/Delete16.gif")
				);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) {
			AudioDeviceModel m = getSelectedAudioDeviceModel();
			if(m == null) {
				CC.getLogger().warning("No selected audio device to remove!");
				return;
			}
			
			CC.getSamplerModel().removeBackendAudioDevice(m.getDeviceId());
		}
	}
	
	private class AudioDeviceProps extends AbstractAction {
		AudioDeviceProps() {
			super("");
			
			putValue(SHORT_DESCRIPTION, i18n.getMenuLabel("ttAudioDeviceProps"));
			
			try {
				final ImageIcon icon = new ImageIcon(
					Res.getUrl("com/grigoriliev/jsampler/classic/view/res/icons/Properties16.gif")
				);
				if(icon.getImageLoadStatus() == MediaTracker.COMPLETE)
					putValue(Action.SMALL_ICON, icon);
			} catch(Exception x) {
				CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
			}
			
			setEnabled(false);
		}
		
		public void
		actionPerformed(ActionEvent e) { new DevicePropsDlg().setVisible(true); }
	}
	
	private class DevicePropsDlg extends InformationDialog {
		DevicePropsDlg() {
			super(SHF.getMainFrame(), i18n.getLabel("AudioDevicesPage.DevicePropsDlg"));
			
			AudioDeviceModel m = getSelectedAudioDeviceModel();
			ParameterTable table = new ParameterTable();
			table.getModel().setParameters (
				m.getDeviceInfo().getAdditionalParameters()
			);
			
			JScrollPane sp = new JScrollPane(table);
			sp.setPreferredSize(JuifeUtils.getUnionSize (
				sp.getMinimumSize(), new Dimension(200, 200)
			));
			setMainPane(sp);
		}
	}
}

class AudioDevicesTableModel extends AbstractTableModel {
	protected final static int ACTIVE_COLUMN_INDEX = 0;
	protected final static int DEVICE_ID_COLUMN_INDEX = 1;
	protected final static int CHANNELS_COLUMN_INDEX = 2;
	
	private final String[] columnNames = {
		"",
		i18n.getLabel("AudioDevicesTableModel.deviceID"),
		i18n.getLabel("AudioDevicesTableModel.channels")
	};
	
	private AudioDeviceModel[] deviceList;
	
	AudioDevicesTableModel() {
		CC.getSamplerModel().addAudioDeviceListListener(new Handler());
		deviceList = CC.getSamplerModel().getAudioDevices();
		
	}
	
	public AudioDeviceModel
	getAudioDeviceModel(int index) { return deviceList[index]; }
	
// The Table Model implementation
	
	/**
	 * Gets the number of columns in the model.
	 * @return The number of columns in the model.
	 */
	public int
	getColumnCount() { return columnNames.length; }
	
	/**
	 * Gets the number of rows in the model.
	 * @return The number of rows in the model.
	 */
	public int
	getRowCount() { return deviceList.length; }
	
	/**
	 * Gets the name of the column at <code>columnIndex</code>.
	 * @return The name of the column at <code>columnIndex</code>.
	 */
	public String
	getColumnName(int col) { return columnNames[col]; }
	
	/**
	 * Gets the value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 * @param row The row whose value is to be queried.
	 * @param col The column whose value is to be queried.
	 * @return The value for the cell at <code>columnIndex</code> and
	 * <code>rowIndex</code>.
	 */
	public Object
	getValueAt(int row, int col) {
		switch(col) {
		case ACTIVE_COLUMN_INDEX:
			return deviceList[row].getDeviceInfo().isActive();
		case DEVICE_ID_COLUMN_INDEX:
			return deviceList[row].getDeviceId();
		case CHANNELS_COLUMN_INDEX:
			return deviceList[row].getDeviceInfo().getChannelCount();
		}
		
		return null;
	}
	
	/**
	 * Sets the value in the cell at <code>columnIndex</code>
	 * and <code>rowIndex</code> to <code>value</code>.
	 */
	public void
	setValueAt(Object value, int row, int col) {
		switch(col) {
		case ACTIVE_COLUMN_INDEX:
			boolean active = (Boolean)value;
			deviceList[row].getDeviceInfo().setActive(active);
			deviceList[row].setBackendActive(active);
			break;
		case CHANNELS_COLUMN_INDEX:
			int channels = (Integer)value;
			getAudioDeviceModel(row).setBackendChannelCount(channels);
			break;
		default: return;
		}
		
		fireTableCellUpdated(row,  col);
	}
	
	/**
	 * Returns <code>true</code> if the cell at
	 * <code>rowIndex</code> and <code>columnIndex</code> is editable.
	 */
	public boolean
	isCellEditable(int row, int col) {
		switch(col) {
		case ACTIVE_COLUMN_INDEX:
			return true;
		case DEVICE_ID_COLUMN_INDEX:
			return false;
		case CHANNELS_COLUMN_INDEX:
			return true;
		default: return false;
		}
	}
	
	/**
	 * Returns the most specific superclass for all the cell values
	 * in the column. This is used by the <code>JTable</code> to set up a
	 * default renderer and editor for the column.
	 * @param columnIndex The index of the column.
	 * @return The common ancestor class of the object values in the model.
	 */
	public Class
	getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}
///////
	
	private class Handler extends AudioDeviceAdapter implements ListListener<AudioDeviceModel> {
		/**
		 * Invoked when a new audio device is created.
		 * @param e An <code>AudioDeviceListEvent</code>
		 * instance providing the event information.
		 */
		public void
		entryAdded(ListEvent<AudioDeviceModel> e) {
			for(AudioDeviceModel m : deviceList) m.removeAudioDeviceListener(this);
			deviceList = CC.getSamplerModel().getAudioDevices();
			for(AudioDeviceModel m : deviceList) m.addAudioDeviceListener(this);
			fireTableDataChanged();
		}
	
		/**
		 * Invoked when an audio device is removed.
		 * @param e An <code>AudioDeviceListEvent</code>
		 * instance providing the event information.
		 */
		public void
		entryRemoved(ListEvent<AudioDeviceModel> e) {
			for(AudioDeviceModel m : deviceList) m.removeAudioDeviceListener(this);
			deviceList = CC.getSamplerModel().getAudioDevices();
			for(AudioDeviceModel m : deviceList) m.addAudioDeviceListener(this);
			fireTableDataChanged();
		}
		
		/** Invoked when when the settings of a particular audio device have changed. */
		public void
		settingsChanged(AudioDeviceEvent e) {
			for(int i = 0; i < deviceList.length; i++) {
				AudioOutputDevice d = deviceList[i].getDeviceInfo();
				AudioOutputDevice d2 = e.getAudioDeviceModel().getDeviceInfo();
				
				if(d.getDeviceId() == d2.getDeviceId()) {
					fireTableRowsUpdated(i,  i);
				}
			}
		}
	}
}
