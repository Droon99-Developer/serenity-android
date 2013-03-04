/**
 * The MIT License (MIT)
 * Copyright (c) 2012 David Carver
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package us.nineworlds.serenity.core.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import us.nineworlds.plex.rest.model.impl.Director;
import us.nineworlds.plex.rest.model.impl.Genre;
import us.nineworlds.plex.rest.model.impl.Media;
import us.nineworlds.plex.rest.model.impl.MediaContainer;
import us.nineworlds.plex.rest.model.impl.Part;
import us.nineworlds.plex.rest.model.impl.Video;
import us.nineworlds.plex.rest.model.impl.Writer;
import us.nineworlds.serenity.SerenityApplication;
import us.nineworlds.serenity.core.model.VideoContentInfo;
import us.nineworlds.serenity.core.model.impl.MoviePosterInfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author dcarver
 * 
 */
public class MoviesRetrievalIntentService extends AbstractPlexRESTIntentService {

	protected List<VideoContentInfo> posterList = null;
	protected String key;
	protected String category;

	public MoviesRetrievalIntentService() {
		super("MoviesRetrievalIntentService");
		posterList = new ArrayList<VideoContentInfo>();

	}

	@Override
	public void sendMessageResults(Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Messenger messenger = (Messenger) extras.get("MESSENGER");
			Message msg = Message.obtain();
			msg.obj = posterList;
			try {
				messenger.send(msg);
			} catch (RemoteException ex) {
				Log.e(getClass().getName(), "Unable to send message", ex);
			}
		}
		

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		key = intent.getExtras().getString("key", "");
		category = intent.getExtras().getString("category", "all");
		createPosters();
		sendMessageResults(intent);
	}

	protected void createPosters() {
		MediaContainer mc = null;
		try {
			factory = SerenityApplication.getPlexFactory();
			mc = retrieveVideos();
		} catch (IOException ex) {
			Log.e("AbstractPosterImageGalleryAdapter",
					"Unable to talk to server: ", ex);
		} catch (Exception e) {
			Log.e("AbstractPosterImageGalleryAdapter", "Oops.", e);
		}

		if (mc != null && mc.getSize() > 0) {
			createVideoContent(mc);
		}

	}

	protected void createVideoContent(MediaContainer mc) {
		String baseUrl = factory.baseURL();
		List<Video> videos = mc.getVideos();
		for (Video movie : videos) {
			VideoContentInfo mpi = new MoviePosterInfo();
			mpi.setId(movie.getRatingKey());
			mpi.setPlotSummary(movie.getSummary());
			
			mpi.setResumeOffset(Long.valueOf(movie.getViewOffset()).intValue());
			
			mpi.setViewCount(movie.getViewCount());

			String burl = baseUrl + ":/resources/movie-fanart.jpg";
			if (movie.getBackgroundImageKey() != null) {
				burl = baseUrl
						+ movie.getBackgroundImageKey().replaceFirst("/", "");
			}
			mpi.setBackgroundURL(burl);

			String turl = "";
			if (movie.getThumbNailImageKey() != null) {
				turl = baseUrl
						+ movie.getThumbNailImageKey().replaceFirst("/", "");
			}

			mpi.setPosterURL(turl);
			mpi.setTitle(movie.getTitle());

			mpi.setContentRating(movie.getContentRating());

			List<Media> mediacont = movie.getMedias();
			if (mediacont != null && !mediacont.isEmpty()) {
				// We grab the first media container until we know more about
				// why there can be multiples.
				Media media = mediacont.get(0);
				List<Part> parts = media.getVideoPart();
				Part part = parts.get(0);
				mpi.setAudioCodec(media.getAudioCodec());
				mpi.setVideoCodec(media.getVideoCodec());
				mpi.setVideoResolution(media.getVideoResolution());
				mpi.setAspectRatio(media.getAspectRatio());
				mpi.setAudioChannels(media.getAudioChannels());

				String directPlayUrl = factory.baseURL()
						+ part.getKey().replaceFirst("/", "");
				mpi.setDirectPlayUrl(directPlayUrl);

			}

			String movieDetails = createVideoDetails(movie, mpi);
			mpi.setCastInfo(movieDetails);

			posterList.add(mpi);
		}

	}

	protected MediaContainer retrieveVideos() throws Exception {
		if (category == null) {
			category = "all";
		}

		return factory.retrieveSections(key, category);
	}

	/**
	 * @param video
	 * @param videoContentInfo
	 * @return
	 */
	protected String createVideoDetails(Video video,
			VideoContentInfo videoContentInfo) {
		String videoDetails = "";

		if (video.getYear() != null) {
			videoContentInfo.setYear(video.getYear());
			videoDetails = "Year: " + video.getYear();
			videoDetails = videoDetails + "  ";
		}

		if (video.getGenres() != null && video.getGenres().size() > 0) {
			videoDetails = videoDetails + "Genre(s): ";
			ArrayList<String> g = new ArrayList<String>();
			
			for (Genre genre : video.getGenres()) {
				g.add(genre.getTag());
				videoDetails = videoDetails + genre.getTag() + "/";
			}
			videoContentInfo.setGenres(g);
			videoDetails = videoDetails.substring(0,
					videoDetails.lastIndexOf("/"));
			videoDetails = videoDetails + "  ";
		}

		if (video.getWriters() != null && video.getWriters().size() > 0) {
			videoDetails = videoDetails + "Writer(s): ";
			ArrayList<String> w = new ArrayList<String>();
			for (Writer writer : video.getWriters()) {
				w.add(writer.getTag());
				videoDetails = videoDetails + writer.getTag() + ", ";
			}
			videoContentInfo.setWriters(w);
			videoDetails = videoDetails.substring(0,
					videoDetails.lastIndexOf(","));
			videoDetails = videoDetails + "  ";
		}

		if (video.getDirectors() != null && video.getDirectors().size() > 0) {
			videoDetails = videoDetails + "Director(s): ";
			ArrayList<String> d = new ArrayList<String>();
			for (Director director : video.getDirectors()) {
				d.add(director.getTag());
				videoDetails = videoDetails + director.getTag() + ", ";
			}
			videoContentInfo.setDirectors(d);
			videoDetails = videoDetails.substring(0,
					videoDetails.lastIndexOf(","));
			videoDetails = videoDetails + "\r\n";
		}
		return videoDetails;
	}

}
