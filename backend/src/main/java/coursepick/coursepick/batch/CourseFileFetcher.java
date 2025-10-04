package coursepick.coursepick.batch;

import coursepick.coursepick.application.dto.CourseFile;

import java.io.IOException;
import java.util.List;

public interface CourseFileFetcher {

    /**
     * @return 다음 페이지의 코스 파일들, 다음 페이지가 없다면 빈 리스트를 응답한다.
     */
    List<CourseFile> fetchNextPage() throws IOException;
}
