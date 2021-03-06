/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2007 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.test;

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.UsbBus;
import ch.ntb.usb.UsbConfigDescriptor;
import ch.ntb.usb.UsbDevice;
import ch.ntb.usb.UsbEndpointDescriptor;
import ch.ntb.usb.UsbInterface;
import ch.ntb.usb.UsbInterfaceDescriptor;

/**
 * This class replicates the code from testlibusb.c supplied in the
 * libusb-0.1.12 release.
 */
public class TestLibUsbJava {
	static boolean verbose;

	/**
	 * prints out endpoint info
	 * 
	 * @param endpoint
	 *            The end point.
	 */
	private static void printEndpoint(UsbEndpointDescriptor endpoint) {
		System.out.print(String.format("      bEndpointAddress: %02xh\n",
				endpoint.getEndpointAddress()));
		System.out.print(String.format("      bmAttributes:     %02xh\n",
				endpoint.getAttributes()));
		System.out.print(String.format("      wMaxPacketSize:   %d\n", endpoint
				.getMaxPacketSize()));
		System.out.print(String.format("      bInterval:        %d\n", endpoint
				.getInterval()));
		System.out.print(String.format("      bRefresh:         %d\n", endpoint
				.getRefresh()));
		System.out.print(String.format("      bSynchAddress:    %d\n", endpoint
				.getSynchAddress()));
	}

	/**
	 * prints out the interface descriptor
	 * 
	 * @param interfaceDescript
	 *            The interface descriptor.
	 */
	private static void printAltsetting(
			UsbInterfaceDescriptor interfaceDescript) {
		System.out.print(String.format("    bInterfaceNumber:   %d\n",
				interfaceDescript.getInterfaceNumber()));
		System.out.print(String.format("    bAlternateSetting:  %d\n",
				interfaceDescript.getAlternateSetting()));
		System.out.print(String.format("    bNumEndpoints:      %d\n",
				interfaceDescript.getNumEndpoints()));
		System.out.print(String.format("    bInterfaceClass:    %d\n",
				interfaceDescript.getInterfaceClass()));
		System.out.print(String.format("    bInterfaceSubClass: %d\n",
				interfaceDescript.getInterfaceSubClass()));
		System.out.print(String.format("    bInterfaceProtocol: %d\n",
				interfaceDescript.getInterfaceProtocol()));
		System.out.print(String.format("    iInterface:         %d\n",
				interfaceDescript.getInterface()));

		for (int i = 0; i < interfaceDescript.getNumEndpoints(); i++) {
			printEndpoint(interfaceDescript.getEndpoints()[i]);
		}
	}

	/**
	 * prints out interface
	 * 
	 * @param usbInterface
	 *            The interface.
	 */
	private static void printInterface(UsbInterface usbInterface) {
		for (int i = 0; i < usbInterface.getNumAlternateSetting(); i++) {
			printAltsetting(usbInterface.getAlternateSetting()[i]);
		}
	}

	/**
	 * prints out configuration
	 * 
	 * @param config
	 *            The configuration.
	 */
	private static void printConfiguration(UsbConfigDescriptor config) {
		System.out.print(String.format("  wTotalLength:         %d\n", config
				.getTotalLength()));
		System.out.print(String.format("  bNumInterfaces:       %d\n", config
				.getNumInterfaces()));
		System.out.print(String.format("  bConfigurationValue:  %d\n", config
				.getConfigurationValue()));
		System.out.print(String.format("  iConfiguration:       %d\n", config
				.getConfiguration()));
		System.out.print(String.format("  bmAttributes:         %02xh\n",
				config.getAttributes()));
		System.out.print(String.format("  MaxPower:             %d\n", config
				.getMaxPower()));

		for (int i = 0; i < config.getNumInterfaces(); i++) {
			printInterface(config.getInterfaces()[i]);
		}
	}

	private static int printDevice(UsbDevice dev, int level) {
		long udev;
		String mfr;
		String product;
		String sn;
		String spaces;
		String descript;

		spaces = "                                ";

		udev = LibusbJava.usb_open(dev);

		if (udev != 0) {
			if (dev.getDescriptor().getManufacturer() != 0) {
				mfr = LibusbJava.usb_get_string_simple(udev, dev
						.getDescriptor().getManufacturer());

				if (mfr != null) {
					descript = String.format("%s - ", mfr);
				} else {
					descript = String.format("%04X - ", dev.getDescriptor()
							.getVendorId());
				}
			} else {
				descript = String.format("%04X - ", dev.getDescriptor()
						.getVendorId());
			}

			if (dev.getDescriptor().getProduct() != 0) {
				product = LibusbJava.usb_get_string_simple(udev, dev
						.getDescriptor().getProduct());

				if (product != null) {
					descript = descript + String.format("%s", product);
				} else {
					descript = descript
							+ String.format("%04X", dev.getDescriptor()
									.getProductId());
				}
			} else {
				descript = descript
						+ String.format("%04X", dev.getDescriptor()
								.getProductId());
			}
		} else {
			descript = String.format("%04X - %04X", dev.getDescriptor()
					.getVendorId(), dev.getDescriptor().getProductId());
		}

		System.out.print(String.format("%sDev #%d: %s\n", spaces.substring(0,
				level * 2), dev.getDevnum(), descript));

		if ((udev != 0) && verbose) {
			if (dev.getDescriptor().getSerialNumber() != 0) {
				sn = LibusbJava.usb_get_string_simple(udev, dev.getDescriptor()
						.getSerialNumber());

				if (sn != null) {
					System.out.print(String.format("%s  - Serial Number: %s\n",
							spaces.substring(0, level * 2), sn));
				}
			}
		}

		if (udev != 0) {
			LibusbJava.usb_close(udev);
		}

		if (verbose) {
			if (dev.getConfig().length == 0) {
				System.out.print("  Couldn't retrieve descriptors\n");

				return 0;
			}

			for (int i = 0; i < dev.getDescriptor().getNumConfigurations(); i++) {
				printConfiguration(dev.getConfig()[i]);
			}
		} else {
			UsbDevice childDev = null;

			for (int i = 0; i < dev.getNumChildren(); i++) {
				if (i == 0) {
					childDev = dev.getChildren();
				} else {
					childDev = childDev.getNext();
				}

				printDevice(childDev, level + 1);
			}
		}

		return 0;
	} // end of printDevice method

	/**
	 * The main method.
	 * 
	 * @param args
	 *            The command line arguments.
	 */
	public static void main(String args[]) throws Exception {
		if ((args.length > 0) && (args[0].equals("-v"))) {
			verbose = true;
		} else {
			verbose = false;
		}

		// used for debugging. 0 = no debugging, 255 = with debugging
		//
		LibusbJava.usb_set_debug(255);

		LibusbJava.usb_init();

		LibusbJava.usb_find_busses();
		LibusbJava.usb_find_devices();

		for (UsbBus bus = LibusbJava.usb_get_busses(); bus != null; bus = bus
				.getNext()) {
			if ((bus.getRootDev() != null) && !verbose) {
				printDevice(bus.getRootDev(), 0);
			} else {
				for (UsbDevice dev = bus.getDevices(); dev != null; dev = dev
						.getNext()) {
					printDevice(dev, 0);
				}
			}
		}
	} // end main
} // end of TestLibUsbJava class
