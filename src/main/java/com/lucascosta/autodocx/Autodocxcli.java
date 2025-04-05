/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.lucascosta.autodocx;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
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

        Flags.verify(args);
         
        try {
            CommandLine cmd = parser.parse(Flags.options, args);
            filePath = cmd.getOptionValue("t");
            outputFilePath = cmd.getOptionValue("t");
            
            if(outputFilePath.equals("")){
                outputFilePath = filePath.substring(0,filePath.length()-5)+"(EDIT).docx";
            }
            args = cmd.getOptionValues("r");
            System.out.println(Arrays.toString(args));
            

        } catch (ParseException e) {
            System.err.println("Error processing arguments: " + e.getMessage());
            System.exit(1);
        }
        
        
        Map<String, String> replaces = new HashMap<>();
        
        for (int i = 0; i < args.length; i++) {
            String[] par = args[i].split(":", 2);
            if (par.length == 2) {
                replaces.put(par[0], par[1]);
            } else {
                System.out.println("Formato inválido para: " + args[i] + ". Use chave:valor.");
                
            }
        }
        
        System.out.println("REPLACES = " + replaces);
        substituirNoDocx(filePath, replaces);
        
        
    }
    private static void substituirNoDocx(String filePath, Map<String, String> replacements) {
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
                String newFilePath = filePath.substring(0,filePath.length()-5)+"(EDIT).docx";
                try (FileOutputStream fos = new FileOutputStream(newFilePath)) {
                    document.write(fos);
                }

                System.out.println("Substituição concluída com sucesso!");
            } catch (IOException e) {
                System.err.println("Erro ao processar o arquivo: " + e.getMessage());
            }
        }

        private static void replaceTextInParagraph(XWPFParagraph paragraph, Map<String, String> replacements) {
            for (XWPFRun run : paragraph.getRuns()) {
                System.out.println("RUN: '"+run.getText(0)+"'");
                String text = run.getText(0);
                if (text != null) {
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        text = text.replace(entry.getKey(), entry.getValue());
                        
                        if(text.equals(entry.getKey())){
                            System.out.println("erro ao substituir o parâmetro "+entry.getKey());
                        }
                    }
                    run.setText(text, 0);
                }
            }
        }
}
