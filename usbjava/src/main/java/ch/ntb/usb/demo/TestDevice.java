/*
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.demo;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;
import ch.ntb.usb.logger.LogUtil;

public class TestDevice extends AbstractDeviceInfo {

	private static final Logger logger = LogUtil.getLogger("ch.ntb.usb.test");

	private String sendData = "0x5b 0x02 0x01 0x00 0x03 0x03 0xf0 0xf0 0x1f";

	private Vector<String> transferTypes;

	private static Device dev = null;

	private TransferMode inMode;
	private TransferMode outMode;

	public TestDevice() {
		logger.setLevel(Level.ALL);
		// create a vector for transfer types
		transferTypes = new Vector<String>();
		transferTypes
				.add(TransferMode.Bulk.ordinal(), TransferMode.Bulk.name());
		transferTypes.add(TransferMode.Interrupt.ordinal(),
				TransferMode.Interrupt.name());
		inMode = TransferMode.Bulk;
		outMode = TransferMode.Bulk;
	}

	@Override
	public void initValues() {
		setIdVendor((short) 0x8235);
		setIdProduct((short) 0x0222);
		setTimeout(2000);
		setConfiguration(1);
		setInterface(0);
		setAltinterface(-1);
		setOutEPBulk(0x01);
		setInEPBulk(0x82);
		setOutEPInt(0x03);
		setInEPInt(0x84);
		setSleepTimeout(2000);
		setMaxDataSize(USB.FULLSPEED_MAX_BULK_PACKET_SIZE);
		setMode(TransferMode.Bulk);
	}

	public void openUsbDevice() {
		dev = USB.getDevice(getIdVendor(), getIdProduct());
		try {
			dev.open(getConfiguration(), getInterface(), getAltinterface());
			logger.info("device opened, interface claimed");
		} catch (USBException e) {
			e.printStackTrace();
		}
	}

	public void closeUsbDevice() {
		try {
			if (dev != null) {
				dev.close();
				logger.info("device closed");
			} else {
				logger.warning("no device to close -> open first");
			}
		} catch (USBException e) {
			e.printStackTrace();
		}
	}

	public void resetUsbDevice() {
		try {
			if (dev != null) {
				dev.reset();
				logger.info("device reset");
			} else {
				logger.warning("no device to reset -> open first");
			}
		} catch (USBException e) {
			e.printStackTrace();
		}
	}

	public void write(byte[] data, int length) {
		int lenWritten = 0;
		try {
			if (dev != null) {
				StringBuffer sb = new StringBuffer();
				switch (getOutMode()) {
				case Bulk:
					lenWritten = dev.writeBulk(getOutEPBulk(), data, length,
							getTimeout(), false);
					sb.append("write_bulk, ep: 0x"
							+ Integer.toHexString(getOutEPBulk()) + ", "
							+ lenWritten + " Bytes sent: ");
					break;
				case Interrupt:
					lenWritten = dev.writeInterrupt(getOutEPInt(), data,
							length, getTimeout(), false);
					sb.append("write_interrupt, ep: 0x"
							+ Integer.toHexString(getOutEPInt()) + ", "
							+ lenWritten + " Bytes sent: ");
					break;
				}
				for (int i = 0; i < lenWritten; i++) {
					sb.append("0x" + String.format("%1$02X", data[i]) + " ");
				}
				logger.info(sb.toString());
			} else {
				logger.warning("no device opened");
			}
		} catch (USBException e) {
			e.printStackTrace();
		}
	}

	public void read() {
		if (dev != null) {
			byte[] data = new byte[dev.getMaxPacketSize()];
			int lenRead = 0;
			try {
				StringBuffer sb = new StringBuffer();
				switch (getInMode()) {
				case Bulk:
					lenRead = dev.readBulk(getInEPBulk(), data, dev
							.getMaxPacketSize(), getTimeout(), false);
					sb.append("read_bulk, ep: 0x"
							+ Integer.toHexString(getInEPBulk()) + ", "
							+ lenRead + " Bytes received: Data: ");
					break;
				case Interrupt:
					lenRead = dev.readInterrupt(getInEPInt(), data, dev
							.getMaxPacketSize(), getTimeout(), false);
					sb.append("read_interrupt, ep: 0x"
							+ Integer.toHexString(getInEPInt()) + ", "
							+ lenRead + " Bytes received: Data: ");
					break;
				}
				for (int i = 0; i < lenRead; i++) {
					sb.append("0x" + String.format("%1$02X", data[i]) + " ");
				}
				logger.info(sb.toString());
			} catch (USBException e) {
				e.printStackTrace();
			}
		} else {
			logger.warning("no device opened");
		}
	}

	public String getSendData() {
		return sendData;
	}

	public void setSendData(String sendData) {
		this.sendData = sendData;
	}

	public Vector<String> getTransferTypes() {
		return transferTypes;
	}

	public TransferMode getOutMode() {
		return outMode;
	}

	public void setOutMode(TransferMode outMode) {
		this.outMode = outMode;
	}

	public TransferMode getInMode() {
		return inMode;
	}

	public void setInMode(TransferMode inMode) {
		this.inMode = inMode;
	}
}
