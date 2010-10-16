/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

import java.io.PrintStream;

public class Utils {

	public static void logBus(UsbBus bus) {
		logBus(bus, System.out);
	}

	public static void logBus(UsbBus bus, PrintStream out) {
		UsbBus usb_Bus = bus;
		while (usb_Bus != null) {
			out.println(usb_Bus.toString());
			UsbDevice dev = usb_Bus.getDevices();
			while (dev != null) {
				out.println("\t" + dev.toString());
				// UsbDeviceDescriptor
				UsbDeviceDescriptor defDesc = dev.getDescriptor();
				out.println("\t\t" + defDesc.toString());
				// UsbConfigDescriptor
				UsbConfigDescriptor[] confDesc = dev.getConfig();
				for (int i = 0; i < confDesc.length; i++) {
					out.println("\t\t" + confDesc[i].toString());
					UsbInterface[] int_ = confDesc[i].getInterfaces();
					if (int_ != null) {
						for (int j = 0; j < int_.length; j++) {
							out.println("\t\t\t" + int_[j].toString());
							UsbInterfaceDescriptor[] intDesc = int_[j]
									.getAlternateSetting();
							if (intDesc != null) {
								for (int k = 0; k < intDesc.length; k++) {
									out.println("\t\t\t\t"
											+ intDesc[k].toString());
									UsbEndpointDescriptor[] epDesc = intDesc[k]
											.getEndpoints();
									if (epDesc != null) {
										for (int e = 0; e < epDesc.length; e++) {
											out.println("\t\t\t\t\t"
													+ epDesc[e].toString());
										}
									}
								}
							}
						}
					}
				}
				dev = dev.getNext();
			}
			usb_Bus = usb_Bus.getNext();
		}
	}
}
