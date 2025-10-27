package coursepick.coursepick.test_util;

import coursepick.coursepick.domain.Coordinate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class GpxTestUtil {

    private static final String TRACK_POINT_FORMAT = """
            <trkpt lat="%s" lon="%s">
              <ele>%s</ele>
            </trkpt>
            """;

    private static final String GPX_FORMAT = """
            <?xml version="1.0" encoding="UTF-8"?>
            <gpx xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd" creator="StravaGPX" version="1.1" xmlns="http://www.topografix.com/GPX/1/1" xmlns:gpxtpx="http://www.garmin.com/xmlschemas/TrackPointExtension/v1" xmlns:gpxx="http://www.garmin.com/xmlschemas/GpxExtensions/v3">
             <trk>
              <name>test-course</name>
              <type>running</type>
                <trkseg>
                %s
                </trkseg>
              </trk>
            </gpx>
            """;

    private static final String GPX_WITH_ID_FORMAT = """
            <?xml version="1.0" encoding="UTF-8"?>
            <gpx xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd" creator="StravaGPX" version="1.1" xmlns="http://www.topografix.com/GPX/1/1" xmlns:gpxtpx="http://www.garmin.com/xmlschemas/TrackPointExtension/v1" xmlns:gpxx="http://www.garmin.com/xmlschemas/GpxExtensions/v3">
             <trk>
              <name>test-course</name>
              <type>running</type>
              <extensions>
                <id>%s</id>
              </extensions>
                <trkseg>
                %s
                </trkseg>
              </trk>
            </gpx>
            """;

    public static InputStream createGpxInputStreamOf(Coordinate... coordinates) {
        String gpx = createGpxOf(coordinates);
        return new ByteArrayInputStream(gpx.getBytes(StandardCharsets.UTF_8));
    }

    public static InputStream createGpxInputStreamOf(String id, Coordinate... coordinates) {
        String gpx = createGpxOf(id, coordinates);
        return new ByteArrayInputStream(gpx.getBytes(StandardCharsets.UTF_8));
    }

    public static String createGpxOf(Coordinate... coordinates) {
        StringBuilder trackPoints = new StringBuilder();
        for (Coordinate coordinate : coordinates) {
            String trackPoint = createTrackPointOf(coordinate);
            trackPoints.append(trackPoint).append("\n");
        }
        return GPX_FORMAT.formatted(trackPoints);
    }

    public static String createGpxOf(String id, Coordinate... coordinates) {
        StringBuilder trackPoints = new StringBuilder();
        for (Coordinate coordinate : coordinates) {
            String trackPoint = createTrackPointOf(coordinate);
            trackPoints.append(trackPoint).append("\n");
        }
        return GPX_WITH_ID_FORMAT.formatted(id, trackPoints);
    }

    private static String createTrackPointOf(Coordinate coordinate) {
        return TRACK_POINT_FORMAT.formatted(coordinate.latitude(), coordinate.longitude(), coordinate.elevation());
    }
}
