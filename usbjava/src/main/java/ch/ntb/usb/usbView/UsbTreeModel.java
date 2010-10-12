/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.usbView;

import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.Usb_Bus;
import ch.ntb.usb.Usb_Config_Descriptor;
import ch.ntb.usb.Usb_Device;
import ch.ntb.usb.Usb_Device_Descriptor;
import ch.ntb.usb.Usb_Endpoint_Descriptor;
import ch.ntb.usb.Usb_Interface;
import ch.ntb.usb.Usb_Interface_Descriptor;

public class UsbTreeModel implements TreeModel, TreeSelectionListener {

	private Usb_Bus rootBus;

	private static final String USB_ROOT = "USB";

	private JTextArea textArea;

	private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

	/**
	 * Default constructor.<br>
	 * 
	 * @param rootBus
	 *            the root bus from which the data is read
	 * @param textArea
	 *            the text area to which the data is written
	 */
	public UsbTreeModel(Usb_Bus rootBus, JTextArea textArea) {
		this.rootBus = rootBus;
		this.textArea = textArea;
	}

	/**
	 * Returns the root of the tree.
	 */
	public Object getRoot() {
		return USB_ROOT;
	}

	/**
	 * Returns the child of parent at index index in the parent's child array.
	 */
	public Object getChild(Object parent, int index) {
		
		if (parent instanceof String && ((String) parent).compareTo(USB_ROOT) == 0)
		{
			Usb_Bus curBus = rootBus;
			
			for (int i = 0; curBus != null; curBus = curBus.getNext(), i++)
			{
				if (i == index)
					return curBus;
			}
		}
			
		else if (parent instanceof Usb_Bus) {
			Usb_Device device = ((Usb_Bus) parent).getDevices();
			int count = 0;
			while (device != null) {
				if (count == index)
					return device;
				count++;
				device = device.getNext();
			}
			return null;
		} else if (parent instanceof Usb_Device) {
			Usb_Device dev = (Usb_Device) parent;
			// return the Usb_Device_Descriptor at index 0
			if (index == 0) {
				return dev.getDescriptor();
			}
			Usb_Config_Descriptor[] confDescs = dev.getConfig();
			if (index >= confDescs.length + 1)
				return null;
			return confDescs[index - 1];
		} else if (parent instanceof Usb_Config_Descriptor) {
			Usb_Interface[] intDescs = ((Usb_Config_Descriptor) parent)
					.getInterface();
			if (index >= intDescs.length)
				return null;
			return intDescs[index];
		} else if (parent instanceof Usb_Interface) {
			Usb_Interface_Descriptor[] altSettings = ((Usb_Interface) parent)
					.getAltsetting();
			if (index >= altSettings.length)
				return null;
			return altSettings[index];
		} else if (parent instanceof Usb_Interface_Descriptor) {
			Usb_Endpoint_Descriptor[] endpoints = ((Usb_Interface_Descriptor) parent)
					.getEndpoint();
			if (index >= endpoints.length)
				return null;
			return endpoints[index];
		}
		return null;
	}

	/**
	 * Returns the number of children of parent.
	 */
	public int getChildCount(Object parent) 
	{
		if (parent instanceof String && ((String) parent).compareTo(USB_ROOT) == 0)
		{
			int count = 0;
			
			Usb_Bus curBus = rootBus;
			
			for (; curBus != null; curBus = curBus.getNext())
			{
				count++;
			}
			
			return count;
			
		}
		else if (parent instanceof Usb_Bus) {
			Usb_Device device = ((Usb_Bus) parent).getDevices();
			int count = 0;
			while (device != null) {
				count++;
				device = device.getNext();
			}
			return count;
		} else if (parent instanceof Usb_Device) {
			// add the Usb_Device_Descriptor
			return ((Usb_Device) parent).getConfig().length + 1;
		} else if (parent instanceof Usb_Config_Descriptor) {
			return ((Usb_Config_Descriptor) parent).getInterface().length;
		} else if (parent instanceof Usb_Interface) {
			return ((Usb_Interface) parent).getAltsetting().length;
		} else if (parent instanceof Usb_Interface_Descriptor) {
			return ((Usb_Interface_Descriptor) parent).getEndpoint().length;
		}
		return 0;
	}

	/**
	 * Returns true if node is a leaf.
	 */
	public boolean isLeaf(Object node) {
		return false;
	}

	/**
	 * Messaged when the user has altered the value for the item identified by
	 * path to newValue. Not used by this model.
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		System.out.println("*** valueForPathChanged : " + path + " --> "
				+ newValue);
	}

	/**
	 * Returns the index of child in parent.
	 */
	public int getIndexOfChild(Object parent, Object child) {
		return 0;
	}

	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.addElement(l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.removeElement(l);
	}

