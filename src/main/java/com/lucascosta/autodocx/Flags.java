/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lucascosta.autodocx;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;



/**
 *
 * @author lucas
 */
public class Flags {

    public static final Options options = new Options();

    static {
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Display this help menu")
                .build());
        
        options.addOption(Option.builder("t")
                .longOpt("template")
                .hasArg()
                .desc("Path to .docx template file")
                .required(true)
                .build());

        options.addOption(Option.builder("o")
                .longOpt("output")
                .hasArg()
                .desc("Output file name .docx")
                .build());
        
        options.addOption(Option.builder("r")
                .longOpt("replaces")
                .hasArgs()
                .desc("Defines the key:value pairs for replacement [\"key1:value1\", ...]")
                .build());
    }

    public static void verify(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                formatter.printHelp("AutoDocx", options);
            }

        } catch (ParseException e) {
            System.err.println("Error processing arguments: " + e.getMessage());
            formatter.printHelp("autodocx", options);
            System.exit(1);
        }
    }
    
}
