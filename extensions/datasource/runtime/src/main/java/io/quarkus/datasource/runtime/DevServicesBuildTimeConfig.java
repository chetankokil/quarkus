package io.quarkus.datasource.runtime;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import io.quarkus.runtime.annotations.ConfigGroup;

@ConfigGroup
public interface DevServicesBuildTimeConfig {

    /**
     * If DevServices has been explicitly enabled or disabled.
     * DevServices is generally enabled by default unless an existing configuration is present.
     *
     * When DevServices is enabled, Quarkus will attempt to automatically configure and start a database when running in Dev or
     * Test mode.
     */
    Optional<Boolean> enabled();

    /**
     * The container image name for container-based DevServices providers.
     *
     * This has no effect if the provider is not a container-based database, such as H2 or Derby.
     */
    Optional<String> imageName();

    /**
     * Environment variables that are passed to the container.
     */
    Map<String, String> containerEnv();

    /**
     * Generic properties that are passed for additional container configuration.
     * <p>
     * Properties defined here are database-specific
     * and are interpreted specifically in each database dev service implementation.
     */
    Map<String, String> containerProperties();

    /**
     * Generic properties that are added to the database connection URL.
     */
    Map<String, String> properties();

    /**
     * Optional fixed port the dev service will listen to.
     * <p>
     * If not defined, the port will be chosen randomly.
     */
    OptionalInt port();

    /**
     * The container start command to use for container-based DevServices providers.
     *
     * This has no effect if the provider is not a container-based database, such as H2 or Derby.
     */
    Optional<String> command();

    /**
     * The database name to use if this Dev Service supports overriding it.
     */
    Optional<String> dbName();

    /**
     * The username to use if this Dev Service supports overriding it.
     */
    Optional<String> username();

    /**
     * The password to use if this Dev Service supports overriding it.
     */
    Optional<String> password();

    /**
     * The path to a SQL script to be loaded from the classpath and applied to the Dev Service database.
     *
     * This has no effect if the provider is not a container-based database, such as H2 or Derby.
     */
    Optional<String> initScriptPath();

    /**
     * The volumes to be mapped to the container.
     * The map key corresponds to the host location; the map value is the container location.
     * If the host location starts with "classpath:",
     * the mapping loads the resource from the classpath with read-only permission.
     *
     * When using a file system location, the volume will be generated with read-write permission,
     * potentially leading to data loss or modification in your file system.
     *
     * This has no effect if the provider is not a container-based database, such as H2 or Derby.
     */
    Map<String, String> volumes();
}
