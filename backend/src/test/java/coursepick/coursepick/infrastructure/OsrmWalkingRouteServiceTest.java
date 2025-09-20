package coursepick.coursepick.infrastructure;

import coursepick.coursepick.domain.Coordinate;
import coursepick.coursepick.test_util.SimpleMockServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class OsrmWalkingRouteServiceTest {

    @Test
    void 두_좌표_사이의_걷기_경로를_조회할_수_있다() throws IOException {
        try (var mockServer = new SimpleMockServer(osrmResponse())) {
            var sut = new OsrmWalkingRouteService(mockServer.url());

            var result = sut.route(
                    new Coordinate(37.5045224, 127.048996),
                    new Coordinate(37.5113001, 127.0392855)
            );

            assertThat(result.size()).isEqualTo(12);
        }
    }

    private static String osrmResponse() {
        return """
                {
                  "code": "Ok",
                  "routes": [
                    {
                      "legs": [
                        {
                          "steps": [],
                          "weight": 115.8,
                          "summary": "",
                          "duration": 115.8,
                          "distance": 1408.3
                        }
                      ],
                      "weight_name": "routability",
                      "geometry": {
                        "coordinates": [
                          [
                            127.04901,
                            37.504526
                          ],
                          [
                            127.048307,
                            37.505993
                          ],
                          [
                            127.047943,
                            37.506427
                          ],
                          [
                            127.047499,
                            37.506771
                          ],
                          [
                            127.045223,
                            37.508171
                          ],
                          [
                            127.044611,
                            37.508705
                          ],
                          [
                            127.044339,
                            37.50913
                          ],
                          [
                            127.043868,
                            37.510277
                          ],
                          [
                            127.040105,
                            37.509187
                          ],
                          [
                            127.039294,
                            37.511302
                          ]
                        ],
                        "type": "LineString"
                      },
                      "weight": 115.8,
                      "duration": 115.8,
                      "distance": 1408.3
                    }
                  ],
                  "waypoints": [
                    {
                      "hint": "zZftgWbx24IJAAAACQAAAAAAAAAAAAAAhzBmQS_0RUEAAAAAAAAAAAkAAAAJAAAAAAAAAAAAAACnagAAMp2SBw5GPAIknZIHCkY8AgAATxUAAAAAOBNvif___38IAAAAEgAAAAAAAAAAAAAAhzBmQS_0RUEAAAAAAAAAAAgAAAASAAAAAAAAAAAAAACnagAAMp2SBw5GPAIknZIHCkY8AgAADwoAAAAA",
                      "location": [
                        127.04901,
                        37.504526
                      ],
                      "name": "선릉로 / 선릉로",
                      "distance": 1.314764322
                    },
                    {
                      "hint": "Zv03g6b9N4OmAAAABQAAAAAAAAAAAAAAlWbnQnjHTkAAAAAAAAAAAKYAAAAFAAAAAAAAAAAAAACnagAAPneSB4ZgPAI2d5IHhGA8AgAAfxUAAAAA",
                      "location": [
                        127.039294,
                        37.511302
                      ],
                      "name": "봉은사로43길",
                      "distance": 0.7411875462
                    }
                  ]
                }
                """;
    }
}
