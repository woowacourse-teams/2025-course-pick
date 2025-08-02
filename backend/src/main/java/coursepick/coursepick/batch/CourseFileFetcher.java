package coursepick.coursepick.batch;

import java.io.File;
import java.util.List;

public interface CourseFileFetcher {

    List<File> fetchAllGpxFiles();
}
