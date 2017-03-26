package com.lastfm.service;

import com.lastfm.properties.LastFmProperties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LastFmServiceImplTest {

    @Mock
    private HttpClient client;

    @Mock
    private HttpResponse httpResponse;

    @Mock
    private HttpEntity entity;

    @Mock
    private LastFmProperties properties;

    private LastFmServiceImpl service;

    @Before
    public void setUp() {
        service = new LastFmServiceImpl(properties, client);
    }

    @Test
    public void getPopularArtistsShouldReturnAResponseIfAnOkLastFmResponse() throws IOException {
        when(properties.get("api.artists.url")).thenReturn("http://test.com/bla/bla/bla");
        when(properties.get("api.key")).thenReturn("KEY");
        when(client.execute(Mockito.isA(HttpRequestBase.class))).thenReturn(httpResponse);
        when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));
        when(httpResponse.getEntity()).thenReturn(entity);
        when(entity.getContent()).thenReturn(new ByteArrayInputStream( "OK".getBytes()));

        String result = service.getPopularArtists("Australia", "1");

        assertTrue("Return ok", result.equals("OK"));
    }

    @Test
    public void getPopularArtistsShouldReturnNullIfAnErrorFromLastFmResponse() throws IOException {
        when(properties.get("api.tracks.url")).thenReturn("http://test.com/bla/bla/bla");
        when(properties.get("api.key")).thenReturn("KEY");
        when(client.execute(Mockito.isA(HttpRequestBase.class))).thenReturn(null);

        String result = service.getTopTracks("Artist");

        assertTrue("Return null", result == null);
    }
}

