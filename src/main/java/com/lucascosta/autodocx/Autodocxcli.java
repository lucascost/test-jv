/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.lucascosta.autodocx;

import static com.lucascosta.autodocx.Flags.options;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import org.apache.poi.xwpf.usermodel.*;

/**
 *
 * @author lucas
 */
public class Autodocxcli {

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        String filePath = "";
        String outputFilePath = "";
        Map<String, String> replaces = new HashMap<>();
         
        try {
            CommandLine cmd = parser.parse(Flags.options, args);
            filePath = cmd.getOptionValue("t");
            outputFilePath = cmd.getOptionValue("o");
            
            if(outputFilePath == null){
                outputFilePath = filePath.substring(0,filePath.length()-5)+"(EDIT).docx";
            }
            args = cmd.getOptionValues("r");            

        } catch (ParseException e) {
            System.err.println("Error processing arguments: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("AutoDocx", options);
            System.exit(1);
        }
        
        for(String arg : args) {
            String[] par = arg.split(":", 2);
            if (par.length == 2) {
                replaces.put(par[0], par[1]);
            } else {
                System.out.println("Invalid format for: " + arg + ". Use key:value.");
            }
        }
        
        replaceInDocx(filePath, outputFilePath, replaces);       
        
    }
    private static void replaceInDocx(String filePath, String outputFilePath, Map<String, String> replacements) {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                replaceTextInParagraph(paragraph, replacements);
            }

            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            replaceTextInParagraph(paragraph, replacements);
                        }
                    }
                }
            }
            
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                document.write(fos);
            }

            System.out.println("Success.");
        } catch (IOException e) {
            System.err.println("Error while processing file: " + e.getMessage());
        }
    }

    private static void replaceTextInParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                    text = text.replace(entry.getKey(), entry.getValue());
                }
                run.setText(text, 0);
            }
        }
    }
}
