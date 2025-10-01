package coursepick.coursepick.domain;

import coursepick.coursepick.application.dto.CourseFile;
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
    private final String name;
    private final List<Coordinate> coordinates;

    private Gpx(String name, List<Coordinate> coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    public static Gpx from(Course course) {
        List<Coordinate> coordinates = course.segments().stream()
                .flatMap(segment -> segment.coordinates().stream())
                .toList();

        return new Gpx(course.name().value(), coordinates);
    }

    public static Gpx from(CourseFile file) {
        try {
            XMLInputFactory xif = XMLInputFactory.newInstance();
            XMLStreamReader xsr = xif.createXMLStreamReader(file.inputStream());
            Double lat = null, lon = null, ele = null;

            List<Coordinate> coordinates = new ArrayList<>();

            while (xsr.hasNext()) {
                int event = xsr.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String localName = xsr.getLocalName();
                    if ("trkpt".equals(localName)) {
                        lat = Double.parseDouble(xsr.getAttributeValue(null, "lat"));
                        lon = Double.parseDouble(xsr.getAttributeValue(null, "lon"));
                    } else if ("ele".equals(localName)) {
                        xsr.next();
                        if (xsr.getEventType() == XMLStreamConstants.CHARACTERS) {
                            ele = Double.parseDouble(xsr.getText());
                        }
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    String localName = xsr.getLocalName();
                    if ("trkpt".equals(localName)) {
                        if (lat != null && lon != null) {
                            coordinates.add(new Coordinate(lat, lon, ele));
                        }
                        lat = lon = ele = null;
                    }
                }
            }

            return new Gpx(file.name(), coordinates);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
                xsw.writeStartElement("ele");
                xsw.writeCharacters(decimalFormat.format(p.elevation()));
                xsw.writeEndElement();
                xsw.writeEndElement();
            }
        }
        xsw.writeEndElement();
        xsw.writeEndElement();
    }

    public List<Course> toCourses() {
        return List.of(new Course(name, coordinates));
    }
}
