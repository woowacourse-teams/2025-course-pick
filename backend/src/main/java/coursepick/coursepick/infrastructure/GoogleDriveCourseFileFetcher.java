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
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static coursepick.coursepick.application.exception.ErrorType.GCP_DRIVE_FETCH_FAIL;
import static coursepick.coursepick.application.exception.ErrorType.GCP_DRIVE_FILE_NOT_EXIST;

@Component
@Profile({"dev"})
public class GoogleDriveCourseFileFetcher implements CourseFileFetcher {

    private static final String APPLICATION_NAME = "coursepick";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${gcp.credentials.path}")
    private Resource credentialsResource;

    @Value("${gcp.drive.folder-id}")
    private String folderId;

    @Override
    public List<CourseFile> fetchAll() {
        try {
            Drive service = getDriveService();
            List<File> files = listGpxMetaData(service);
            if (files.isEmpty()) {
                throw GCP_DRIVE_FILE_NOT_EXIST.create();
            }

            List<CourseFile> results = new ArrayList<>();

            for (File file : files) {
                results.add(new CourseFile(
                        file.getName(),
                        CourseFileExtension.GPX,
                        service.files().get(file.getId()).executeMediaAsInputStream()
                ));
            }

            return results;
        } catch (Exception e) {
            throw GCP_DRIVE_FETCH_FAIL.create(e.getMessage());
        }
    }

    private List<File> listGpxMetaData(Drive service) throws IOException {
        List<File> gpxFiles = new ArrayList<>();
        String query = String.format("'%s' in parents and name contains '.gpx' and trashed = false", folderId);
        String pageToken = null;

        do {
            FileList result = service.files().list()
                    .setQ(query)
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();

            gpxFiles.addAll(result.getFiles());
            pageToken = result.getNextPageToken();
        } while (pageToken != null);

        return gpxFiles;
    }

    private Drive getDriveService() throws GeneralSecurityException, IOException {
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
