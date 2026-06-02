package coursepick.coursepick.domain.course;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.logging.LogContent;
import lombok.extern.slf4j.Slf4j;

import javax.xml.stream.*;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
@Slf4j
public class Gpx {

    private static final String CREATOR = "Coursepick - https://github.com/woowacourse-teams/2025-course-pick";
    private final String id;
    private final String name;
    private final List<Coordinate> coordinates;

    private Gpx(String id, String name, List<Coordinate> coordinates) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
    }

    public static Gpx from(Course course) {
        List<Coordinate> coordinates = course.coordinates();

        return new Gpx(course.id(), course.name().value(), coordinates);
    }

    public record GpxParseResult(
            List<Gpx> gpxList,
            List<String> skippedReasons
    ) {
    }

    public static GpxParseResult from(CourseFile file) {
        try {
            XMLInputFactory xif = XMLInputFactory.newInstance();
            XMLStreamReader xsr = xif.createXMLStreamReader(file.inputStream());
            
            List<Gpx> gpxList = new ArrayList<>();
            List<String> skippedReasons = new ArrayList<>();
            String globalName = null;
            String currentId = null;
            String currentName = null;
            List<Coordinate> currentCoordinates = new ArrayList<>();
            boolean hasExtensions = false;
            boolean inTrack = false;
            boolean inMetadata = false;

            int trackIndex = 0;
            while (xsr.hasNext()) {
                int event = xsr.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String localName = xsr.getLocalName();
                    if ("metadata".equals(localName)) {
                        inMetadata = true;
                    } else if ("trk".equals(localName)) {
                        inTrack = true;
                        trackIndex++;
                        currentId = null;
                        currentName = null;
                        currentCoordinates = new ArrayList<>();
                    } else if ("name".equals(localName)) {
                        xsr.next();
                        if (xsr.getEventType() == XMLStreamConstants.CHARACTERS) {
                            String nameValue = xsr.getText().trim();
                            if (!nameValue.isEmpty()) {
                                if (inTrack) {
                                    currentName = nameValue;
                                } else if (inMetadata || globalName == null) {
                                    globalName = nameValue;
                                }
                            }
                        }
                    } else if ("extensions".equals(localName)) {
                        hasExtensions = true;
                    } else if ("trkpt".equals(localName)) {
                        double lat = Double.parseDouble(xsr.getAttributeValue(null, "lat"));
                        double lon = Double.parseDouble(xsr.getAttributeValue(null, "lon"));
                        currentCoordinates.add(new Coordinate(lat, lon));
                    } else if ("id".equals(localName) && hasExtensions) {
                        xsr.next();
                        if (xsr.getEventType() == XMLStreamConstants.CHARACTERS) {
                            currentId = xsr.getText();
                        }
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    String localName = xsr.getLocalName();
                    if ("trk".equals(localName)) {
                        String finalName = extractFinalName(currentName);
                        if (finalName != null && !currentCoordinates.isEmpty()) {
                            gpxList.add(new Gpx(currentId, finalName, currentCoordinates));
                        } else {
                            String reason = String.format("%d번째 트랙: %s", 
                                    trackIndex, (finalName == null) ? "이름 누락" : "좌표 부족");
                            skippedReasons.add(reason);
                            log.warn("GPX 파일에서 트랙 정보를 건너뜁니다. 파일명={}, 사유={}", file.name(), reason);
                        }
                        inTrack = false;
                    } else if ("metadata".equals(localName)) {
                        inMetadata = false;
                    } else if ("extensions".equals(localName)) {
                        hasExtensions = false;
                    }
                }
            }

            return new GpxParseResult(gpxList, skippedReasons);
        } catch (XMLStreamException e) {
            log.warn("[EXCEPTION] CourseFile -> Gpx 변환에 실패했습니다.", LogContent.exception(e));
            throw new IllegalArgumentException(e);
        }
    }

    private static String extractFinalName(String trackName) {
        if (trackName == null || trackName.isBlank()) {
            return null;
        }
        return trackName;
    }

    public String toXmlContent() {
        StringWriter sw = new StringWriter();
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        try {
            XMLStreamWriter xsw = xof.createXMLStreamWriter(sw);

            xsw.writeStartDocument("UTF-8", "1.0");
            xsw.writeStartElement("gpx");
            writeRootAttributes(xsw);
            writeTrk(xsw);
            xsw.writeEndElement();
            xsw.writeEndDocument();
            xsw.flush();
            xsw.close();
            return sw.toString();
        } catch (XMLStreamException e) {
            log.warn("[EXCEPTION] Gpx -> Xml 변환에 실패했습니다.", LogContent.exception(e));
            throw new IllegalStateException(e);
        }
    }

    private void writeRootAttributes(XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeDefaultNamespace("http://www.topografix.com/GPX/1/1");
        xsw.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xsw.writeAttribute("creator", CREATOR);
        xsw.writeAttribute("version", "1.1");
        xsw.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation",
                "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
    }

    private void writeTrk(XMLStreamWriter xsw) throws XMLStreamException {
        DecimalFormat decimalFormat = new DecimalFormat("0.#######");

        xsw.writeStartElement("trk");
        xsw.writeStartElement("name");
        xsw.writeCharacters(name);
        xsw.writeEndElement();
        xsw.writeStartElement("trkseg");
        if (coordinates != null) {
            for (var p : coordinates) {
                xsw.writeStartElement("trkpt");
                xsw.writeAttribute("lat", decimalFormat.format(p.latitude()));
                xsw.writeAttribute("lon", decimalFormat.format(p.longitude()));
                xsw.writeEndElement();
            }
        }
        xsw.writeEndElement();
        xsw.writeEndElement();
    }

    public List<Course> toCourses(User user) {
        return List.of(new Course(id, new CourseName(name), coordinates, user));
    }
}
