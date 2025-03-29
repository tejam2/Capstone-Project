package com.rabeet.spring;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utilities {
    public static String todaysDate() {
        String pattern = "MM-dd-yy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String date = format.format(Calendar.getInstance().getTime());
        return date;
    }

    public static void executeSystemCommand(String command) throws IOException, InterruptedException {
        java.lang.Runtime rt = java.lang.Runtime.getRuntime();
        Process exec = rt.exec(command);
        exec.waitFor();
    }

    public static void replaceFromFile(String fileName, String oldLine, String newLine) {
        try {
            // input the file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(fileName));
            String line;
            StringBuffer inputBuffer = new StringBuffer();

            while ((line = file.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            String inputStr = inputBuffer.toString();

            file.close();

            // this if structure determines whether or not to replace "0" or "1"
            inputStr = inputStr.replace(oldLine, newLine);

            // write the new String with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(fileName);
            fileOut.write(inputStr.getBytes());
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }

    public static boolean createFolder(String name) {
        File file = new File(name);
        if (file.isDirectory())
            return false;
        return file.mkdir();
    }

    public static String[] getDirectories() {
        return getDirectoriesInPath(".");
    }

    public static String[] getDirectoriesInPath(String path) {
        File file = new File(path);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        return directories;
    }

    public static BufferedReader getFileReader(String FILENAME) {
        Reader reader = null;
        try {
            InputStream myFileStream = new FileInputStream(FILENAME);
            reader = new BufferedReader(new InputStreamReader(myFileStream, "utf-8"));
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.err.println(FILENAME + " does not exist!");
            System.exit(1);
        }
        return (BufferedReader) reader;
    }

    public static void writeToFile(String file, String text, boolean append) {

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, append), "utf-8"))) {
            writer.write(text);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}