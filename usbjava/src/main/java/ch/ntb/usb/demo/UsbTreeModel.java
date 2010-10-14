/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.demo;

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
import ch.ntb.usb.UsbBus;
import ch.ntb.usb.UsbConfigDescriptor;
import ch.ntb.usb.UsbDevice;
import ch.ntb.usb.UsbDeviceDescriptor;
import ch.ntb.usb.UsbEndpointDescriptor;
import ch.ntb.usb.UsbInterface;
import ch.ntb.usb.UsbInterfaceDescriptor;

public class UsbTreeModel implements TreeModel, TreeSelectionListener {

	private UsbBus rootBus;

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
	public UsbTreeModel(UsbBus rootBus, JTextArea textArea) {
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
			UsbBus curBus = rootBus;
			
			for (int i = 0; curBus != null; curBus = curBus.getNext(), i++)
			{
				if (i == index)
					return curBus;
			}
		}
			
		else if (parent instanceof UsbBus) {
			UsbDevice device = ((UsbBus) parent).getDevices();
			int count = 0;
			while (device != null) {
				if (count == index)
					return device;
				count++;
				device = device.getNext();
			}
			return null;
		} else if (parent instanceof UsbDevice) {
			UsbDevice dev = (UsbDevice) parent;
			// return the UsbDeviceDescriptor at index 0
			if (index == 0) {
				return dev.getDescriptor();
			}
			UsbConfigDescriptor[] confDescs = dev.getConfig();
			if (index >= confDescs.length + 1)
				return null;
			return confDescs[index - 1];
		} else if (parent instanceof UsbConfigDescriptor) {
			UsbInterface[] intDescs = ((UsbConfigDescriptor) parent)
					.getInterfaces();
			if (index >= intDescs.length)
				return null;
			return intDescs[index];
		} else if (parent instanceof UsbInterface) {
			UsbInterfaceDescriptor[] altSettings = ((UsbInterface) parent)
					.getAlternateSetting();
			if (index >= altSettings.length)
				return null;
			return altSettings[index];
		} else if (parent instanceof UsbInterfaceDescriptor) {
			UsbEndpointDescriptor[] endpoints = ((UsbInterfaceDescriptor) parent)
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
			
			UsbBus curBus = rootBus;
			
			for (; curBus != null; curBus = curBus.getNext())
			{
				count++;
			}
			
			return count;
			
		}
		else if (parent instanceof UsbBus) {
			UsbDevice device = ((UsbBus) parent).getDevices();
			int count = 0;
			while (device != null) {
				count++;
				device = device.getNext();
			}
			return count;
		} else if (parent instanceof UsbDevice) {
			// add the UsbDeviceDescriptor
			return ((UsbDevice) parent).getConfig().length + 1;
		} else if (parent instanceof UsbConfigDescriptor) {
			return ((UsbConfigDescriptor) parent).getInterfaces().length;
		} else if (parent instanceof UsbInterface) {
			return ((UsbInterface) parent).getAlternateSetting().length;
		} else if (parent instanceof UsbInterfaceDescriptor) {
			return ((UsbInterfaceDescriptor) parent).getEndpoint().length;
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
	protected void fireTreeStructureChanged(UsbBus newRootBus) {
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
		if (component instanceof UsbBus) {
			UsbBus bus = (UsbBus) component;
			StringBuffer sb = new StringBuffer("Usb_Bus\n");
			sb.append("\tdirname: " + bus.getDirname() + "\n");
			sb.append("\tlocation: 0x" + Long.toHexString(bus.getLocation())
					+ "\n");
			textArea.setText(sb.toString());
		} else if (component instanceof UsbDevice) {
			UsbDevice device = (UsbDevice) component;
			StringBuffer sb = new StringBuffer("Usb_Device\n");
			sb.append("\tfilename: " + device.getFilename() + "\n");
			sb.append("\tdevnum: " + device.getDevnum() + "\n");
			sb.append("\tnum_children: " + device.getNumChildren() + "\n");
			textArea.setText(sb.toString());
		} else if (component instanceof UsbDeviceDescriptor) {
			UsbDeviceDescriptor devDesc = (UsbDeviceDescriptor) component;
			StringBuffer sb = new StringBuffer("Usb_Device_Descriptor\n");
			sb.append("\tblenght: 0x"
					+ Integer.toHexString(devDesc.getBLength() & 0xFF) + "\n");
			sb.append("\tbDescriptorType: 0x"
					+ Integer.toHexString(devDesc.getBDescriptorType() & 0xFF)
					+ "\n");
			sb.append("\tbcdUSB: 0x"
					+ Integer.toHexString(devDesc.getBcdUSB() & 0xFFFF) + "\n");
			sb.append("\tbDeviceClass: 0x"
					+ Integer.toHexString(devDesc.getDeviceClass() & 0xFF)
					+ "\n");
			sb.append("\tbDeviceSubClass: 0x"
					+ Integer.toHexString(devDesc.getDeviceSubClass() & 0xFF)
					+ "\n");
			sb.append("\tbDeviceProtocol: 0x"
					+ Integer.toHexString(devDesc.getDeviceProtocol() & 0xFF)
					+ "\n");
			sb.append("\tbMaxPacketSize0: 0x"
					+ Integer.toHexString(devDesc.getMaxPacketSize0() & 0xFF)
					+ " (" + devDesc.getMaxPacketSize0() + ")\n");
			sb.append("\tidVendor: 0x"
					+ Integer.toHexString(devDesc.getVendorId() & 0xFFFF)
					+ "\n");
			sb.append("\tidProduct: 0x"
					+ Integer.toHexString(devDesc.getProductId() & 0xFFFF)
					+ "\n");
			sb
					.append("\tbcdDevice: 0x"
							+ Integer
									.toHexString(devDesc.getBcdDevice() & 0xFF)
							+ "\n");
			sb.append("\tiManufacturer: 0x"
					+ Integer.toHexString(devDesc.getManufacturer() & 0xFF)
					+ "\n");
			sb.append("\tiProduct: 0x"
					+ Integer.toHexString(devDesc.getProduct()) + "\n");
			sb.append("\tiSerialNumber: 0x"
					+ Integer.toHexString(devDesc.getSerialNumber() & 0xFF)
					+ "\n");
			sb
					.append("\tbNumConfigurations: 0x"
							+ Integer.toHexString(devDesc
									.getNumConfigurations() & 0xFF) + "\n");
			// get device handle to retrieve string descriptors
			UsbBus bus = rootBus;
			while (bus != null) {
				UsbDevice dev = bus.getDevices();
				while (dev != null) {
					UsbDeviceDescriptor tmpDevDesc = dev.getDescriptor();
					if ((dev.getDescriptor() != null)
							&& ((dev.getDescriptor().getManufacturer() > 0)
									|| (dev.getDescriptor().getProduct() > 0) || (dev
									.getDescriptor().getSerialNumber() > 0))) {
						if (tmpDevDesc.equals(devDesc)) {
							long handle = LibusbJava.usb_open(dev);
							sb.append("\nString descriptors\n");
							if (handle <= 0) {
								sb.append("\terror opening the device\n");
								break;
							}
							if (dev.getDescriptor().getManufacturer() > 0) {
								String manufacturer = LibusbJava
										.usb_get_string_simple(handle, devDesc
												.getManufacturer());
								if (manufacturer == null)
									manufacturer = "unable to fetch manufacturer string";
								sb.append("\tiManufacturer: " + manufacturer
										+ "\n");
							}
							if (dev.getDescriptor().getProduct() > 0) {
								String product = LibusbJava
										.usb_get_string_simple(handle, devDesc
												.getProduct());
								if (product == null)
									product = "unable to fetch product string";
								sb.append("\tiProduct: " + product + "\n");
							}
							if (dev.getDescriptor().getSerialNumber() > 0) {
								String serialNumber = LibusbJava
										.usb_get_string_simple(handle, devDesc
												.getSerialNumber());
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
		} else if (component instanceof UsbConfigDescriptor) {
			UsbConfigDescriptor confDesc = (UsbConfigDescriptor) component;
			StringBuffer sb = new StringBuffer("Usb_Config_Descriptor\n");
			sb.append("\tblenght: 0x"
					+ Integer.toHexString(confDesc.getBLength()) + "\n");
			sb.append("\tbDescriptorType: 0x"
					+ Integer.toHexString(confDesc.getBDescriptorType() & 0xFF)
					+ "\n");
			sb.append("\tbNumInterfaces: 0x"
					+ Integer.toHexString(confDesc.getNumInterfaces() & 0xFF)
					+ "\n");
			sb
					.append("\tbConfigurationValue: 0x"
							+ Integer.toHexString(confDesc
									.getConfigurationValue() & 0xFF) + "\n");
			sb.append("\tiConfiguration: 0x"
					+ Integer.toHexString(confDesc.getConfiguration() & 0xFF)
					+ "\n");
			sb.append("\tbmAttributes: 0x"
					+ Integer.toHexString(confDesc.getAttributes() & 0xFF)
					+ "\n");
			sb.append("\tMaxPower [mA]: 0x"
					+ Integer.toHexString(confDesc.getMaxPower() & 0xFF) + " ("
					+ confDesc.getMaxPower() + ")\n");
			sb.append("\textralen: 0x"
					+ Integer.toHexString(confDesc.getExtralen()) + "\n");
			sb.append("\textra: " + extraDescriptorToString(confDesc.getExtra()) + "\n");
			// get device handle to retrieve string descriptors
			UsbBus bus = rootBus;
			while (bus != null) {
				UsbDevice dev = bus.getDevices();
				while (dev != null) {
					UsbConfigDescriptor[] tmpConfDesc = dev.getConfig();
					for (int i = 0; i < tmpConfDesc.length; i++) {
						if ((tmpConfDesc.equals(confDesc))
								&& (confDesc.getConfiguration() > 0)) {
							long handle = LibusbJava.usb_open(dev);
							sb.append("\nString descriptors\n");
							if (handle <= 0) {
								sb.append("\terror opening the device\n");
								break;
							}
							String configuration = LibusbJava
									.usb_get_string_simple(handle, confDesc
											.getConfiguration());
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
		} else if (component instanceof UsbInterface) {
			UsbInterface int_ = (UsbInterface) component;
			StringBuffer sb = new StringBuffer("Usb_Interface\n");
			sb.append("\tnum_altsetting: 0x"
					+ Integer.toHexString(int_.getNumAlternateSetting()) + "\n");
			sb.append("\taltsetting: " + int_.getAlternateSetting() + "\n");
			textArea.setText(sb.toString());
		} else if (component instanceof UsbInterfaceDescriptor) {
			UsbInterfaceDescriptor intDesc = (UsbInterfaceDescriptor) component;
			StringBuffer sb = new StringBuffer("Usb_Interface_Descriptor\n");
			sb.append("\tblenght: 0x"
					+ Integer.toHexString(intDesc.getBLength() & 0xFF) + "\n");
			sb.append("\tbDescriptorType: 0x"
					+ Integer.toHexString(intDesc.getBDescriptorType() & 0xFF)
					+ "\n");
			sb.append("\tbInterfaceNumber: 0x"
					+ Integer.toHexString(intDesc.getInterfaceNumber() & 0xFF)
					+ "\n");
			sb.append("\tbAlternateSetting: 0x"
					+ Integer
							.toHexString(intDesc.getAlternateSetting() & 0xFF)
					+ "\n");
			sb.append("\tbNumEndpoints: 0x"
					+ Integer.toHexString(intDesc.getNumEndpoints() & 0xFF)
					+ "\n");
			sb.append("\tbInterfaceClass: 0x"
					+ Integer.toHexString(intDesc.getInterfaceClass() & 0xFF)
					+ "\n");
			sb
					.append("\tbInterfaceSubClass: 0x"
							+ Integer.toHexString(intDesc
									.getInterfaceSubClass() & 0xFF) + "\n");
			sb
					.append("\tbInterfaceProtocol: 0x"
							+ Integer.toHexString(intDesc
									.getInterfaceProtocol() & 0xFF) + "\n");
			sb.append("\tiInterface: 0x"
					+ Integer.toHexString(intDesc.getInterface()) + "\n");
			sb.append("\textralen: 0x"
					+ Integer.toHexString(intDesc.getExtralen()) + "\n");
			sb.append("\textra: " + extraDescriptorToString(intDesc.getExtra()) + "\n");
			// get device handle to retrieve string descriptors
			UsbBus bus = rootBus;
			while (bus != null) {
				UsbDevice dev = bus.getDevices();
				while (dev != null) {
					UsbConfigDescriptor[] confDescs = dev.getConfig();
					for (int i = 0; i < confDescs.length; i++) {
						UsbInterface[] ints = confDescs[i].getInterfaces();
						for (int j = 0; j < ints.length; j++) {
							UsbInterfaceDescriptor[] tmpIntDescs = ints[j]
									.getAlternateSetting();
							for (int k = 0; k < ints.length; k++) {
								if (i < tmpIntDescs.length && tmpIntDescs[i].equals(intDesc)
										&& (intDesc.getInterface() > 0)) {
									long handle = LibusbJava.usb_open(dev);
									sb.append("\nString descriptors\n");
									if (handle <= 0) {
										sb
												.append("\terror opening the device\n");
										break;
									}
									String interface_ = LibusbJava
											.usb_get_string_simple(handle,
													intDesc.getInterface());
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
		} else if (component instanceof UsbEndpointDescriptor) {
			UsbEndpointDescriptor epDesc = (UsbEndpointDescriptor) component;
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
