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
import java.awt.Component;
import java.awt.Rectangle;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.grigoriliev.jsampler.juife.swing.ComponentList;
import com.grigoriliev.jsampler.juife.swing.ComponentListModel;
import com.grigoriliev.jsampler.juife.swing.DefaultComponentListModel;

import com.grigoriliev.jsampler.CC;
import com.grigoriliev.jsampler.SamplerChannelModel;

import com.grigoriliev.jsampler.view.JSChannelsPane;
import com.grigoriliev.jsampler.swing.view.SHF;
import com.grigoriliev.jsampler.swing.view.SwingChannelsPane;

import static com.grigoriliev.jsampler.classic.view.A4n.a4n;
import static com.grigoriliev.jsampler.classic.view.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class ChannelsPane extends SwingChannelsPane<Channel> implements ListSelectionListener {
	private final ComponentList chnList = new ComponentList();
	private final DefaultComponentListModel listModel = new DefaultComponentListModel();
	
	private final JScrollPane scrollPane;
		
	/**
	 * Creates a new instance of <code>ChannelsPane</code> with
	 * the specified <code>title</code>.
	 * @param title The title of this <code>ChannelsPane</code>
	 */
	public
	ChannelsPane(String title) {
		super(title);
		
		setLayout(new BorderLayout());
		
		chnList.setOpaque(false);
		//chnList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		chnList.setModel(listModel);
		chnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		chnList.addListSelectionListener(this);
		chnList.addMouseListener(new ContextMenu());
		//chnList.setDragEnabled(true);
		
		scrollPane = new JScrollPane(chnList);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		add(scrollPane);
		
		setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		
	}
	
	@Override
	public void
	setSelectedChannel(Channel channel) {
		chnList.setSelectedComponent(channel, true);
	}
	
	/**
	 * Adds new channel to this channels pane.
	 * @param channelModel The sampler channel model to be used by the new channel.
	 */
	@Override
	public void
	addChannel(SamplerChannelModel channelModel) {
		Channel channel = new Channel(channelModel);
		listModel.add(channel);
		if(channel.getChannelInfo().getEngine() == null) channel.expandChannel();
		chnList.setSelectedComponent(channel, true);
		scrollToBottom();
		
		firePropertyChange("channelAdded", null, channelModel);
	}
	
	/**
	 * Adds the specified channels to this channels pane.
	 * @param chns The channels to be added.
	 */
	@Override
	public void
	addChannels(Channel[] chns) {
		if(chns == null || chns.length == 0) return;
		
		for(Channel c : chns) listModel.add(c);
		chnList.setSelectionInterval (
			listModel.getSize() - chns.length, listModel.getSize() - 1
		);
		
		chnList.ensureIndexIsVisible(listModel.getSize() - 1);
		
		firePropertyChange("channelsAdded", null, chns);
	}
		
	/**
	 * Removes the specified channel from this channels pane.
	 * This method is invoked when a sampler channel is removed in the back-end.
	 * @param chn The channel to be removed from this channels pane.
	 */
	@Override
	public void
	removeChannel(Channel chn) {
		listModel.remove(chn);
		
		firePropertyChange("channelRemoved", null, chn);
	}
	
	/**
	 * Gets the first channel in this channels pane.
	 * @return The first channel in this channels pane or <code>null</code> if 
	 * the channels pane is empty.
	 */
	@Override
	public Channel
	getFirstChannel() { return listModel.size() == 0 ? null : (Channel)listModel.get(0); }
	
	/**
	 * Gets the last channel in this channels pane.
	 * @return The last channel in this channels pane or <code>null</code> if 
	 * the channels pane is empty.
	 */
	@Override
	public Channel
	getLastChannel() {
		return listModel.size() == 0 ? null : (Channel)listModel.get(listModel.size()-1);
	}
	
	/**
	 * Gets the channel at the specified index.
	 * @return The channel at the specified index.
	 * @throws ArrayIndexOutOfBoundsException If the index is out of range.
	 */
	@Override
	public Channel
	getChannel(int idx) { return (Channel)listModel.get(idx); }
	
	/**
	 * Gets an array with all channels in this channels pane.
	 * @return An array with all channels in this channels pane.
	 */
	@Override
	public Channel[]
	getChannels() {
		Channel[] chns = new Channel[listModel.size()];
		for(int i = 0; i < listModel.size(); i++) chns[i] = (Channel)listModel.get(i);
		return chns;
	}
	
	/**
	 * Gets the number of channels in this channels pane.
	 * @return The number of channels in this channels pane.
	 */
	@Override
	public int
	getChannelCount() { return listModel.size(); }
	
	/**
	 * Determines whether there is at least one selected channel.
	 * @return <code>true</code> if there is at least one selected channel,
	 * <code>false</code> otherwise.
	 */
	@Override
	public boolean
	hasSelectedChannel() { return !chnList.isSelectionEmpty(); }
	
	/**
	 * Gets the number of the selected channels.
	 * @return The number of the selected channels.
	 */
	@Override
	public int
	getSelectedChannelCount() { return chnList.getSelectedIndices().length; }
	
	/**
	 * Gets an array with all selected channels.
	 * The channels are sorted in increasing index order.
	 * @return The selected channels or an empty array if nothing is selected.
	 */
	@Override
	public Channel[]
	getSelectedChannels() {
		Component[] cS = chnList.getSelectedComponents();
		Channel[] chns = new Channel[cS.length];
		for(int i = 0; i < cS.length; i++) chns[i] = (Channel)cS[i];
		return chns;
	}
	
	/**
	 * Removes all selected channels in this channels pane.
	 * Notice that this method does not remove any channels in the back-end.
	 * It is invoked after the channels are already removed in the back-end.
	 * @return The number of removed channels.
	 */
	@Override
	public int 
	removeSelectedChannels() {
		int[] l = chnList.getSelectedIndices();
		ComponentListModel model = chnList.getModel();
		
		for(;;) {
			int i = chnList.getMinSelectionIndex();
			if(i == -1) break;
			model.remove(i);
		}
		
		firePropertyChange("channelsRemoved", null, null);
		
		return l.length;
	}
	
	/** Selects all channels. */
	@Override
	public void
	selectAll() { chnList.selectAll(); }
	
	/** Deselects all selected channels. */
	@Override
	public void
	clearSelection() { chnList.clearSelection(); }
	
	/**
	 * Registers the specified listener for receiving list selection events.
	 * @param listener The <code>ListSelectionListener</code> to register.
	 */
	@Override
	public void
	addListSelectionListener(com.grigoriliev.jsampler.event.ListSelectionListener listener) {
		listenerList.add(com.grigoriliev.jsampler.event.ListSelectionListener.class, listener);
	}
	
	/**
	 * Removes the specified listener.
	 * @param listener The <code>ListSelectionListener</code> to remove.
	 */
	@Override
	public void
	removeListSelectionListener(com.grigoriliev.jsampler.event.ListSelectionListener listener) {
		listenerList.remove(com.grigoriliev.jsampler.event.ListSelectionListener.class, listener);
	}
	
	/**
	 * Invoked when the selection has changed.
	 * This method implements <code>valueChanged</code>
	 * method of the <code>ListSelectionListener</code> interface.
	 * @param e A <code>ListSelectionEvent</code>
	 * instance providing the event information.
	 */
	@Override
	public void
	valueChanged(ListSelectionEvent e) {
		ListSelectionEvent e2 = null;
		Object[] listeners = listenerList.getListenerList();
		
		for(int i = listeners.length - 2; i >= 0; i -= 2) {
			if(listeners[i] == ListSelectionListener.class) {
				if(e2 == null) e2 = new ListSelectionEvent (
					this,
					e.getFirstIndex(),
					e.getLastIndex(),
					e.getValueIsAdjusting()
				);
				((ListSelectionListener)listeners[i + 1]).valueChanged(e2);
			}
		}
			
	}
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed. The default value is <code>true</code>.
	 * @see updateChannelListUI
	 */
	@Override
	public boolean
	getAutoUpdate() { return chnList.getAutoUpdate(); }
	
	/**
	 * Determines whether the channel list UI should be automatically updated
	 * when channel is added/removed.
	 * @see updateChannelListUI
	 */
	@Override
	public void
	setAutoUpdate(boolean b) { chnList.setAutoUpdate(b); }
	
	/**
	 * Updates the channel list UI.
	 * @see setAutoUpdate
	 */
	@Override
	public void
	updateChannelListUI() { chnList.updateList(); }
	
		
	@Override
	public void
	moveSelectedChannelsOnTop() {
		Channel[] chns = getSelectedChannels();
			
		if(chns.length == 0) {
			CC.getLogger().info("Can't move channel(s) at the beginning");
			return;
		}
		
		for(int i = chns.length - 1; i >= 0; i--) {
			listModel.remove(chns[i]);
			listModel.insert(chns[i], 0);
		}
		
		chnList.setSelectionInterval(0, chns.length - 1);
		chnList.ensureIndexIsVisible(0);
				
		firePropertyChange("channelsPositionChanged", null, chns);
	}
	
	@Override
	public void
	moveSelectedChannelsUp() {
		Channel[] chns = getSelectedChannels();
			
		if(chns.length == 0 || chns[0] == getFirstChannel()) {
			CC.getLogger().info("Can't move channel(s) up");
			return;
		}
		
		for(int i = 0; i < chns.length; i++) listModel.moveUp(chns[i]);
		
		int[] si = chnList.getSelectedIndices();
		
		for(int i = 0; i < si.length; i++) si[i] -= 1;
		
		chnList.setSelectedIndices(si);
		chnList.ensureIndexIsVisible(si[0]);
				
		firePropertyChange("channelsPositionChanged", null, chns);
	}
	
	@Override
	public void
	moveSelectedChannelsDown() {
		Channel[] chns = getSelectedChannels();
			
		if(chns.length == 0 || chns[chns.length - 1] == getLastChannel()) {
			CC.getLogger().info("Can't move channel(s) down");
			return;
		}
		
		for(int i = chns.length - 1; i >= 0; i--) listModel.moveDown(chns[i]);
		
		int[] si = chnList.getSelectedIndices();
		for(int i = 0; i < si.length; i++) si[i] += 1;
		chnList.setSelectedIndices(si);
		chnList.ensureIndexIsVisible(si[si.length - 1]);
				
		firePropertyChange("channelsPositionChanged", null, chns);
	}
	
	@Override
	public void
	moveSelectedChannelsAtBottom() {
		Channel[] chns = getSelectedChannels();
			
		if(chns.length == 0) {
			CC.getLogger().info("Can't move channel(s) at the end");
			return;
		}
		
		for(int i =  0; i < chns.length; i++) {
			listModel.remove(chns[i]);
			listModel.add(chns[i]);
		}
		
		chnList.setSelectionInterval (
			listModel.getSize() - chns.length, listModel.getSize() - 1
		);
		chnList.ensureIndexIsVisible(listModel.getSize() - 1);
				
		firePropertyChange("channelsPositionChanged", null, chns);
	}
	
	private void
	scrollToBottom() {
		int h = scrollPane.getViewport().getView().getHeight();
		scrollPane.getViewport().scrollRectToVisible(new Rectangle(0, h - 2, 1, 1));
	}
	
	class ContextMenu extends MouseAdapter {
		private final JPopupMenu cmenu = new JPopupMenu();
		private final JMenu submenu = new JMenu(i18n.getMenuLabel("channels.MoveToTab"));
		
		ContextMenu() {
			JMenuItem mi = new JMenuItem(a4n.moveChannelsOnTop);
			mi.setIcon(null);
			cmenu.add(mi);
			
			mi = new JMenuItem(a4n.moveChannelsUp);
			mi.setIcon(null);
			cmenu.add(mi);
			
			mi = new JMenuItem(a4n.moveChannelsDown);
			mi.setIcon(null);
			cmenu.add(mi);
			
			mi = new JMenuItem(a4n.moveChannelsAtBottom);
			mi.setIcon(null);
			cmenu.add(mi);
			
			cmenu.add(submenu);
			
			cmenu.addSeparator();
			
			mi = new JMenuItem(a4n.removeChannels);
			mi.setIcon(null);
			cmenu.add(mi);
		}
		
		@Override
		public void
		mousePressed(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		@Override
		public void
		mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger()) show(e);
		}
	
		void
		show(MouseEvent e) {
			/*int idx = chnList.locationToIndex(e.getPoint());
			if(!chnList.isSelectedIndex(idx)) chnList.setSelectedIndex(idx);
			
			if(idx != -1 && CC.getMainFrame().getChannelsPaneCount() > 1) {
				updateMenu();
				submenu.setEnabled(true);
			} else submenu.setEnabled(false);
			
			cmenu.show(e.getComponent(), e.getX(), e.getY());*/
		}
		
		private void
		updateMenu() {
			submenu.removeAll();
			Vector<SwingChannelsPane> v = SHF.getMainFrame().getChannelsPaneList();
			for(SwingChannelsPane p : v) 
				if(p != CC.getMainFrame().getSelectedChannelsPane())
					submenu.add(new JMenuItem(new A4n.MoveChannelsTo(p)));
		}
	}
	
	@Override
	public void
	processChannelSelection(Channel c, boolean controlDown, boolean shiftDown) {
		
	}
}
