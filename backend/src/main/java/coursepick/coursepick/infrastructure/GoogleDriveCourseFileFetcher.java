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
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Profile({"dev"})
public class GoogleDriveCourseFileFetcher implements CourseFileFetcher {

    private static final String APPLICATION_NAME = "coursepick";
    private static final String QUERY_FORMAT = "'%s' in parents and name contains '.gpx' and trashed = false";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${gcp.credentials.path}")
    private Resource credentialsResource;

    @Value("${gcp.drive.folder-id}")
    private String folderId;

    private Drive drive;

    @PostConstruct
    public void init() {
        try {
            this.drive = getDrive();
        } catch (GeneralSecurityException | IOException e) {
            throw new IllegalStateException("구글 드라이브 서비스 초기화에 실패했습니다.", e);
        }
    }

    @Override
    public List<CourseFile> fetchAll() {
        List<File> files = listGpxMetaData(drive);

        if (files.isEmpty()) {
            throw new IllegalStateException("구글 드라이브에 파일이 존재하지 않습니다.");
        }

        return files.parallelStream()
                .map(this::fetchDriveFileToCourseFile)
                .toList();
    }

    private List<File> listGpxMetaData(Drive service) {
        List<File> gpxFiles = new ArrayList<>();
        String query = QUERY_FORMAT.formatted(folderId);
        String pageToken = null;

        do {
            FileList result;
            try {
                result = service.files().list()
                        .setQ(query)
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken)
                        .execute();
            } catch (IOException e) {
                throw new RuntimeException("파일 메타데이터 가져오기에 실패했씁니다.", e);
            }

            gpxFiles.addAll(result.getFiles());
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return gpxFiles;
    }

    private CourseFile fetchDriveFileToCourseFile(File file) {
        try {
            return new CourseFile(
                    file.getName(),
                    CourseFileExtension.GPX,
                    drive.files().get(file.getId()).executeMediaAsInputStream()
            );
        } catch (IOException e) {
            throw new RuntimeException("파일 다운로드에 실패했습니다. 파일명=" + file.getName(), e);
        }
    }

    private Drive getDrive() throws GeneralSecurityException, IOException {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(httpTransport, JSON_FACTORY, new HttpCredentialsAdapter(getCredentials()))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private GoogleCredentials getCredentials() throws IOException {
        return GoogleCredentials.fromStream(credentialsResource.getInputStream())
                .createScoped(Collections.singleton(DriveScopes.DRIVE_READONLY));
    }
}
