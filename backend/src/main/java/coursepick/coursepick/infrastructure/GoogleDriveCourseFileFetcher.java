package coursepick.coursepick.infrastructure;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import coursepick.coursepick.application.dto.CourseFile;
import coursepick.coursepick.application.dto.CourseFileExtension;
import coursepick.coursepick.batch.CourseFileFetcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Component
@Profile({"dev", "prod"})
public class GoogleDriveCourseFileFetcher implements CourseFileFetcher {

    private static final String APPLICATION_NAME = "coursepick";
    private static final String QUERY_FORMAT = "'%s' in parents and name contains '.gpx' and trashed = false";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final String folderId;
    private final Drive drive;

    private String nextPageToken;
    private boolean isInitialRequest;

    public GoogleDriveCourseFileFetcher(
            @Value("${gcp.credentials.path}") Resource credentialsResource,
            @Value("${gcp.drive.folder-id}") String folderId
    ) {
        this.folderId = folderId;
        this.drive = initDrive(credentialsResource);
        this.nextPageToken = null;
        this.isInitialRequest = true;
    }

    @Override
    public List<CourseFile> fetchNextPage() throws IOException {
        List<File> files = listNextPageFiles();

        return files.parallelStream()
                .map(this::fetchDriveFileToCourseFile)
                .toList();
    }

    /**
     * 다음 페이지의 File 들을 조회한다.
     * 만약 다음 페이지가 없는 경우, 빈 리스트가 응답되며, 페이지 상태가 초기화된다.
     *
     * @return 조회한 File 리스트
     */
    private List<File> listNextPageFiles() throws IOException {
        if (isListingOver()) {
            isInitialRequest = true;
            return Collections.emptyList();
        }

        FileList result = getFileList(nextPageToken);

        isInitialRequest = false;
        nextPageToken = result.getNextPageToken();

        return result.getFiles();
    }

    private boolean isListingOver() {
        return !isInitialRequest && nextPageToken == null;
    }

    private FileList getFileList(String pageToken) throws IOException {
        String query = QUERY_FORMAT.formatted(folderId);
        return drive.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name)")
                .setPageToken(pageToken)
                .execute();
    }

    private CourseFile fetchDriveFileToCourseFile(File file) {
        try {
            return new CourseFile(
                    file.getName(),
                    CourseFileExtension.GPX,
                    drive.files().get(file.getId()).executeMediaAsInputStream()
            );
        } catch (IOException e) {
            throw new UncheckedIOException("파일 다운로드에 실패했습니다. 파일명=" + file.getName(), e);
        }
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private static Drive initDrive(Resource credentialsResource) {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsResource.getInputStream())
                    .createScoped(Collections.singleton(DriveScopes.DRIVE_READONLY));

            HttpRequestInitializer initializer = request -> {
                HttpCredentialsAdapter adapter = new HttpCredentialsAdapter(credentials);
                adapter.initialize(request);
                request.setConnectTimeout(3 * 60 * 1000);
                request.setReadTimeout(1 * 60 * 1000);
            };

            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            return new Drive.Builder(httpTransport, JSON_FACTORY, initializer)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("구글 드라이브 서비스 초기화에 실패했습니다.", e);
        }
    }
}
