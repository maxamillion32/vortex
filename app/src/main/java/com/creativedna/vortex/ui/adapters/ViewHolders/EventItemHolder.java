package com.creativedna.vortex.ui.adapters.ViewHolders;

/**
 * Created by Bryan Lamtoo - creativeDNA (U) LTD.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.colintmiller.simplenosql.NoSQL;
import com.colintmiller.simplenosql.NoSQLEntity;
import com.creativedna.vortex.R;
import com.creativedna.vortex.events.FavoriteEvent;
import com.creativedna.vortex.models.Artist;
import com.creativedna.vortex.models.Event;
import com.creativedna.vortex.ui.activities.EventDetailsActivity;
import com.creativedna.vortex.utils.DataFormatter;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class EventItemHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.ivEventsSingleEvent_favorite_image)
    ImageView ivFavorite;
    @Bind(R.id.ivEventImage)
    ImageView eventImage;
    @Bind(R.id.tvEventTitle)
    TextView eventTitle;
    @Bind(R.id.tvEventDesc)
    TextView eventDesc;
    @Bind(R.id.tvEventTime)
    TextView eventTime;
    @Bind(R.id.tvEventVenue)
    TextView eventVenue;
    @Bind(R.id.tvEventCity)
    TextView eventCity;
    @Bind(R.id.ivShare)
    ImageView shareEvent;

    View itemView;
    Context context;

    public EventItemHolder(Context context, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemView = itemView;
        this.context = context;
    }

    public void renderView(final Event event) {
        eventTitle.setText(event.getName());
        eventCity.setText(event.getVenue().getCity());
        eventDesc.setText(event.getVenue().getName());
        Picasso.with(context).load(event.getImageUrl()).placeholder(R.drawable.placeholder).into(eventImage);
        eventTime.setText(event.getEventDateLocal());
        eventVenue.setText(event.getVenue().getName());
        String time = DataFormatter.formatTime(event.getEventDateLocal());
        eventTime.setText(time);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("event", event);
                context.startActivity(intent);
            }
        });

        String artists = "";

        if (event.getArtists() != null) {
            for (Artist artist : event.getArtists()) {
                artists += artist.getCategory_name() + ", ";
            }
        }
//
        if(artists.length() != 0) {
            artists = artists.trim().substring(0, artists.length() - 1);
        }else{
            artists = "";
        }
//
        Log.d("artists", artists);
//
        if(artists.length() != 0) {
            eventDesc.setText(artists);
            eventDesc.setVisibility(View.VISIBLE);
        }else{
            eventDesc.setText(artists);
            eventDesc.setVisibility(View.GONE);
        }

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (ivFavorite.getTag().equals("unfavorite")) {
                if (!event.is_favorite()) {
                    ivFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.starred_star));
                    favorite(event);
                    Toast.makeText(context, "Added to Favorites", Toast.LENGTH_LONG).show();
                    ivFavorite.setTag("favorite");
                    event.setIs_favorite(true);
                } else {
                    ivFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.star_unstarred));
                    unfavorite(event);
                    Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_LONG).show();
                    ivFavorite.setTag("unfavorite");
                    event.setIs_favorite(false);
                    //save(event);

                }
                EventBus.getDefault().post(new FavoriteEvent(event));
            }

        });
    }

    public void favorite(Event event) {
        NoSQLEntity<Event> entity = new NoSQLEntity<Event>("favorites", event.getId() + "");
        entity.setData(event);
        NoSQL.with(context).using(Event.class).save(entity);
    }

    public void unfavorite(Event event) {
        NoSQL.with(context).using(Event.class)
                .bucketId("favorites")
                .entityId(event.getId() + "")
                .delete();
    }

}