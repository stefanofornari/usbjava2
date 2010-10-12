/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2007 Andreas Schläpfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.test;

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.Usb_Bus;
import ch.ntb.usb.Usb_Config_Descriptor;
import ch.ntb.usb.Usb_Device;
import ch.ntb.usb.Usb_Endpoint_Descriptor;
import ch.ntb.usb.Usb_Interface;
import ch.ntb.usb.Usb_Interface_Descriptor;

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
	private static void printEndpoint(Usb_Endpoint_Descriptor endpoint) {
		System.out.print(String.format("      bEndpointAddress: %02xh\n",
				endpoint.getBEndpointAddress()));
		System.out.print(String.format("      bmAttributes:     %02xh\n",
				endpoint.getBmAttributes()));
		System.out.print(String.format("      wMaxPacketSize:   %d\n", endpoint
				.getWMaxPacketSize()));
		System.out.print(String.format("      bInterval:        %d\n", endpoint
				.getBInterval()));
		System.out.print(String.format("      bRefresh:         %d\n", endpoint
				.getBRefresh()));
		System.out.print(String.format("      bSynchAddress:    %d\n", endpoint
				.getBSynchAddress()));
	}

	/**
	 * prints out the interface descriptor
	 * 
	 * @param interfaceDescript
	 *            The interface descriptor.
	 */
	private static void printAltsetting(
			Usb_Interface_Descriptor interfaceDescript) {
		System.out.print(String.format("    bInterfaceNumber:   %d\n",
				interfaceDescript.getBInterfaceNumber()));
		System.out.print(String.format("    bAlternateSetting:  %d\n",
				interfaceDescript.getBAlternateSetting()));
		System.out.print(String.format("    bNumEndpoints:      %d\n",
				interfaceDescript.getBNumEndpoints()));
		System.out.print(String.format("    bInterfaceClass:    %d\n",
				interfaceDescript.getBInterfaceClass()));
		System.out.print(String.format("    bInterfaceSubClass: %d\n",
				interfaceDescript.getBInterfaceSubClass()));
		System.out.print(String.format("    bInterfaceProtocol: %d\n",
				interfaceDescript.getBInterfaceProtocol()));
		System.out.print(String.format("    iInterface:         %d\n",
				interfaceDescript.getIInterface()));

		for (int i = 0; i < interfaceDescript.getBNumEndpoints(); i++) {
			printEndpoint(interfaceDescript.getEndpoint()[i]);
		}
	}

	/**
	 * prints out interface
	 * 
	 * @param usbInterface
	 *            The interface.
	 */
	private static void printInterface(Usb_Interface usbInterface) {
		for (int i = 0; i < usbInterface.getNumAltsetting(); i++) {
			printAltsetting(usbInterface.getAltsetting()[i]);
		}
	}

	/**
	 * prints out configuration
	 * 
	 * @param config
	 *            The configuration.
	 */
	private static void printConfiguration(Usb_Config_Descriptor config) {
		System.out.print(String.format("  wTotalLength:         %d\n", config
				.getWTotalLength()));
		System.out.print(String.format("  bNumInterfaces:       %d\n", config
				.getBNumInterfaces()));
		System.out.print(String.format("  bConfigurationValue:  %d\n", config
				.getBConfigurationValue()));
		System.out.print(String.format("  iConfiguration:       %d\n", config
				.getIConfiguration()));
		System.out.print(String.format("  bmAttributes:         %02xh\n",
				config.getBmAttributes()));
		System.out.print(String.format("  MaxPower:             %d\n", config
				.getMaxPower()));

		for (int i = 0; i < config.getBNumInterfaces(); i++) {
			printInterface(config.getInterface()[i]);
		}
	}

	private static int printDevice(Usb_Device dev, int level) {
		long udev;
		String mfr;
		String product;
		String sn;
		String spaces;
		String descript;

		spaces = "                                ";

		udev = LibusbJava.usb_open(dev);

		if (udev != 0) {
			if (dev.getDescriptor().getIManufacturer() != 0) {
				mfr = LibusbJava.usb_get_string_simple(udev, dev
						.getDescriptor().getIManufacturer());

				if (mfr != null) {
					descript = String.format("%s - ", mfr);
				} else {
					descript = String.format("%04X - ", dev.getDescriptor()
							.getIdVendor());
				}
			} else {
				descript = String.format("%04X - ", dev.getDescriptor()
						.getIdVendor());
			}

			if (dev.getDescriptor().getIProduct() != 0) {
				product = LibusbJava.usb_get_string_simple(udev, dev
						.getDescriptor().getIProduct());

				if (product != null) {
					descript = descript + String.format("%s", product);
				} else {
					descript = descript
							+ String.format("%04X", dev.getDescriptor()
									.getIdProduct());
				}
			} else {
				descript = descript
						+ String.format("%04X", dev.getDescriptor()
								.getIdProduct());
			}
		} else {
			descript = String.format("%04X - %04X", dev.getDescriptor()
					.getIdVendor(), dev.getDescriptor().getIdProduct());
		}

		System.out.print(String.format("%sDev #%d: %s\n", spaces.substring(0,
				level * 2), dev.getDevnum(), descript));

		if ((udev != 0) && verbose) {
			if (dev.getDescriptor().getISerialNumber() != 0) {
				sn = LibusbJava.usb_get_string_simple(udev, dev.getDescriptor()
						.getISerialNumber());

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

			for (int i = 0; i < dev.getDescriptor().getBNumConfigurations(); i++) {
				printConfiguration(dev.getConfig()[i]);
			}
		} else {
			Usb_Device childDev = null;

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

		for (Usb_Bus bus = LibusbJava.usb_get_busses(); bus != null; bus = bus
				.getNext()) {
			if ((bus.getRootDev() != null) && !verbose) {
				printDevice(bus.getRootDev(), 0);
			} else {
				for (Usb_Device dev = bus.getDevices(); dev != null; dev = dev
						.getNext()) {
					printDevice(dev, 0);
				}
			}
		}
	} // end main
} // end of TestLibUsbJava class
