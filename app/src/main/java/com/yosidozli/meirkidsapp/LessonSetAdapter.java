package com.yosidozli.meirkidsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by yosid on 20/05/2017.
 */

public class LessonSetAdapter extends RecyclerView.Adapter<LessonSetAdapter.LessonSetViewHolder> {

    private final static String TAG = "LessonSetAdapter";
    final private ListItemClickListener mListItemClickListener;


    List<LessonSet> list;
    int layoutId;

    public LessonSetAdapter(List<LessonSet> list, ListItemClickListener  listItemClickListener, int layoutId) {
        this.list = list;
        mListItemClickListener = listItemClickListener;
        this.layoutId = layoutId;
    }
    public LessonSetAdapter(List<LessonSet> list, ListItemClickListener  listItemClickListener) {
        this.list = list;
        mListItemClickListener = listItemClickListener;
    }





    @Override
    public LessonSetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForLessonItem = layoutId;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForLessonItem, parent, shouldAttachToParentImmediately);

        LessonSetViewHolder viewHolder = new LessonSetViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LessonSetViewHolder holder, int position) {
       // Log.d(TAG, "#"+position);

        LessonSet lessonSet = list.get(position);

        holder.bind(lessonSet,position);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class LessonSetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView titleTextView;
        ImageView imageView;
        private String imageUrl;
        CardView cardView;
        ProgressBar progressBar;
        int position;
        LessonSet lessonSet;

        public LessonSetViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            imageView = (ImageView) itemView.findViewById(R.id.contentImageView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progresdBar);
            itemView.setOnClickListener(this);

        }

        void bind (LessonSet lessonSet, int position){
            imageView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            titleTextView.setText(lessonSet.getTitle());
            this.imageUrl = lessonSet.getSmallImagePath();
            this.position = position;
            this.lessonSet = lessonSet;
            imageView.setVisibility(View.VISIBLE);




            Glide.with(itemView).load(imageUrl).centerCrop().listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);

                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);

                    return false;
                }
            }).into(imageView);
//            if(lessonSet.getImage() == null) {
//                /*progressBar.setVisibility(View.GONE);
//                imageView.setVisibility(View.VISIBLE);
//                GlideApp.with((Context) mListItemClickListener).load(lessonSet.getBigImagePath()).centerCrop()
//                        .placeholder(new ColorDrawable(Color.DKGRAY)).into(imageView);*/
//                new DownloadImageTask().execute(this);
//            }
//
//            else{
//                imageView.setImageBitmap(lessonSet.getImage());
//                progressBar.setVisibility(View.GONE);
//                imageView.setVisibility(View.VISIBLE);
//            }




        }

        @Override
        public void onClick(View v) {
            int itemClickedPosition = getAdapterPosition();
            mListItemClickListener.onListItemClick(itemClickedPosition);
        }

        class DownloadImageTask extends AsyncTask<LessonSetViewHolder,Void,Bitmap>{
            private LessonSetViewHolder v;
            @Override
            protected Bitmap doInBackground(LessonSetViewHolder... params) {
                Bitmap image;
                v = params[0];
                URLConnection conn;
                InputStream in = null;


                    try {
                        conn = new URL(v.imageUrl).openConnection();
                        in = conn.getInputStream();
                        image =BitmapFactory.decodeStream(in);
                        } catch (IOException e) {
                        image = null;
                    } finally {
                        if (in != null)
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }



                return image;
            }

            @Override
            protected void onPostExecute(Bitmap result){
                super.onPostExecute(result);
                if(v.getAdapterPosition() == position) {
                    v.progressBar.setVisibility(View.GONE);
                    v.imageView.setVisibility(View.VISIBLE);
                    v.lessonSet.setImage(result);
                    imageView.setImageBitmap(result);
                }

            }

        }
    }

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }


}
