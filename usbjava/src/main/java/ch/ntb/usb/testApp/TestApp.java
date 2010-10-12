/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.testApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class TestApp extends JFrame {

	private static final long serialVersionUID = 994508729204158681L;
	TestDevice dev;
	private JPanel rootPanel = null;
	private JPanel sendReceivePanel = null;
	private JPanel settingsPanel = null;
	private JButton openDeviceButton = null;
	private JButton closeDevice = null;
	private JButton resetButton = null;
	private JPanel settingsPanelTop = null;
	private JPanel settingsPanelBottom = null;
	JTextField vendorIDText = null;
	JTextField productIDText = null;
	private JPanel vendorIDPanel = null;
	private JPanel productIDPanel = null;
	private JPanel configurationPanel = null;
	JTextField configurationText = null;
	private JPanel interfacePanel = null;
	JTextField interfaceText = null;
	private JPanel altInterfacePanel = null;
	JTextField altInterfaceText = null;
	private JPanel settingsPanelTop2Left = null;
	private JPanel settingsPanelTop2Right = null;
	private JPanel outEpPanel = null;
	JTextField outEpText = null;
	private JPanel inEpPanel = null;
	JTextField inEpText = null;
	private JPanel timeoutPanel = null;
	private JTextField timeoutText = null;
	private JPanel sendDataPanel = null;
	private JPanel sendRecButtonsPanel = null;
	private JButton sendButton = null;
	private JButton recButton = null;
	JTextField sendDataText = null;
	JComboBox sendTypeComboBox = null;
	private JComboBox recTypeComboBox = null;
	private JPanel sendRecButtonsPanelTop = null;
	private JPanel sendRecButtonsPanelBottom = null;

	public TestApp(TestDevice devInfo) {
		super();
		this.dev = devInfo;
		initialize();
	}

	private void initialize() {

		this.setTitle("USB Test Application");

		this.setContentPane(getRootPanel());

		// read default values
		this.vendorIDText.setText(toHexString(dev.getIdVendor() & 0xffff));
		this.productIDText.setText(toHexString(dev.getIdProduct() & 0xffff));
		this.configurationText.setText(new Integer(dev.getConfiguration())
				.toString());
		this.interfaceText.setText(new Integer(dev.getInterface()).toString());
		this.altInterfaceText.setText(new Integer(dev.getAltinterface())
				.toString());
		this.timeoutText.setText(new Integer(dev.getTimeout()).toString());
		this.sendDataText.setText(dev.getSendData());
		setOutEpAddr();
		setInEpAddr();

		this.pack();
		this.setVisible(true);
	}

	void setOutEpAddr() {
		switch (dev.getOutMode()) {
		case Bulk:
			this.outEpText.setText(toHexString(dev.getOutEPBulk()));
			break;
		case Interrupt:
			this.outEpText.setText(toHexString(dev.getOutEPInt()));
			break;
		default:
			break;
		}
	}

	void setInEpAddr() {
		switch (dev.getInMode()) {
		case Bulk:
			this.inEpText.setText(toHexString(dev.getInEPBulk()));
			break;
		case Interrupt:
			this.inEpText.setText(toHexString(dev.getInEPInt()));
			break;
		default:
			break;
		}
	}

	private JPanel getRootPanel() {
		if (rootPanel == null) {
			rootPanel = new JPanel();
			rootPanel
					.setLayout(new BoxLayout(getRootPanel(), BoxLayout.Y_AXIS));
			rootPanel.add(getSettingsPanel(), null);
			rootPanel.add(getSendReceivePanel(), null);
		}
		return rootPanel;
	}

	private JPanel getSendReceivePanel() {
		if (sendReceivePanel == null) {
			BorderLayout borderLayout2 = new BorderLayout();
			borderLayout2.setHgap(5);
			sendReceivePanel = new JPanel();
			sendReceivePanel.setLayout(borderLayout2);
			sendReceivePanel.setBorder(BorderFactory.createTitledBorder(null,
					"Send and Receive Data",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			sendReceivePanel.add(getSendRecButtonsPanel(), BorderLayout.NORTH);
			sendReceivePanel.add(getSendDataPanel(), BorderLayout.SOUTH);
		}
		return sendReceivePanel;
	}

	private JPanel getSettingsPanel() {
		if (settingsPanel == null) {
			settingsPanel = new JPanel();
			settingsPanel.setLayout(new BoxLayout(getSettingsPanel(),
					BoxLayout.Y_AXIS));
			settingsPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Device Settings", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			settingsPanel.add(getSettingsPanelTop(), null);
			settingsPanel.add(getSettingsPanelBottom(), null);
		}
		return settingsPanel;
	}

	private JButton getOpenDeviceButton() {
		if (openDeviceButton == null) {
			openDeviceButton = new JButton();
			openDeviceButton.setText("Open Device");
			openDeviceButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// update values for the device
							dev.setIdVendor((short) parseInt(vendorIDText
									.getText().trim()));
							dev.setIdProduct((short) parseInt(productIDText
									.getText().trim()));
							dev.setConfiguration(parseInt(configurationText
									.getText().trim()));
							dev.setInterface(parseInt(interfaceText.getText()
									.trim()));
							dev.setAltinterface(parseInt(altInterfaceText
									.getText().trim()));
							// opent the device
							dev.openUsbDevice();
						}
					});
		}
		return openDeviceButton;
	}

	private JButton getCloseDevice() {
		if (closeDevice == null) {
			closeDevice = new JButton();
			closeDevice.setText("Close Device");
			closeDevice.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dev.closeUsbDevice();
				}
			});
		}
		return closeDevice;
	}

	private JButton getResetButton() {
		if (resetButton == null) {
			resetButton = new JButton();
			resetButton.setText("Reset Device");
			resetButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dev.resetUsbDevice();
				}
			});
		}
		return resetButton;
	}

	private JPanel getSettingsPanelTop() {
		if (settingsPanelTop == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setVgap(1);
			flowLayout.setAlignment(FlowLayout.LEFT);
			settingsPanelTop = new JPanel();
			settingsPanelTop.setLayout(flowLayout);
			settingsPanelTop.add(getVendorIDPanel(), null);
			settingsPanelTop.add(getProductIDPanel(), null);
			settingsPanelTop.add(getConfigurationPanel(), null);
			settingsPanelTop.add(getInterfacePanel(), null);
			settingsPanelTop.add(getAltInterfacePanel(), null);
		}
		return settingsPanelTop;
	}

	private JPanel getSettingsPanelBottom() {
		if (settingsPanelBottom == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setVgap(1);
			flowLayout1.setHgap(0);
			flowLayout1.setAlignment(FlowLayout.LEFT);
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = -1;
			gridBagConstraints1.gridy = -1;
			settingsPanelBottom = new JPanel();
			settingsPanelBottom.setLayout(flowLayout1);
			settingsPanelBottom.add(getSettingsPanelTop2Left(), null);
			settingsPanelBottom.add(getSettingsPanelTop2Right(), null);
		}
		return settingsPanelBottom;
	}

	private JTextField getVendorIDText() {
		if (vendorIDText == null) {
			vendorIDText = new JTextField();
			vendorIDText.setPreferredSize(new Dimension(100, 20));
		}
		return vendorIDText;
	}

	private JTextField getProductIDText() {
		if (productIDText == null) {
			productIDText = new JTextField();
			productIDText.setPreferredSize(new Dimension(100, 20));
		}
		return productIDText;
	}

	private JPanel getVendorIDPanel() {
		if (vendorIDPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints3.gridy = -1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = -1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = -1;
			gridBagConstraints5.gridy = -1;
			vendorIDPanel = new JPanel();
			vendorIDPanel.setBorder(BorderFactory.createTitledBorder(null,
					"VendorID", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			vendorIDPanel.setLayout(new BoxLayout(getVendorIDPanel(),
					BoxLayout.X_AXIS));
			vendorIDPanel.add(getVendorIDText(), null);
		}
		return vendorIDPanel;
	}

	private JPanel getProductIDPanel() {
		if (productIDPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints4.gridy = -1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.gridx = -1;
			productIDPanel = new JPanel();
			productIDPanel.setBorder(BorderFactory.createTitledBorder(null,
					"ProductID", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			productIDPanel.setLayout(new BoxLayout(getProductIDPanel(),
					BoxLayout.X_AXIS));
			productIDPanel.add(getProductIDText(), null);
		}
		return productIDPanel;
	}

	private JPanel getConfigurationPanel() {
		if (configurationPanel == null) {
			configurationPanel = new JPanel();
			configurationPanel.setLayout(new BoxLayout(getConfigurationPanel(),
					BoxLayout.X_AXIS));
			configurationPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Configuration", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			configurationPanel.add(getConfigurationText(), null);
		}
		return configurationPanel;
	}

	private JTextField getConfigurationText() {
		if (configurationText == null) {
			configurationText = new JTextField();
			configurationText.setPreferredSize(new Dimension(100, 20));
		}
		return configurationText;
	}

	private JPanel getInterfacePanel() {
		if (interfacePanel == null) {
			interfacePanel = new JPanel();
			interfacePanel.setLayout(new BoxLayout(getInterfacePanel(),
					BoxLayout.X_AXIS));
			interfacePanel.setBorder(BorderFactory.createTitledBorder(null,
					"Interface", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			interfacePanel.add(getInterfaceText(), null);
		}
		return interfacePanel;
	}

	private JTextField getInterfaceText() {
		if (interfaceText == null) {
			interfaceText = new JTextField();
			interfaceText.setPreferredSize(new Dimension(100, 20));
		}
		return interfaceText;
	}

	private JPanel getAltInterfacePanel() {
		if (altInterfacePanel == null) {
			altInterfacePanel = new JPanel();
			altInterfacePanel.setLayout(new BoxLayout(getAltInterfacePanel(),
					BoxLayout.X_AXIS));
			altInterfacePanel.setBorder(BorderFactory.createTitledBorder(null,
					"Alternate Int", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			altInterfacePanel.add(getAltInterfaceText(), null);
		}
		return altInterfacePanel;
	}

	private JTextField getAltInterfaceText() {
		if (altInterfaceText == null) {
			altInterfaceText = new JTextField();
			altInterfaceText.setPreferredSize(new Dimension(100, 20));
		}
		return altInterfaceText;
	}

	private JPanel getSettingsPanelTop2Left() {
		if (settingsPanelTop2Left == null) {
			FlowLayout flowLayout2 = new FlowLayout();
			flowLayout2.setVgap(2);
			flowLayout2.setAlignment(FlowLayout.LEFT);
			flowLayout2.setHgap(5);
			settingsPanelTop2Left = new JPanel();
			settingsPanelTop2Left.setLayout(flowLayout2);
			settingsPanelTop2Left.add(getOutEpPanel(), null);
			settingsPanelTop2Left.add(getInEpPanel(), null);
			settingsPanelTop2Left.add(getTimeoutPanel(), null);
		}
		return settingsPanelTop2Left;
	}

	private JPanel getSettingsPanelTop2Right() {
		if (settingsPanelTop2Right == null) {
			FlowLayout flowLayout3 = new FlowLayout();
			flowLayout3.setVgap(2);
			settingsPanelTop2Right = new JPanel();
			settingsPanelTop2Right.setLayout(flowLayout3);
			settingsPanelTop2Right.add(getOpenDeviceButton(), null);
			settingsPanelTop2Right.add(getCloseDevice(), null);
			settingsPanelTop2Right.add(getResetButton(), null);
		}
		return settingsPanelTop2Right;
	}

	private JPanel getOutEpPanel() {
		if (outEpPanel == null) {
			outEpPanel = new JPanel();
			outEpPanel.setLayout(new BoxLayout(getOutEpPanel(),
					BoxLayout.X_AXIS));
			outEpPanel.setBorder(BorderFactory.createTitledBorder(null,
					"OUT EP", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			outEpPanel.add(getOutEpText(), null);
		}
		return outEpPanel;
	}

	private JTextField getOutEpText() {
		if (outEpText == null) {
			outEpText = new JTextField();
			outEpText.setPreferredSize(new Dimension(100, 20));
		}
		return outEpText;
	}

	private JPanel getInEpPanel() {
		if (inEpPanel == null) {
			inEpPanel = new JPanel();
			inEpPanel
					.setLayout(new BoxLayout(getInEpPanel(), BoxLayout.X_AXIS));
			inEpPanel.setBorder(BorderFactory.createTitledBorder(null, "IN EP",
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			inEpPanel.add(getInEpText(), null);
		}
		return inEpPanel;
	}

	private JTextField getInEpText() {
		if (inEpText == null) {
			inEpText = new JTextField();
			inEpText.setPreferredSize(new Dimension(100, 20));
		}
		return inEpText;
	}

	private JPanel getTimeoutPanel() {
		if (timeoutPanel == null) {
			timeoutPanel = new JPanel();
			timeoutPanel.setLayout(new BoxLayout(getTimeoutPanel(),
					BoxLayout.X_AXIS));
			timeoutPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Timeout", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			timeoutPanel.add(getTimeoutText(), null);
		}
		return timeoutPanel;
	}

	private JTextField getTimeoutText() {
		if (timeoutText == null) {
			timeoutText = new JTextField();
			timeoutText.setPreferredSize(new Dimension(100, 20));
		}
		return timeoutText;
	}

	private JPanel getSendDataPanel() {
		if (sendDataPanel == null) {
			FlowLayout flowLayout4 = new FlowLayout();
			flowLayout4.setAlignment(FlowLayout.LEFT);
			sendDataPanel = new JPanel();
			sendDataPanel.setLayout(flowLayout4);
			sendDataPanel.setBorder(BorderFactory.createTitledBorder(null,
					"Data to send [hex]", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			sendDataPanel.add(getSendRecDataText(), null);
		}
		return sendDataPanel;
	}

	private JPanel getSendRecButtonsPanel() {
		if (sendRecButtonsPanel == null) {
			FlowLayout flowLayout5 = new FlowLayout();
			flowLayout5.setAlignment(FlowLayout.LEFT);
			flowLayout5.setVgap(0);
			sendRecButtonsPanel = new JPanel();
			sendRecButtonsPanel.setLayout(flowLayout5);
			sendRecButtonsPanel.add(getSendRecButtonsPanelTop(), null);
			sendRecButtonsPanel.add(getSendRecButtonsPanelBottom(), null);
		}
		return sendRecButtonsPanel;
	}

	private JButton getSendButton() {
		if (sendButton == null) {
			sendButton = new JButton();
			sendButton.setText("Send");
			sendButton.setName("sendButton");
			sendButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int index = sendTypeComboBox.getSelectedIndex();
					if (index == TestDevice.TransferMode.Bulk.ordinal()) {
						dev.setOutEPBulk(parseInt(outEpText.getText().trim()));
						dev.setMode(TestDevice.TransferMode.Bulk);
					} else if (index == TestDevice.TransferMode.Interrupt
							.ordinal()) {
						dev.setOutEPInt(parseInt(outEpText.getText().trim()));
						dev.setMode(TestDevice.TransferMode.Interrupt);
					}
					byte[] data = parseByteArray(sendDataText.getText().trim());
					dev.write(data, data.length);
				}
			});
		}
		return sendButton;
	}

	private JButton getRecButton() {
		if (recButton == null) {
			recButton = new JButton();
			recButton.setText("Receive");
			recButton.setName("recButton");
			recButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int index = sendTypeComboBox.getSelectedIndex();
					if (index == TestDevice.TransferMode.Bulk.ordinal()) {
						dev.setInEPBulk(parseInt(inEpText.getText().trim()));
						dev.setMode(TestDevice.TransferMode.Bulk);
					} else if (index == TestDevice.TransferMode.Interrupt
							.ordinal()) {
						dev.setInEPInt(parseInt(inEpText.getText().trim()));
						dev.setMode(TestDevice.TransferMode.Interrupt);
					}
					dev.read();
				}
			});
		}
		return recButton;
	}

	private JTextField getSendRecDataText() {
		if (sendDataText == null) {
			sendDataText = new JTextField();
			sendDataText.setPreferredSize(new Dimension(650, 20));
		}
		return sendDataText;
	}

	int parseInt(String s) {
		if (s == "")
			return 0;
		if (s.indexOf('x') > 0) {
			// is hex number
			if (s.length() <= 2) { // exception for "0x"
				return 0;
			}
			return Integer.parseInt(
					s.substring(s.indexOf('x') + 1, s.length()), 16);
		}
		// is decimal number
		return Integer.parseInt(s);
	}

	byte[] parseByteArray(String s) {
		final int HEX_WIDTH = 5;

		StringBuffer sb = new StringBuffer();
		int stringIndex = 0, spaceIndex = 0;
		String ss;
		while (stringIndex + 3 < s.length()) {
			ss = s.substring(spaceIndex, spaceIndex + 4);
			spaceIndex = s.indexOf(' ', stringIndex) + 1;
			sb.append((char) parseInt(ss));
			stringIndex += HEX_WIDTH;
		}
		return sb.toString().getBytes();
	}

	private static String toHexString(int value) {
		return "0x" + Integer.toHexString(value);
	}

	/**
	 * This method initializes sendTypeComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getSendTypeComboBox() {
		if (sendTypeComboBox == null) {
			sendTypeComboBox = new JComboBox(dev.getTransferTypes());
			sendTypeComboBox.setSelectedIndex(dev.getOutMode().ordinal());
			sendTypeComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						String mode = (String) e.getItem();
						if (mode.equalsIgnoreCase("Bulk")) {
							dev.setOutMode(TestDevice.TransferMode.Bulk);
							setOutEpAddr();
						} else if (mode.equalsIgnoreCase("Interrupt")) {
							dev.setOutMode(TestDevice.TransferMode.Interrupt);
							setOutEpAddr();
						}
					}
				}
			});
		}
		return sendTypeComboBox;
	}

	/**
	 * This method initializes recTypeComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getRecTypeComboBox() {
		if (recTypeComboBox == null) {
			recTypeComboBox = new JComboBox(dev.getTransferTypes());
			recTypeComboBox.setSelectedIndex(dev.getInMode().ordinal());
			recTypeComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						String mode = (String) e.getItem();
						if (mode.equalsIgnoreCase("Bulk")) {
							dev.setInMode(TestDevice.TransferMode.Bulk);
							setInEpAddr();
						} else if (mode.equalsIgnoreCase("Interrupt")) {
							dev.setInMode(TestDevice.TransferMode.Interrupt);
							setInEpAddr();
						}
					}
				}
			});
			// recTypeComboBox.addActionListener(new
			// java.awt.event.ActionListener() {
			// public void actionPerformed(java.awt.event.ActionEvent e) {
			// JComboBox source = (JComboBox) e.getSource();
			// String mode = "";
			// } if (mode.equalsIgnoreCase("Bulk")) {
			// dev.setInMode(TestDevice.TransferMode.Bulk);
			// setInEpAddr();
			// } else if (mode.equalsIgnoreCase("Interrupt")) {
			// dev.setInMode(TestDevice.TransferMode.Interrupt);
			// setInEpAddr();
			// }
			//
			// });
		}
		return recTypeComboBox;
	}

	/**
	 * This method initializes sendRecButtonsPanelTop
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSendRecButtonsPanelTop() {
		if (sendRecButtonsPanelTop == null) {
			BorderLayout borderLayout1 = new BorderLayout();
			borderLayout1.setHgap(5);
			sendRecButtonsPanelTop = new JPanel();
			sendRecButtonsPanelTop.setBorder(BorderFactory.createTitledBorder(
					null, "OUT", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 12), new Color(51, 51, 51)));
			sendRecButtonsPanelTop.setLayout(borderLayout1);
			sendRecButtonsPanelTop.add(getSendButton(), BorderLayout.EAST);
			sendRecButtonsPanelTop
					.add(getSendTypeComboBox(), BorderLayout.WEST);
		}
		return sendRecButtonsPanelTop;
	}

	/**
	 * This method initializes sendRecButtonsPanelBottom
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSendRecButtonsPanelBottom() {
		if (sendRecButtonsPanelBottom == null) {
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setHgap(5);
			sendRecButtonsPanelBottom = new JPanel();
			sendRecButtonsPanelBottom.setBorder(BorderFactory
					.createTitledBorder(null, "IN",
							TitledBorder.DEFAULT_JUSTIFICATION,
							TitledBorder.DEFAULT_POSITION, new Font("Dialog",
									Font.BOLD, 12), new Color(51, 51, 51)));
			sendRecButtonsPanelBottom.setLayout(borderLayout);
			sendRecButtonsPanelBottom.add(getRecButton(), BorderLayout.EAST);
			sendRecButtonsPanelBottom.add(getRecTypeComboBox(),
					BorderLayout.WEST);
		}
		return sendRecButtonsPanelBottom;
	}

	public static void main(String[] args) {
		// set LookAndFeel
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		TestApp app = new TestApp(new TestDevice());
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
