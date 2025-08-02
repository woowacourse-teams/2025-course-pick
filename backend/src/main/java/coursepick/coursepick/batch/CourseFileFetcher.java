package coursepick.coursepick.batch;

import coursepick.coursepick.application.dto.CourseFile;

import java.util.List;

public interface CourseFileFetcher {

    List<CourseFile> fetchAll();
}
