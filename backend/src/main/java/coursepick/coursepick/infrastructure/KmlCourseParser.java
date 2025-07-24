package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.domain.Course;
import coursepick.coursepick.domain.CourseParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class KmlCourseParser implements CourseParser {

    @Override
    public boolean canParse(String fileExtension) {
        return fileExtension.equals("kml");
    }

    @Override
    public List<Course> parse(InputStream fileStream) {
        List<Course> courses = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(fileStream);

            NodeList placemarks = document.getElementsByTagName("Placemark");

            for (int i = 0; i < placemarks.getLength(); i++) {
                Node placemark = placemarks.item(i);
                if (placemark.getNodeType() == Node.ELEMENT_NODE) {
                    Element placemarkElement = (Element) placemark;
                    Course course = parseCourse(placemarkElement);
                    if (course != null) {
                        courses.add(course);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("KML 파일 파싱 중 오류가 발생했습니다", e);
        }

        return courses;
    }

    private Course parseCourse(Element placemark) {
        String courseName = parseCourseName(placemark);
        List<Coordinate> coordinates = parseCoordinates(placemark);

        if (courseName == null || courseName.isBlank()) return null;
        if (coordinates.isEmpty()) return null;

        return new Course(courseName, coordinates);
    }

    private String parseCourseName(Element placemark) {
        NodeList nodeList = placemark.getElementsByTagName("name");
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }

    private List<Coordinate> parseCoordinates(Element placemark) {
        List<Coordinate> coordinates = new ArrayList<>();

        NodeList coordinatesList = placemark.getElementsByTagName("coordinates");
        if (coordinatesList.getLength() == 0) {
            return coordinates;
        }

        String coordinatesText = coordinatesList.item(0).getTextContent().trim();
        String[] coordinatePairs = coordinatesText.split("\\s+");

        for (String pair : coordinatePairs) {
            if (pair.trim().isEmpty()) {
                continue;
            }

            String[] values = pair.split(",");
            if (values.length >= 2) {
                try {
                    double longitude = Double.parseDouble(values[0]);
                    double latitude = Double.parseDouble(values[1]);
                    coordinates.add(new Coordinate(latitude, longitude, 0));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return coordinates;
    }
}
