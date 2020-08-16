package com.company;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 *
 * Simple program that scans through folders and filters JPG images that contain 1D or 2D barcodes
 *
 *

 *
 */
public class Main {

    // static paths to the directories used during run time
    private static final String DIR_ = "images";
    private static final String DEST_BY_BAR = "filtered_images_by_bar";
    private static final String DEST_CORRUPTED = "marked_corrupted";
    private static final String TEST_DATA_DIR = "test_images";


    public static void main(String[] args) throws IOException {
    //    renameFiles(" ", "_", new File(DEST_BY_BAR));
        File rootSrcFolder = new File(DIR_);
        File[] testDir = new File(DEST_BY_BAR).listFiles();

        for(int c = 0; c < 10000; c+=100){
            File f = testDir[c];
            Files.copy(f.toPath(), new File(TEST_DATA_DIR).toPath().resolve(f.getName()));

        }


        // Since we are dealing with the large amount of files 100.000+ expected execution is going to be long
        // we measured time in minutes/hours
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//        LocalDateTime startTime = LocalDateTime.now();
//
//        try {
//            filterImagesByBarcode(rootSrcFolder);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        LocalDateTime endTime = LocalDateTime.now();
//        System.out.println();
//        System.out.println("Start time ..... " + dtf.format(startTime));
//        System.out.println("End time .... " + dtf.format(endTime));
//        System.out.println("Images filtered .... " + AposeBarcodeScanner.discoveredCodes);


    }

    /**
     *
     * Method that sorts images in separate folder if they contain
     * barcode (are labels of medications)
     *
     * @param rootFolder - root folder represented in File object
     * @throws IOException
     */
    private static void filterImagesByBarcode(File rootFolder) throws IOException {
        File sortDestFolder = new File(DEST_BY_BAR);
        int imageCount = new File(DEST_BY_BAR).list().length;
        for (File subDir : rootFolder.listFiles()) {
            if (subDir.isDirectory()) {
            //    System.out.println("Current Dir_ " + subDir.getName());
                for (File file :  subDir.listFiles()) {
                    String ext  = getFileExtension(file);

                    // skip any file that is not a JPG image
                    if (!ext.equals("jpg"))
                        continue;

                    // skip files with 0 bytes size
                    if (file.length() == 0)
                        continue;

                    // Copy corrupted file into the folder, it is for later use, to check if file is really
                    // corrupted. To eliminate false negatives...
                    if (!jpegEnded(file.getPath())) {
                        File errFolder = new File(DEST_CORRUPTED + "/" + subDir.getName());
                        if (!errFolder.exists()) {
                            errFolder.mkdir();
                        }
                        Files.copy(file.toPath(), errFolder.toPath().resolve(file.getName()));
                        continue;
                    }

                    // if file is valid pass it to the barcode scanner to check if bar code is present
                    // if barcode is present copy the file into the destination folder
                    if (AposeBarcodeScanner.hasBarcode(file)) {
                        try {
                            Files.copy(file.toPath(), sortDestFolder.toPath().resolve("image " + imageCount++
                                    + "." + ext));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    /**
     * The method checks end of file value (EOF) of JPEG/JPG file.
     * <p>
     * From the documentation of JPEG, 0xFFD9 indicates valid EOF.
     * Therefore, method will return false if file has invalid
     * extension.
     *
     * @param path - path to the JPEG/JPG image
     * @return boolean
     * @throws IOException
     */
    private static boolean jpegEnded(String path) throws IOException {
        // Load image into randomly accessed file
        try (RandomAccessFile fh = new RandomAccessFile(path, "r")) {
            long length = fh.length(); //

            // if the length of the file is small, the file is most likely corrupted
            // Image with single gray pixel has at least 105 bytes in JPEG compression
            if (length < 100L) {
                return false;
            }

            // move file read index to last 2 bytes
            fh.seek(length - 2);
            byte[] eoi = new byte[2];
            // load last 2 bytes into the byte array
            fh.readFully(eoi);

            // from examining a lot of images, the value of last 2 bytes
            // of images that were not corrupted was either -1 or -39 or 0.
            // While corrupted images had completely different value
            if (eoi[0] == -1 || eoi[0] == -39 || eoi[0] == 0
                    && eoi[1] == -39 || eoi[1] == -1 || eoi[1] == 0) {
                return true;
            }

            System.out.println("Last 2 bytes values {" + eoi[0] + ", " + eoi[1] + "}");
            return false;
        }
    }

    /**
     * @param file - file we want to get extension from
     * @return - file extension in String object
     */
    private static String getFileExtension(File file) {
        String[] parts = file.getName().split("\\.");
        return parts[parts.length - 1];
    }

    private static void renameFiles(String target, String sub, File dir){
        for(File f: dir.listFiles() ){
            System.out.println(f.getParent());
            if(f.renameTo(new File(f.getParent()+"/"+f.getName().replace(target, sub)))){

            }else{
                System.out.println("Not renamed");
            }
        }

    }

    /**
     * method that deletes all folders/files one by one until
     * encounters limit file/folder name and returns
     * The method is useful if files are grouped/sorted in chronological order
     *
     *
     * @param file - root folder that contains subfolders
     * @param limit - name of file that method should return upon encounter
     * @throws IOException
     */
    private static void deleteFolders(File file, String limit) throws IOException{
        for(File f: file.listFiles()){
            if(f.getName().equals(limit)){
               return;
            }
                FileUtils.deleteDirectory(f);
        }
    }
}
