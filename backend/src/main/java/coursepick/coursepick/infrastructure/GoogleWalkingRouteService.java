package coursepick.coursepick.infrastructure;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import coursepick.coursepick.application.WalkingRouteService;
import coursepick.coursepick.domain.Coordinate;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Profile({"dev", "prod"})
public class GoogleWalkingRouteService implements WalkingRouteService {

    private final GeoApiContext context;

    public GoogleWalkingRouteService(@Value("${gcp.credentials.geo-api-key}") String geoApiKey) {
        this.context = initGeoApiContext(geoApiKey);
    }

    private static GeoApiContext initGeoApiContext(String apiKey) {
        return new GeoApiContext.Builder()
                .apiKey(apiKey)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .retryTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @PreDestroy
    public void destroy() {
        context.shutdown();
    }

    @Override
    public List<Coordinate> route(Coordinate origin, Coordinate destination) {
        try {
            DirectionsResult result = DirectionsApi.newRequest(context)
                    .origin(new LatLng(origin.latitude(), origin.longitude()))
                    .destination(new LatLng(destination.latitude(), destination.longitude()))
                    .mode(TravelMode.WALKING)
                    .alternatives(false)
                    .language("ko")
                    .await();

            DirectionsRoute route = result.routes[0];
            List<LatLng> path = route.overviewPolyline.decodePath();
            return path.stream()
                    .map(latlng -> new Coordinate(latlng.lat, latlng.lng))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("길찾기에 실패했습니다.", e);
        }
    }
}
