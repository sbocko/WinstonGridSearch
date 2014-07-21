package sk.upjs.winston.gridsearch.converter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by stefan on 7/12/14.
 */
public class AttributeNamer {

    private static String[] files = {};

    /**
     * takes 1 arguments:
     * - dataset directory
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("\nUsage: AttributeCounter <filedir> \n");
            System.exit(1);
        }

        for (String filename : files) {
            String filePath = args[0];
//            String filePath = "/Users/stefan/stefan_bocko@master.exp.upjs.sk/";
            if (filePath.endsWith("/") || filePath.endsWith("\\")) {
                filePath += filename;
            } else {
                filePath += "/" + filename;
            }

            File file = new File(filePath);

            if (file.exists() && file.isFile()) {
                try {
                    Scanner s = new Scanner(file);
                    String line = s.nextLine();
                    s.close();
                    int numberOfAttrs = line.split(",").length;

                    if (line.contains("'") || numberOfAttrs < 4 || numberOfAttrs > 100 || file.length() > 10000000) {
                        System.out.println("Unknown format: " + file);
                    } else {
                        System.out.println("Number of attributes for file: " + file.getName() + " " + numberOfAttrs);
                        String names = createAttributeNames(numberOfAttrs);
                        String fileString = FileUtils.readFileToString(file);

                        PrintWriter writer = new PrintWriter(file.getAbsolutePath());
                        writer.println(names);
                        writer.write(fileString);
                        writer.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Err reading file: " + file.getName());
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Err reading file: " + file.getName());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }

    private static String createAttributeNames(int numberOfAttributes) {
        String result = "";
        for (int i = 1; i < numberOfAttributes; i++) {
            result += "attribute_" + i + ", ";
        }
        result += "attribute_" + numberOfAttributes;
        return result;
    }
}
