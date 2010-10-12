/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.test.devices;

import ch.ntb.usb.USB;
import ch.ntb.usb.demo.AbstractDeviceInfo;

public class CY7C68013A extends AbstractDeviceInfo {

	@Override
	public void initValues() {
		setIdVendor((short) 0x8235);
		setIdProduct((short) 0x0222);
		setTimeout(2000);
		setConfiguration(1);
		setInterface(0);
		setAltinterface(-1);
		setOutEPInt(0x02);
		setInEPInt(0x86);
		setOutEPBulk(0x04);
		setInEPBulk(0x88);
		setSleepTimeout(2000);
		setMaxDataSize(USB.HIGHSPEED_MAX_BULK_PACKET_SIZE);
		setMode(TransferMode.Bulk);
	}
}
