/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.demo;

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.UsbBus;
import ch.ntb.usb.Utils;

/**
 * Initalises Libusb and prints the bus(ses) with attached devices to the
 * standard out.<br>
 * 
 * 
 */
public class LogBus {

	private static void logBus() {
		// if you don't use the ch.ntb.usb.Device class you must initialise
		// Libusb before use
		LibusbJava.usb_init();
		LibusbJava.usb_find_busses();
		LibusbJava.usb_find_devices();

		// retrieve a object tree representing the bus with its devices and
		// descriptors
		UsbBus bus = LibusbJava.usb_get_busses();

		// log the bus structure to standard out
		Utils.logBus(bus);
	}

	public static void main(String[] args) {
		logBus();
	}
}
