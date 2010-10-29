/*
 * Java libusb wrapper
 * Copyright (c) 2010 Stefano Fornari <stefano.fornari at gmail.com>
 *
 * http://code.google.com/p/usbjava/
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the USB ids database. For now it contains only few
 * devices which are hardcoded in the class. In the future it should read the
 * usb.ids file from the net and update regularly.
 * 
 * @author ste
 */
public class DeviceDatabase {

    private final Map<String, ManufacturerModels> models;

    public DeviceDatabase() {
        models = new HashMap<String, ManufacturerModels>();

        Map<String, String> canonModels = new HashMap<String, String>();
        canonModels.put("317b", "EOS 1000D");

        ManufacturerModels canon = new ManufacturerModels("Canon, Inc.");
        canon.addModels(canonModels);

        models.put("04a9", canon);

    }

    public String getVendor(String vendorId) {
        ManufacturerModels m = getVendorModels(vendorId);

        if (m != null) {
            return m.getVendor();
        }

        return null;
    }

    /**
     * Return a model name given vendor and model ids
     *
     * @param vendorId - NOT NULL
     * @param modelId - NOT NULL
     *
     * @return a model name given vendor and model ids or null if not found
     *
     * @throws IllegalArgumentException if any of the parameters is null
     */
    public String getModel(String vendorId, String modelId) {
        ManufacturerModels m = getVendorModels(vendorId);

        if (m != null) {
            return m.getModel(modelId);
        }

        return null;
    }

    /**
     * Return a model display name given vendor and model ids. The display name
     * contains the vendor name, without Inc. or Ltd. Corp
     *
     * @param vendorId - NOT NULL
     * @param modelId - NOT NULL
     *
     * @return a model display name  given vendor and model ids or null if not found
     *
     * @throws IllegalArgumentException if any of the parameters is null
     */
    public String getModelDisplayName(String vendorId, String modelId) {
        ManufacturerModels m = getVendorModels(vendorId);

        if (m != null) {
            return m.getSimpleVendorName() + ' ' + m.getModel(modelId);
        }

        return null;
    }

    // --------------------------------------------------------- Private methods
    private ManufacturerModels getVendorModels(String vendorId) {
        if (vendorId == null) {
            throw new IllegalArgumentException("vendorId cannot be null");
        }

        return models.get(vendorId);
    }

    // ----------------------------------------------------------- Inner classes
    private class ManufacturerModels {

        private String name;
        private Map<String, String> models;

        public ManufacturerModels(String name) {
            this.name = name;
            models = new HashMap<String, String>();
        }

        public void addModels(Map<String, String> toAdd) {
            models.putAll(toAdd);
        }

        public String getModel(String modelId) {
            if (modelId == null) {
                throw new IllegalArgumentException("modelId cannot be null");
            }
            return models.get(modelId);
        }

        public String getVendor() {
            return name;
        }

        public String getSimpleVendorName() {
            final String[] suffixes = new String[]{
                ", Inc.", ", Ltd", "Corp."
            };

            for (String s : suffixes) {
                if (name.endsWith(s)) {
                    return name.substring(0, name.lastIndexOf(s));
                }
            }

            return name;
        }
    }
}
