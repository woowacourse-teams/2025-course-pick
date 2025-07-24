package coursepick.coursepick.domain;

import java.io.InputStream;
import java.util.List;

public interface CourseParser {

    boolean canParse(String fileExtension);
    
    List<Course> parse(InputStream fileStream);
}
