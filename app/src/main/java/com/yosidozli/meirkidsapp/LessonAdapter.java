package com.yosidozli.meirkidsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by yosid on 20/05/2017.
 */

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private final static String TAG = "LessonAdapter";
    final private ListItemClickListener mListItemClickListener;
    private boolean shouldShowRegister = false;


    public boolean isShouldShowRegister() {
        return shouldShowRegister;
    }

    public void setShouldShowRegister(boolean shouldShowRegister) {
        this.shouldShowRegister = shouldShowRegister;
    }



    List<Lesson> lessonList;
    int layoutId;

    public LessonAdapter(List<Lesson> lessonList ,ListItemClickListener  listItemClickListener, int layoutId) {
        this.lessonList = lessonList;
        mListItemClickListener = listItemClickListener;
        this.layoutId = layoutId;
    }
    public LessonAdapter(List<Lesson> lessonList ,ListItemClickListener  listItemClickListener) {
        this.lessonList = lessonList;
        mListItemClickListener = listItemClickListener;
    }





    @Override
    public LessonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutForLessonItem = layoutId;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutForLessonItem, parent, shouldAttachToParentImmediately);
        LessonViewHolder viewHolder = new LessonViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LessonViewHolder holder, int position) {
        //Log.d(TAG, "#"+position);
        Lesson lesson;
        if(shouldShowRegister)
            lesson = position >0 ? lessonList.get(position-1) : null;
        else
            lesson = lessonList.get(position);

        holder.bind(lesson,position);


    }


    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    class LessonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView titleTextView;
        TextView setNameTextView;
        ImageView imageView;
        private String imageUrl;
        CardView cardView;
        ProgressBar progressBar;
        int position;
        Lesson lesson;

        public LessonViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            setNameTextView = (TextView) itemView.findViewById(R.id.setNameTextView);
            imageView = (ImageView) itemView.findViewById(R.id.contentImageView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progresdBar);
            itemView.setOnClickListener(this);

        }

        void bind (Lesson lesson, int position) {
            if ( shouldShowRegister && position == 0){

                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                setNameTextView.setVisibility(View.GONE);
                titleTextView.setText(R.string.message_register_to_continue);




        } else {
                imageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                titleTextView.setText(lesson.getTitle());
                setNameTextView.setText(lesson.getSetName());
                this.imageUrl = lesson.getImageUrl();
                this.position = position;
                this.lesson = lesson;

                if(lesson.getImage() == null) {
                   /* progressBar.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    GlideApp.with((Context) mListItemClickListener).load(lesson.getImageUrl()).centerCrop().placeholder(new ColorDrawable(Color.DKGRAY)).into(imageView);*/

                    new DownloadImageTask().execute(this);
                }

                else {
                    this.imageView.setImageBitmap(lesson.getImage());
                    progressBar.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }


            }



        }



        @Override
        public void onClick(View v) {
            int itemClickedPosition = getAdapterPosition();
            mListItemClickListener.onListItemClick(itemClickedPosition);
        }

        class DownloadImageTask extends AsyncTask<LessonViewHolder,Void,Bitmap>{
            private LessonViewHolder v;
            @Override
            protected Bitmap doInBackground(LessonViewHolder... params) {
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
                    v.lesson.setImage(result);
                    imageView.setImageBitmap(result);

                }

            }

        }
    }

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }



}
