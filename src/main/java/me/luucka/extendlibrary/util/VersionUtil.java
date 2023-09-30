package me.luucka.extendlibrary.util;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class VersionUtil {

    public static final ServerVersion v1_13_2_R01 = ServerVersion.fromString("1.13.2-R0.1-SNAPSHOT");
    public static final ServerVersion v1_14_4_R01 = ServerVersion.fromString("1.14.4-R0.1-SNAPSHOT");
    public static final ServerVersion v1_15_2_R01 = ServerVersion.fromString("1.15.2-R0.1-SNAPSHOT");
    public static final ServerVersion v1_16_5_R01 = ServerVersion.fromString("1.16.5-R0.1-SNAPSHOT");
    public static final ServerVersion v1_17_1_R01 = ServerVersion.fromString("1.17.1-R0.1-SNAPSHOT");
    public static final ServerVersion v1_18_2_R01 = ServerVersion.fromString("1.18.2-R0.1-SNAPSHOT");
    public static final ServerVersion v1_19_4_R01 = ServerVersion.fromString("1.19.4-R0.1-SNAPSHOT");
    public static final ServerVersion v1_20_2_R01 = ServerVersion.fromString("1.20.2-R0.1-SNAPSHOT");

    private static final Set<ServerVersion> allVersions = ImmutableSet.of(v1_13_2_R01, v1_14_4_R01, v1_15_2_R01, v1_16_5_R01, v1_17_1_R01, v1_18_2_R01, v1_19_4_R01, v1_20_2_R01);

    private static ServerVersion serverVersion = null;

    public static ServerVersion getServerVersion() {
        if (serverVersion == null) {
            serverVersion = ServerVersion.fromString(Bukkit.getServer().getBukkitVersion());
        }
        return serverVersion;
    }

    private static final Set<ServerVersion> supportedVersions = new HashSet<>();

    public static Set<ServerVersion> getSupportedVersions() {
        return VersionUtil.supportedVersions;
    }

    public static boolean isServerVersionSupported(ServerVersion minVersion) {
        VersionUtil.supportedVersions.clear();
        VersionUtil.supportedVersions.addAll(allVersions.stream()
                .filter(version -> version.isHigherThanOrEqualTo(minVersion))
                .collect(Collectors.toSet()));
        return VersionUtil.supportedVersions.contains(getServerVersion());
    }

    private VersionUtil() {
    }

    public static final class ServerVersion implements Comparable<ServerVersion> {

        private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)\\.?([0-9]*)?(?:-?R?([\\d.]+))?(?:-SNAPSHOT)?");

        private final int major;
        private final int minor;
        private final int patch;
        private final double revision;

        private ServerVersion(final int major, final int minor, final int patch, final double revision) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.revision = revision;
        }

        public static ServerVersion fromString(final String string) {
            Matcher matcher = VERSION_PATTERN.matcher(string);
            if (!matcher.matches()) {
                Bukkit.getLogger().log(Level.SEVERE, string + " is not in valid version format. e.g. 1.20.1-R0.1");
            }
            return from(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
        }

        private static ServerVersion from(final String major, final String minor, String patch, String revision) {
            if (patch == null || patch.isEmpty()) patch = "0";
            if (revision == null || revision.isEmpty()) revision = "0";
            return new ServerVersion(
                    Integer.parseInt(major),
                    Integer.parseInt(minor),
                    Integer.parseInt(patch),
                    Double.parseDouble(revision)
            );
        }


        public boolean isHigherThan(final ServerVersion o) {
            return compareTo(o) > 0;
        }

        public boolean isHigherThanOrEqualTo(final ServerVersion o) {
            return compareTo(o) >= 0;
        }

        public boolean isLowerThan(final ServerVersion o) {
            return compareTo(o) < 0;
        }

        public boolean isLowerThanOrEqualTo(final ServerVersion o) {
            return compareTo(o) <= 0;
        }

        public int getMajor() {
            return major;
        }

        public int getMinor() {
            return minor;
        }

        public int getPatch() {
            return patch;
        }

        public double getRevision() {
            return revision;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final ServerVersion that = (ServerVersion) o;
            return major == that.major &&
                    minor == that.minor &&
                    patch == that.patch &&
                    revision == that.revision;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(major, minor, patch, revision);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(major + "." + minor);
            if (patch != 0) {
                sb.append(".").append(patch);
            }
            return sb.toString();
        }

        @Override
        public int compareTo(final ServerVersion o) {
            if (major < o.major) {
                return -1;
            } else if (major > o.major) {
                return 1;
            } else { // equal major
                if (minor < o.minor) {
                    return -1;
                } else if (minor > o.minor) {
                    return 1;
                } else { // equal minor
                    if (patch < o.patch) {
                        return -1;
                    } else if (patch > o.patch) {
                        return 1;
                    } else { // equal patch
                        return Double.compare(revision, o.revision);
                    }
                }
            }
        }
    }

}
