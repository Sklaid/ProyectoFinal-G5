package com.techcorp.devops.property;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

/**
 * Generator for creating invalid email addresses for property-based testing.
 * 
 * This generator creates various types of invalid email formats to test
 * email validation logic. It generates emails that violate common email
 * format rules such as:
 * - Missing @ symbol
 * - Missing domain
 * - Missing local part
 * - Contains spaces
 * - Empty strings
 * - Missing top-level domain
 */
public class InvalidEmailGenerator {
    
    /**
     * Generates invalid email addresses for testing validation logic.
     * 
     * This method returns an Arbitrary that produces various types of invalid emails:
     * - "notanemail" (no @ symbol)
     * - "@example.com" (missing local part)
     * - "user@" (missing domain)
     * - "user @example.com" (contains space)
     * - "user@.com" (missing domain name)
     * - "" (empty string)
     * - "user@@example.com" (double @)
     * - "user@domain" (missing TLD)
     * - ".user@example.com" (starts with dot)
     * - "user.@example.com" (ends with dot)
     * 
     * @return Arbitrary that produces invalid email strings
     */
    public static Arbitrary<String> invalidEmails() {
        return Arbitraries.of(
                "notanemail",
                "@example.com",
                "user@",
                "user @example.com",
                "user@.com",
                "",
                "user@@example.com",
                "user@domain",
                ".user@example.com",
                "user.@example.com",
                "user..name@example.com",
                "user@domain..com",
                "user name@example.com",
                "user@domain .com",
                "@",
                "@@",
                "user",
                "user@",
                "@domain.com",
                "user@domain@com",
                "user@domain,com",
                "user@domain com",
                "user @domain.com",
                " user@domain.com",
                "user@domain.com ",
                "user\t@domain.com",
                "user@domain\n.com"
        );
    }
    
    /**
     * Generates emails with missing @ symbol.
     * 
     * @return Arbitrary that produces emails without @ symbol
     */
    public static Arbitrary<String> emailsWithoutAtSymbol() {
        return Arbitraries.strings()
                .alpha()
                .numeric()
                .ofMinLength(5)
                .ofMaxLength(20)
                .filter(s -> !s.contains("@"));
    }
    
    /**
     * Generates emails with missing domain.
     * 
     * @return Arbitrary that produces emails ending with @
     */
    public static Arbitrary<String> emailsWithoutDomain() {
        return Arbitraries.strings()
                .alpha()
                .ofMinLength(3)
                .ofMaxLength(15)
                .map(s -> s + "@");
    }
    
    /**
     * Generates emails with missing local part.
     * 
     * @return Arbitrary that produces emails starting with @
     */
    public static Arbitrary<String> emailsWithoutLocalPart() {
        return Arbitraries.strings()
                .alpha()
                .ofMinLength(3)
                .ofMaxLength(15)
                .map(s -> "@" + s + ".com");
    }
    
    /**
     * Generates emails with spaces.
     * 
     * @return Arbitrary that produces emails containing spaces
     */
    public static Arbitrary<String> emailsWithSpaces() {
        return Arbitraries.strings()
                .alpha()
                .ofMinLength(3)
                .ofMaxLength(10)
                .map(s -> s + " @example.com");
    }
    
    /**
     * Generates empty or whitespace-only strings.
     * 
     * @return Arbitrary that produces empty or whitespace strings
     */
    public static Arbitrary<String> emptyOrWhitespace() {
        return Arbitraries.of(
                "",
                " ",
                "  ",
                "\t",
                "\n",
                "   ",
                "\t\t",
                " \t ",
                " \n "
        );
    }
    
    /**
     * Generates a mix of all invalid email types.
     * 
     * @return Arbitrary that produces various invalid email formats
     */
    public static Arbitrary<String> allInvalidEmails() {
        return Arbitraries.oneOf(
                invalidEmails(),
                emailsWithoutAtSymbol(),
                emailsWithoutDomain(),
                emailsWithoutLocalPart(),
                emailsWithSpaces(),
                emptyOrWhitespace()
        );
    }
}
