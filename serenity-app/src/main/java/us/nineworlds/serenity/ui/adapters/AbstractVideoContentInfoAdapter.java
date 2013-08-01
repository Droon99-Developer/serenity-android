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

package us.nineworlds.serenity.ui.adapters;

import java.util.ArrayList;
import java.util.List;


import us.nineworlds.serenity.SerenityApplication;
import us.nineworlds.serenity.core.model.VideoContentInfo;

import android.app.Activity;
import android.content.Context;
import android.widget.BaseAdapter;

/**
 * An abstract class for handling the creation of video content for use during
 * browsing. Implementations need to implement the abstract methods to provide
 * functionality for retrieval and display of video content when browsing the
 * episodes.
 * 
 * @author dcarver
 * 
 */
public abstract class AbstractVideoContentInfoAdapter extends AbstractContentInfoAdapter {

	protected static List<VideoContentInfo> posterList = new ArrayList<VideoContentInfo>();
	
	protected static final int SIZE_HEIGHT = 400;
	protected static final int SIZE_WIDTH = 200;

	public AbstractVideoContentInfoAdapter(Activity c, String key) {
		super(c, key, null);
		fetchDataFromService();
	}

	public AbstractVideoContentInfoAdapter(Activity c, String key,
			String category) {
		super(c, key, category);
		fetchDataFromService();
	}

	@Override
	public int getCount() {
		return posterList.size();
	}

	@Override
	public Object getItem(int position) {
		return posterList.get(position);
	}

}