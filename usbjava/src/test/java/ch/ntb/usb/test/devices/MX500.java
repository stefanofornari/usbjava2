/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.test.devices;

import ch.ntb.usb.demo.AbstractDeviceInfo;

public class MX500 extends AbstractDeviceInfo {

	@Override
	public void initValues() {
		setIdVendor((short) 0x046d);
		setIdProduct((short) 0xc025);
		setTimeout(2000);
		setConfiguration(1);
		setInterface(0);
		setAltinterface(0);
		setOutEPInt(-1);
		setInEPInt(0x81);
		setOutEPBulk(-1);
		setInEPBulk(-1);
		setSleepTimeout(5000);
		setMaxDataSize(5);
		setMode(TransferMode.Interrupt);
		// we only read data -> don't compare
		setDoCompareData(false);
	}
}
