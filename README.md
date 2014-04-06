Java tool for validating apache velocity templates
====================
[![Build Status](https://travis-ci.org/todvora/velocity-validator.svg?branch=master)](https://travis-ci.org/todvora/velocity-validator)

Purpose
---------------------
Simple validation tool for detecting bugs in Apache Velocity templates. It iterates through directories and validates every .vm file. Errors are printed with filename, error and possible solutions.

Usage
---------------------

    java -jar velocityvalidator.jar path_to_templates [-rules=path_to_config_file] [-verbose]


* `path_to_tempates` = root directory, where starts detection of templates, it will crawl recursive thru whole subtree of directories
* `path_to_config_file` = not mandatory, file, on every line new java regular expression of illegal input.  For example `#foreach(.* as .*)` . Correct one is `#foreach(.* in .*)`

Examples of incorrect templates
---------------------

###IncorrectVariableName.vm


    ###UTF-8
    
    ## illegal exclamation mark at end of variable
    ${variable!}
    

###TwoEndBlocksTemplate.vm

    ###UTF-8
    
    #if($variable)
    #end

    ## one more end, with no opening tag
    #end

###UnfinishedBlockTemplate.vm

    ###UTF-8
    
    #if($variable)

        $variable
    ## commented out end
    ## #end


Validation output
---------------------

Run command in directory with templates mentioned above:
    
    java -jar bin/velocityvalidator-0.1.1.jar .

And output should look like:

    Error in file /mnt/raid/home/dvorak/examples/./IncorrectVariableName.vm
        org.apache.velocity.runtime.parser.TemplateParseException: Encountered "!}\n" at /mnt/raid/home/dvorak/examples/./IncorrectVariableName.vm[line 3, column 11]
        Was expecting one of:
            "}" ...
            <DOT> ...
            
    Error in file /mnt/raid/home/dvorak/examples/./TwoEndBlocksTemplate.vm
        org.apache.velocity.runtime.parser.TemplateParseException: Encountered "#end\n" at /mnt/raid/home/dvorak/examples/./TwoEndBlocksTemplate.vm[line 5, column 1]
        Was expecting one of:
            <EOF> 
            "(" ...
            <RPAREN> ...
            <ESCAPE_DIRECTIVE> ...
            <SET_DIRECTIVE> ...
            "##" ...
            "\\\\" ...
            "\\" ...
            <TEXT> ...
            "*#" ...
            "*#" ...
            <STRING_LITERAL> ...
            <IF_DIRECTIVE> ...
            <STOP_DIRECTIVE> ...
            <INTEGER_LITERAL> ...
            <FLOATING_POINT_LITERAL> ...
            <WORD> ...
            <BRACKETED_WORD> ...
            <IDENTIFIER> ...
            <DOT> ...
            "{" ...
            "}" ...
            
    Error in file /mnt/raid/home/dvorak/examples/./UnfinishedBlockTemplate.vm
        org.apache.velocity.runtime.parser.TemplateParseException: Encountered "<EOF>" at /mnt/raid/home/dvorak/examples/./UnfinishedBlockTemplate.vm[line 7, column 8]
        Was expecting one of:
            "(" ...
            <RPAREN> ...
            <ESCAPE_DIRECTIVE> ...
            <SET_DIRECTIVE> ...
            "##" ...
            "\\\\" ...
            "\\" ...
            <TEXT> ...
            "*#" ...
            "*#" ...
            <STRING_LITERAL> ...
            <END> ...
            <IF_DIRECTIVE> ...
            <ELSEIF_DIRECTIVE> ...
            <ELSE_DIRECTIVE> ...
            <STOP_DIRECTIVE> ...
            <INTEGER_LITERAL> ...
            <FLOATING_POINT_LITERAL> ...
            <WORD> ...
            <BRACKETED_WORD> ...
            <IDENTIFIER> ...
            <DOT> ...
            "{" ...
            "}" ...
            
    Done, Found 3 errors

How to build project
--------------------
This project is based on [Maven](https://maven.apache.org/). You will need to have installed JDK and Maven.
Then simply run
```mvn package``` and in project directory ```target/dist``` will be created jar with validator.
Directory ```target/dist/lib``` contains all project dependencies.