package com.hcq.docconverter.cli;

import com.hcq.docconverter.DocumentConverter;
import com.hcq.docconverter.openoffice.connection.OpenOfficeConnection;
import com.hcq.docconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.hcq.docconverter.openoffice.converter.OpenOfficeDocumentConverter;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.ConnectException;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class ConvertDocument {

    private static final Option OPTION_OUTPUT_FORMAT = new Option(
            "f", "output-format", true, "output format (e.g. pdf)");
    private static final Option OPTION_PORT = new Option(
            "p", "port", true, "OpenOffice.org port");
    private static final Option OPTION_VERBOSE = new Option(
            "v", "verbose", false, "verbose");
    private static final Options OPTIONS = initOptions();
    private static final int EXIT_CODE_CONNECTION_FAILED = 1;
    private static final int EXIT_CODE_TOO_FEW_ARGS = 255;

    private static Options initOptions() {
        Options options = new Options();
        options.addOption(OPTION_OUTPUT_FORMAT);
        options.addOption(OPTION_PORT);
        options.addOption(OPTION_VERBOSE);
        return options;
    }

    public static void main(String[] arguments) throws Exception {
        CommandLineParser commandLineParser = new PosixParser();
        CommandLine commandLine = commandLineParser.parse(OPTIONS, arguments);

        int port = 8100;
        if (commandLine.hasOption(OPTION_PORT.getOpt())) {
            port = Integer.parseInt(commandLine.getOptionValue(OPTION_PORT.getOpt()));
        }

        String outputFormat = null;
        if (commandLine.hasOption(OPTION_OUTPUT_FORMAT.getOpt())) {
            outputFormat = commandLine.getOptionValue(OPTION_OUTPUT_FORMAT.getOpt());
        }

        boolean verbose = false;
        if (commandLine.hasOption(OPTION_VERBOSE.getOpt())) {
            verbose = true;
        }

        String[] fileNames = commandLine.getArgs();
        if (((outputFormat == null) && (fileNames.length != 2)) || (fileNames.length < 1)) {
            String syntax = "convert [options] input-file output-file; or\n[options] -f output-format input-file [input-file...]";

            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp(syntax, OPTIONS);
            System.exit(255);
        }

        OpenOfficeConnection connection = new SocketOpenOfficeConnection(port);
        try {
            if (verbose) {
                System.out.println("-- connecting to OpenOffice.org on port " + port);
            }
            connection.connect();
        } catch (ConnectException officeNotRunning) {
            System.err
                    .println("ERROR: connection failed. Please make sure OpenOffice.org is running and listening on port " +
                            port + ".");
            System.exit(1);
        }
        try {
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            if (outputFormat == null) {
                File inputFile = new File(fileNames[0]);
                File outputFile = new File(fileNames[1]);
                convertOne(converter, inputFile, outputFile, verbose);
            } else {
                for (int i = 0; i < fileNames.length; i++) {
                    File inputFile = new File(fileNames[i]);
                    File outputFile = new File(FilenameUtils.getFullPath(fileNames[i]) +
                            FilenameUtils.getBaseName(fileNames[i]) + "." + outputFormat);
                    convertOne(converter, inputFile, outputFile, verbose);
                }
            }
        } finally {
            if (verbose) {
                System.out.println("-- disconnecting");
            }
            connection.disconnect();
        }
    }

    private static void convertOne(DocumentConverter converter, File inputFile, File outputFile, boolean verbose) {
        if (verbose) {
            System.out.println("-- converting " + inputFile + " to " + outputFile);
        }
        converter.convert(inputFile, outputFile);
    }
}
