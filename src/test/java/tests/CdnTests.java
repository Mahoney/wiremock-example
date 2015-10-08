package tests;

import client.HttpClient;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import jodd.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CdnTests {

    String appName = "testwm1";
    
    WireMock origin = new WireMock(appName+".herokuapp.com", 80);
    
    HttpClient cdn = new HttpClient("https://"+appName+".global.ssl.fastly.net/");
    
    String path = "/"+RandomStringUtils.randomAlphanumeric(5);

    @Test
    public void cdnCachesContent() {

        // given
        origin.register(
            get(urlEqualTo(path))
            .willReturn(aResponse()
                .withHeader("Cache-Control", "max-age=30")
                .withBody("Old Body")
            )
        );

        // and
        cdn.get(path);

        // when
        origin.register(
            get(urlEqualTo(path))
            .willReturn(aResponse()
                .withBody("New Body")
            )
        );

        // then
        assertThat(cdn.get(path).body(), is("Old Body"));

    }

    @Test
    public void cannotPurgeContent() {

        // given
        origin.register(
            get(urlEqualTo(path))
            .willReturn(aResponse()
                .withHeader("Cache-Control", "max-age=30")
                .withBody("Old Body")
            )
        );

        // and
        cdn.get(path);

        // and
        origin.register(
            get(urlEqualTo(path))
            .willReturn(aResponse()
                .withBody("New Body")
            )
        );

        // when
        HttpResponse purgeResponse = cdn.execute("PURGE", path);

        // then
        assertThat(cdn.get(path).body(), is("Old Body"));
        assertThat(purgeResponse.statusCode(), is(401));

    }

    @Test
    public void canPurgeContentIfAuthorised() {

        // given
        origin.register(
            get(urlEqualTo(path))
            .willReturn(aResponse()
                .withHeader("Cache-Control", "max-age=30")
                .withBody("Old Body")
            )
        );

        // and
        cdn.get(path);

        // and
        origin.register(
            get(urlEqualTo(path))
            .willReturn(aResponse()
                .withBody("New Body")
            )
        );

        // when
        cdn.execute(
            "PURGE",
            of("Authorization", "Bearer very_secret"),
            path
        );

        // then
        assertThat(cdn.get(path).body(), is("New Body"));

    }

}