	/**
	 * The only event raised by this model is TreeStructureChanged with the root
	 * as path, i.e. the whole tree has changed.
	 */
	protected void fireTreeStructureChanged(Usb_Bus newRootBus) {
		rootBus = newRootBus;
		int len = treeModelListeners.size();
		TreeModelEvent e = new TreeModelEvent(this, new Object[] { newRootBus });
		for (int i = 0; i < len; i++) {
			treeModelListeners.elementAt(i).treeStructureChanged(e);
		}
	}

	public void valueChanged(TreeSelectionEvent e) {
		JTree tree = (JTree) e.getSource();
		Object component = tree.getLastSelectedPathComponent();
		if (component instanceof Usb_Bus) {
			Usb_Bus bus = (Usb_Bus) component;
			StringBuffer sb = new StringBuffer("Usb_Bus\n");
			sb.append("\tdirname: " + bus.getDirname() + "\n");
			sb.append("\tlocation: 0x" + Long.toHexString(bus.getLocation())
					+ "\n");
			textArea.setText(sb.toString());
		} else if (component instanceof Usb_Device) {
			Usb_Device device = (Usb_Device) component;
			StringBuffer sb = new StringBuffer("Usb_Device\n");
			sb.append("\tfilename: " + device.getFilename() + "\n");
			sb.append("\tdevnum: " + device.getDevnum() + "\n");
			sb.append("\tnum_children: " + device.getNumChildren() + "\n");
			textArea.setText(sb.toString());
		} else if (component instanceof Usb_Device_Descriptor) {
			Usb_Device_Descriptor devDesc = (Usb_Device_Descriptor) component;
			StringBuffer sb = new StringBuffer("Usb_Device_Descriptor\n");
			sb.append("\tblenght: 0x"
					+ Integer.toHexString(devDesc.getBLength() & 0xFF) + "\n");
			sb.append("\tbDescriptorType: 0x"
					+ Integer.toHexString(devDesc.getBDescriptorType() & 0xFF)
					+ "\n");
			sb.append("\tbcdUSB: 0x"
					+ Integer.toHexString(devDesc.getBcdUSB() & 0xFFFF) + "\n");
			sb.append("\tbDeviceClass: 0x"
					+ Integer.toHexString(devDesc.getBDeviceClass() & 0xFF)
					+ "\n");
			sb.append("\tbDeviceSubClass: 0x"
					+ Integer.toHexString(devDesc.getBDeviceSubClass() & 0xFF)
					+ "\n");
			sb.append("\tbDeviceProtocol: 0x"
					+ Integer.toHexString(devDesc.getBDeviceProtocol() & 0xFF)
					+ "\n");
			sb.append("\tbMaxPacketSize0: 0x"
					+ Integer.toHexString(devDesc.getBMaxPacketSize0() & 0xFF)
					+ " (" + devDesc.getBMaxPacketSize0() + ")\n");
			sb.append("\tidVendor: 0x"
					+ Integer.toHexString(devDesc.getIdVendor() & 0xFFFF)
					+ "\n");
			sb.append("\tidProduct: 0x"
					+ Integer.toHexString(devDesc.getIdProduct() & 0xFFFF)
					+ "\n");
			sb
					.append("\tbcdDevice: 0x"
							+ Integer
									.toHexString(devDesc.getBcdDevice() & 0xFF)
							+ "\n");
			sb.append("\tiManufacturer: 0x"
					+ Integer.toHexString(devDesc.getIManufacturer() & 0xFF)
					+ "\n");
			sb.append("\tiProduct: 0x"
					+ Integer.toHexString(devDesc.getIProduct()) + "\n");
			sb.append("\tiSerialNumber: 0x"
					+ Integer.toHexString(devDesc.getISerialNumber() & 0xFF)
					+ "\n");
			sb
					.append("\tbNumConfigurations: 0x"
							+ Integer.toHexString(devDesc
									.getBNumConfigurations() & 0xFF) + "\n");
			// get device handle to retrieve string descriptors
			Usb_Bus bus = rootBus;
			while (bus != null) {
				Usb_Device dev = bus.getDevices();
				while (dev != null) {
					Usb_Device_Descriptor tmpDevDesc = dev.getDescriptor();
					if ((dev.getDescriptor() != null)
							&& ((dev.getDescriptor().getIManufacturer() > 0)
									|| (dev.getDescriptor().getIProduct() > 0) || (dev
									.getDescriptor().getISerialNumber() > 0))) {
						if (tmpDevDesc.equals(devDesc)) {
							long handle = LibusbJava.usb_open(dev);
							sb.append("\nString descriptors\n");
							if (handle <= 0) {
								sb.append("\terror opening the device\n");
								break;
							}
							if (dev.getDescriptor().getIManufacturer() > 0) {
								String manufacturer = LibusbJava
										.usb_get_string_simple(handle, devDesc
												.getIManufacturer());
								if (manufacturer == null)
									manufacturer = "unable to fetch manufacturer string";
								sb.append("\tiManufacturer: " + manufacturer
										+ "\n");
							}
							if (dev.getDescriptor().getIProduct() > 0) {
								String product = LibusbJava
										.usb_get_string_simple(handle, devDesc
												.getIProduct());
								if (product == null)
									product = "unable to fetch product string";
								sb.append("\tiProduct: " + product + "\n");
							}
							if (dev.getDescriptor().getISerialNumber() > 0) {
								String serialNumber = LibusbJava
										.usb_get_string_simple(handle, devDesc
												.getISerialNumber());
								if (serialNumber == null)
									serialNumber = "unable to fetch serial number string";
								sb.append("\tiSerialNumber: " + serialNumber
										+ "\n");
							}
							LibusbJava.usb_close(handle);
						}
					}
					dev = dev.getNext();
				}
				bus = bus.getNext();
			}
			textArea.setText(sb.toString());
		} else if (component instanceof Usb_Config_Descriptor) {
			Usb_Config_Descriptor confDesc = (Usb_Config_Descriptor) component;
			StringBuffer sb = new StringBuffer("Usb_Config_Descriptor\n");
			sb.append("\tblenght: 0x"
					+ Integer.toHexString(confDesc.getBLength()) + "\n");
			sb.append("\tbDescriptorType: 0x"
					+ Integer.toHexString(confDesc.getBDescriptorType() & 0xFF)
					+ "\n");
			sb.append("\tbNumInterfaces: 0x"
					+ Integer.toHexString(confDesc.getBNumInterfaces() & 0xFF)
					+ "\n");
			sb
					.append("\tbConfigurationValue: 0x"
							+ Integer.toHexString(confDesc
									.getBConfigurationValue() & 0xFF) + "\n");
			sb.append("\tiConfiguration: 0x"
					+ Integer.toHexString(confDesc.getIConfiguration() & 0xFF)
					+ "\n");
			sb.append("\tbmAttributes: 0x"
					+ Integer.toHexString(confDesc.getBmAttributes() & 0xFF)
					+ "\n");
			sb.append("\tMaxPower [mA]: 0x"
					+ Integer.toHexString(confDesc.getMaxPower() & 0xFF) + " ("
					+ confDesc.getMaxPower() + ")\n");
			sb.append("\textralen: 0x"
					+ Integer.toHexString(confDesc.getExtralen()) + "\n");
			sb.append("\textra: " + extraDescriptorToString(confDesc.getExtra()) + "\n");
			// get device handle to retrieve string descriptors
			Usb_Bus bus = rootBus;
			while (bus != null) {
				Usb_Device dev = bus.getDevices();
				while (dev != null) {
					Usb_Config_Descriptor[] tmpConfDesc = dev.getConfig();
					for (int i = 0; i < tmpConfDesc.length; i++) {
						if ((tmpConfDesc.equals(confDesc))
								&& (confDesc.getIConfiguration() > 0)) {
							long handle = LibusbJava.usb_open(dev);
							sb.append("\nString descriptors\n");
							if (handle <= 0) {
								sb.append("\terror opening the device\n");
								break;
							}
							String configuration = LibusbJava
									.usb_get_string_simple(handle, confDesc
											.getIConfiguration());
							if (configuration == null)
								configuration = "unable to fetch configuration string";
							sb.append("\tiConfiguration: " + configuration
									+ "\n");

							LibusbJava.usb_close(handle);

						}
					}
					dev = dev.getNext();
				}
				bus = bus.getNext();
			}
			textArea.setText(sb.toString());
		} else if (component instanceof Usb_Interface) {
			Usb_Interface int_ = (Usb_Interface) component;
			StringBuffer sb = new StringBuffer("Usb_Interface\n");
			sb.append("\tnum_altsetting: 0x"
					+ Integer.toHexString(int_.getNumAltsetting()) + "\n");
			sb.append("\taltsetting: " + int_.getAltsetting() + "\n");
			textArea.setText(sb.toString());
		} else if (component instanceof Usb_Interface_Descriptor) {
			Usb_Interface_Descriptor intDesc = (Usb_Interface_Descriptor) component;
			StringBuffer sb = new StringBuffer("Usb_Interface_Descriptor\n");
			sb.append("\tblenght: 0x"
					+ Integer.toHexString(intDesc.getBLength() & 0xFF) + "\n");
			sb.append("\tbDescriptorType: 0x"
					+ Integer.toHexString(intDesc.getBDescriptorType() & 0xFF)
					+ "\n");
			sb.append("\tbInterfaceNumber: 0x"
					+ Integer.toHexString(intDesc.getBInterfaceNumber() & 0xFF)
					+ "\n");
			sb.append("\tbAlternateSetting: 0x"
					+ Integer
							.toHexString(intDesc.getBAlternateSetting() & 0xFF)
					+ "\n");
			sb.append("\tbNumEndpoints: 0x"
					+ Integer.toHexString(intDesc.getBNumEndpoints() & 0xFF)
					+ "\n");
			sb.append("\tbInterfaceClass: 0x"
					+ Integer.toHexString(intDesc.getBInterfaceClass() & 0xFF)
					+ "\n");
			sb
					.append("\tbInterfaceSubClass: 0x"
							+ Integer.toHexString(intDesc
									.getBInterfaceSubClass() & 0xFF) + "\n");
			sb
					.append("\tbInterfaceProtocol: 0x"
							+ Integer.toHexString(intDesc
									.getBInterfaceProtocol() & 0xFF) + "\n");
			sb.append("\tiInterface: 0x"
					+ Integer.toHexString(intDesc.getIInterface()) + "\n");
			sb.append("\textralen: 0x"
					+ Integer.toHexString(intDesc.getExtralen()) + "\n");
			sb.append("\textra: " + extraDescriptorToString(intDesc.getExtra()) + "\n");
			// get device handle to retrieve string descriptors
			Usb_Bus bus = rootBus;
			while (bus != null) {
				Usb_Device dev = bus.getDevices();
				while (dev != null) {
					Usb_Config_Descriptor[] confDescs = dev.getConfig();
					for (int i = 0; i < confDescs.length; i++) {
						Usb_Interface[] ints = confDescs[i].getInterface();
						for (int j = 0; j < ints.length; j++) {
							Usb_Interface_Descriptor[] tmpIntDescs = ints[j]
									.getAltsetting();
							for (int k = 0; k < ints.length; k++) {
								if (i < tmpIntDescs.length && tmpIntDescs[i].equals(intDesc)
										&& (intDesc.getIInterface() > 0)) {
									long handle = LibusbJava.usb_open(dev);
									sb.append("\nString descriptors\n");
									if (handle <= 0) {
										sb
												.append("\terror opening the device\n");
										break;
									}
									String interface_ = LibusbJava
											.usb_get_string_simple(handle,
													intDesc.getIInterface());
									if (interface_ == null)
										interface_ = "unable to fetch interface string";
									sb.append("\tiInterface: " + interface_
											+ "\n");
									LibusbJava.usb_close(handle);
								}
							}
						}
					}
					dev = dev.getNext();
				}
				bus = bus.getNext();
			}
			textArea.setText(sb.toString());
		} else if (component instanceof Usb_Endpoint_Descriptor) {
			Usb_Endpoint_Descriptor epDesc = (Usb_Endpoint_Descriptor) component;
			StringBuffer sb = new StringBuffer("Usb_Endpoint_Descriptor\n");
			sb.append("\tblenght: 0x"
					+ Integer.toHexString(epDesc.getBLength() & 0xFF) + "\n");
			sb.append("\tbDescriptorType: 0x"
					+ Integer.toHexString(epDesc.getBDescriptorType() & 0xFF)
					+ "\n");
			sb.append("\tbEndpointAddress: 0x"
					+ Integer.toHexString(epDesc.getBEndpointAddress() & 0xFF)
					+ "\n");
			sb.append("\tbmAttributes: 0x"
					+ Integer.toHexString(epDesc.getBmAttributes() & 0xFF)
					+ "\n");
			sb.append("\twMaxPacketSize: 0x"
					+ Integer.toHexString(epDesc.getWMaxPacketSize() & 0xFFFF)
					+ " (" + epDesc.getWMaxPacketSize() + ")\n");
			sb.append("\tbInterval: 0x"
					+ Integer.toHexString(epDesc.getBInterval() & 0xFF) + "\n");
			sb.append("\tbRefresh: 0x"
					+ Integer.toHexString(epDesc.getBRefresh() & 0xFF) + "\n");
			sb.append("\tbSynchAddress: 0x"
					+ Integer.toHexString(epDesc.getBSynchAddress()) + "\n");
			sb.append("\textralen: 0x"
					+ Integer.toHexString(epDesc.getExtralen()) + "\n");
			sb.append("\textra: " + extraDescriptorToString(epDesc.getExtra()) + "\n");
			textArea.setText(sb.toString());
		}
	}

	private String extraDescriptorToString(byte[] extra) {
		if (extra != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < extra.length; i++) {
				sb.append("0x");
				sb.append(Integer.toHexString(extra[i] & 0xff));
				sb.append(' ');
			}
			return sb.toString();
		}
		return null;
	}
}
