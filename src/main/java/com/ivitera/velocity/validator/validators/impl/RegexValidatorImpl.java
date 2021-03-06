package com.ivitera.velocity.validator.validators.impl;

import com.ivitera.velocity.validator.exceptions.InitializationException;
import com.ivitera.velocity.validator.exceptions.ValidationException;
import com.ivitera.velocity.validator.utils.StringUtils;
import com.ivitera.velocity.validator.validators.Validator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidatorImpl implements Validator {

    private boolean enabled = false;

    private static List<Pattern> patterns = new ArrayList<>();
    private static final String LINES_DELIMITER = "\n";

    public void validate(File file) throws ValidationException, IOException {

        if(!enabled) {
            return;
        }

        List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
        for (Pattern pattern : patterns) {
            String text = StringUtils.join(lines, LINES_DELIMITER);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String found = matcher.group(0);
                int startAt = text.indexOf(found);
                int line = text.substring(0, startAt).split(LINES_DELIMITER).length;
                String[] parts = found.split(LINES_DELIMITER);
                int column = lines.get(line - 1).indexOf(parts[0]) + 1;
                throw createException(file.getAbsolutePath(), pattern, found, line, column);
            }
        }
    }

    public void init(File config) throws InitializationException {

        if (config == null) {
            enabled = false;
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(config.getAbsolutePath()), StandardCharsets.UTF_8);
            patterns.clear();
            enabled = false;
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    patterns.add(Pattern.compile(trimmedLine));
                }
            }
            if (!patterns.isEmpty()) {
                enabled = true;
            }
        } catch (Exception e) {
            throw new InitializationException(e);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    private ValidationException createException(String file, Pattern pattern, String found,
                                                int line, int column) throws ValidationException {
        return new ValidationException(
                "Encountered \"" + pattern.toString() + "\" at " + file + "[line " + line
                        + ", column " + column + "]" + LINES_DELIMITER + "    " + found);
    }
}
