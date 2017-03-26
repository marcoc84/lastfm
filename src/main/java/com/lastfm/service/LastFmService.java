package com.lastfm.service;

public interface LastFmService {
    String getPopularArtists(String country, String page);

    String getTopTracks(String name);
}
