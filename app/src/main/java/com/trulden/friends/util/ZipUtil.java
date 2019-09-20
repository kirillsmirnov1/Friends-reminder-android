package com.trulden.friends.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Provides zip and unzip functions
 */
public abstract class ZipUtil {

    // Taken from here
    // https://stackoverflow.com/questions/25562262/how-to-compress-files-into-zip-folder-in-android

    private static final int BUFFER = 4096;

    /**
     * Zip files from array to archive
     * @param _files paths of files to archive
     * @param zipFileName path of archive
     */
    public static void zip(String[] _files, String zipFileName) {
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(zipFileName, false);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte[] data = new byte[BUFFER];

            for (String file : _files) {
                Log.v("Compress", "Adding: " + file);
                FileInputStream fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
            dest.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unzip archive to specified directory
     * @param _zipFile path to archive
     * @param _targetLocation path to target directory
     */
    public static void unzip(String _zipFile, String _targetLocation) {

        //create target location folder if not exist
        dirChecker(_targetLocation);

        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {

                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_targetLocation + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ensures existence of directories for path
     * @param targetLocation path which should exist
     */
    private static void dirChecker(String targetLocation) {
        new File(targetLocation).mkdirs();
    }
}
