package coursepick.coursepick.domain.course;

import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.domain.user.User;
import coursepick.coursepick.logging.LogContent;

import java.time.Instant;

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
    private final List<RawCoordinate> coordinates;

    private Gpx(String id, String name, List<RawCoordinate> coordinates) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
    }

    public static Gpx from(Course course) {
        List<RawCoordinate> coordinates = course.coordinates().stream()
                .map(coord -> new RawCoordinate(coord.latitude(), coord.longitude(), null))
                .toList();

        return new Gpx(course.id(), course.name().value(), coordinates);
    }

    public static Gpx from(CourseFile file) {
        try {
            XMLInputFactory xif = XMLInputFactory.newInstance();
            XMLStreamReader xsr = xif.createXMLStreamReader(file.inputStream());
            String id = null;
            Double lat = null, lon = null;
            Instant time = null;
            boolean hasExtensions = false;

            List<RawCoordinate> coordinates = new ArrayList<>();

            while (xsr.hasNext()) {
                int event = xsr.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String localName = xsr.getLocalName();
                    if ("extensions".equals(localName)) {
                        hasExtensions = true;
                    } else if ("trkpt".equals(localName)) {
                        lat = Double.parseDouble(xsr.getAttributeValue(null, "lat"));
                        lon = Double.parseDouble(xsr.getAttributeValue(null, "lon"));
                    } else if ("id".equals(localName) && hasExtensions) {
                        xsr.next();
                        if (xsr.getEventType() == XMLStreamConstants.CHARACTERS) {
                            id = xsr.getText();
                        }
                    } else if ("time".equals(localName)) {
                        time = Instant.parse(xsr.getElementText());
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    String localName = xsr.getLocalName();
                    if ("trkpt".equals(localName)) {
                        if (lat != null && lon != null && time != null) {
                            coordinates.add(new RawCoordinate(lat, lon, time));
                        } else if (lat != null && lon != null) {
                            coordinates.add(new RawCoordinate(lat, lon, null));
                        }

                        lat = lon = null;
                        time = null;
                    } else if ("extensions".equals(localName)) {
                        hasExtensions = false;
                    }
                }
            }

            return new Gpx(id, file.name(), coordinates);
        } catch (XMLStreamException e) {
            log.warn("[EXCEPTION] CourseFile -> Gpx 변환에 실패했습니다.", LogContent.exception(e));
            throw new IllegalArgumentException(e);
        }
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
        return List.of(new Course(id, coordinates, new CourseName(name), user));
    }
}
