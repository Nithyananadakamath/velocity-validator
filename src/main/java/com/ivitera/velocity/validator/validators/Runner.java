package com.ivitera.velocity.validator.validators;


import com.ivitera.velocity.validator.exceptions.InitializationException;
import com.ivitera.velocity.validator.utils.PathSearcher;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.List;

public class Runner {

    private static final Logger log = Logger.getLogger(Runner.class);

    private File configFile;
    private File baseDir;
    private boolean verbose;

    public Runner(File configFile, File baseDir, boolean verbose) {
        this.configFile = configFile;
        this.baseDir = baseDir;
        this.verbose = verbose;
    }


    public boolean run() throws FileNotFoundException, InitializationException {

        if(verbose) {
            log.info("verbose mode is ON");
        }

        List<File> files = PathSearcher.getFileListing(baseDir, new FileFilter() {
            public boolean accept(File file) {
                return file.getAbsolutePath().endsWith(".vm");
            }
        });

        try {
            ValidatorsService.init(configFile);
        } catch (Exception e) {
            throw new InitializationException(e);
        }


        List<? extends Validator> validators = ValidatorsService.getAllValidators();
        int errors = 0;
        for (Validator validator : validators) {
            if (validator.isEnabled()) {
                for (File f : files) {
                    try {
                        validator.validate(f);
                        if (verbose) {
                            log.info("File OK: " + getFilePrintPath(f));
                        }
                    } catch (Exception e) {
                        errors++;
                        log.error("Error in file " + getFilePrintPath(f));
                        log.error("    " + e.getMessage().replace(baseDir.getAbsolutePath(), "./").replace("\n", "\n    "));
                    }
                }
            }
        }
        if (verbose) {
            log.info("Checked " + files.size() + " files");
            if (errors == 0) {
                log.info("No errors found in given path");
            }
        }
        if (errors > 0) {
            log.error("Done, Found " + errors + " errors");
            return false;
        }
        return true;
    }

    private String getFilePrintPath(File f) {
        String basePath = baseDir.getAbsolutePath();
        if(!basePath.endsWith("/")) {
            basePath = basePath + "/";
        }
        return f.getAbsolutePath().replace(basePath, "./");
    }
}
