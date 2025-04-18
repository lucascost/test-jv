/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.lucascosta.autodocx;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.xwpf.usermodel.*;

/**
 *
 * @author lucas
 */
public class Autodocx {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Erro: é necessário informar o caminho do arquivo e pelo menos um parâmetro chave-valor.");
            System.out.println("Uso: java autodocx <caminho_do_arquivo> \"chave1:valor1\" [\"chave2:valor2\" ...]");
            
        }
        
        String caminhoArquivo = args[0];
         Map<String, String> dados = new HashMap<>();
        
        for (int i = 1; i < args.length; i++) {
            String[] par = args[i].split(":", 2);
            if (par.length == 2) {
                dados.put(par[0], par[1]);
            } else {
                System.out.println("Formato inválido para: " + args[i] + ". Use chave:valor.");
                
            }
        }
        
        
        substituirNoDocx(caminhoArquivo, dados);
        
        
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

                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    document.write(fos);
                }

                System.out.println("Substituição concluída com sucesso!");
            } catch (IOException e) {
                System.err.println("Erro ao processar o arquivo: " + e.getMessage());
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
