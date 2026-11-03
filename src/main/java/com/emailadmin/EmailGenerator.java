package com.emailadmin;

import java.text.Normalizer;
import java.util.Locale;

public final class EmailGenerator {
    private EmailGenerator() {}

    public static String generate(String employeeName, String department) {
        String name = normalize(employeeName).replaceAll("[^a-z0-9]+", ".");
        String dept = normalize(department).replaceAll("[^a-z0-9]+", "");
        name = name.replaceAll("^\\.|\\.$", "");
        if (name.isEmpty() || dept.isEmpty()) {
            throw new IllegalArgumentException("Name and department are required.");
        }
        return name + "." + dept + "@company.com";
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value.trim().toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}
