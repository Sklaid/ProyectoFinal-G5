package com.techcorp.devops.property;

import net.jqwik.spring.JqwikSpringSupport;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for property-based tests using jqwik.
 * 
 * This class provides common configuration for all property-based tests:
 * - Spring Boot test context
 * - Test profile activation
 * - jqwik Spring support
 * 
 * All property tests should extend this class to ensure consistent configuration.
 * 
 * Property tests are configured to run a minimum of 100 iterations (trials) by default.
 * This can be overridden on individual @Property annotations using the tries parameter.
 * 
 * Example usage:
 * <pre>
 * public class MyPropertyTest extends BasePropertyTest {
 *     
 *     &#64;Property(tries = 100)
 *     void myProperty(@ForAll("validInputs") MyInput input) {
 *         // Test implementation
 *     }
 * }
 * </pre>
 */
@SpringBootTest
@ActiveProfiles("test")
@JqwikSpringSupport
public abstract class BasePropertyTest {
    
    /**
     * Default number of trials for property-based tests.
     * Individual tests can override this by specifying tries in @Property annotation.
     */
    protected static final int DEFAULT_TRIES = 100;
}
