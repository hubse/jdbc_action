package com.jdbc.crud;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomLogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return new Date(record.getMillis()) + " | " +
               record.getLevel() + " | " +
               record.getSourceClassName() + " | " +
               record.getMessage() + "\n";
    }
}
