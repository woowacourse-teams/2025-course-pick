package coursepick.coursepick.domain;

import coursepick.coursepick.application.dto.CourseInfo;

import java.io.InputStream;
import java.util.List;

public interface CourseParser {

    boolean canParse(String fileExtension);

    List<CourseInfo> parse(InputStream fileStream);
}
