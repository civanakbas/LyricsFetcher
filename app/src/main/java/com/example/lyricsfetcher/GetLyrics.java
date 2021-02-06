package com.example.lyricsfetcher;



import android.util.Log;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetLyrics {
    public String GetLyrics(String trackName, String artistName) {
        String lyrics ="";
        try {
            //Getting the url
            String apiKey = "42c468ce0ca60329fe7044bd5c980eda";
            MusixMatch musixMatch = new MusixMatch(apiKey);
            Track track;
            track = musixMatch.getMatchingTrack(trackName,artistName);
            TrackData data = track.getTrack();
            String url = data.getTrackShareUrl();
            url = url.replace("http://", "https://");
            Log.d("url",url);
            //Scraping the url
            Document doc = Jsoup.connect(url).get();
            doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
            Elements first = doc.select("span.lyrics__content__ok");
            if(first.isEmpty())
                first = doc.select("span.lyrics__content__error");
            if(first.isEmpty()){
                first = doc.select("span.lyrics__content__warning");
            }
            lyrics = first.toString();
            lyrics = lyrics.replace("<span class=\"lyrics__content__error\">", "");
            lyrics = lyrics.replace("<span class=\"lyrics__content__ok\">", "");
            lyrics = lyrics.replace("<span class=\"lyrics__content__warning\">", "");
            lyrics = lyrics.replace("</span>", "");
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (MusixMatchException e1){
            e1.printStackTrace();
        }
        return lyrics;

    }
}