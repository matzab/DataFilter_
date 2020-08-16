package com.company;
import com.aspose.barcode.barcoderecognition.BarCodeReader;
import com.aspose.barcode.barcoderecognition.BarCodeResult;
import com.aspose.barcode.barcoderecognition.DecodeType;

import java.io.File;


/**
 * We use Apose Barcode library to check for present
 * barcodes in the images.
 *
 * Source: https://docs.aspose.com/display/barcodejava/Product+Overview
 *
 */
public class AposeBarcodeScanner {
    static int discoveredCodes = 0;


    /**
     * Simple method that scanns through image and checks for present
     * barcodes
     *
     * @param f - image that needs to be scanned
     * @return - return true if barcode was found else return false
     */
    public static boolean hasBarcode(File f) {

        BarCodeReader reader = new BarCodeReader(f.getPath(),
                DecodeType.ALL_SUPPORTED_TYPES);

        for (BarCodeResult result : reader.readBarCodes()) {
            if (result != null) {
                return true;
            }

        }
        return false;
    }
}

