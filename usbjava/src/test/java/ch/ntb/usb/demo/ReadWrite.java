/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schlpfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.demo;

import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;

/**
 * Demo class to demonstrate simple read and write operations to an USB device.<br>
 * 
 */
public class ReadWrite {

	private static void logData(byte[] data) {
		System.out.print("Data: ");
		for (int i = 0; i < data.length; i++) {
			System.out.print("0x" + Integer.toHexString(data[i] & 0xff) + " ");
		}
		System.out.println();
	}

	public static void main(String[] args) {
		// get a device instance with vendor id and product id
		Device dev = USB.getDevice((short) 0x8235, (short) 0x0222);
		try {
			// data to write to the device
			byte[] data = new byte[] { 0, 1, 2, 3 };
			// data read from the device
			byte[] readData = new byte[data.length];

			// open the device with configuration 1, interface 0 and without
			// altinterface
			// this will initialise Libusb for you
			dev.open(1, 0, -1);
			// write some data to the device
			// 0x03 is the endpoint address of the OUT endpoint 3 (from PC to
			// device)
			dev.writeInterrupt(0x03, data, data.length, 2000, false);
			// read some data from the device
			// 0x84 is the endpoint address of the IN endpoint 4 (from PC to
			// device)
			// bit 7 (0x80) is set in case of an IN endpoint
			dev.readInterrupt(0x84, readData, readData.length, 2000, false);
			// log the data from the device
			logData(readData);
			// close the device
			dev.close();
		} catch (USBException e) {
			// if an exception occures during connect or read/write an exception
			// is thrown
			e.printStackTrace();
		}
	}
}
