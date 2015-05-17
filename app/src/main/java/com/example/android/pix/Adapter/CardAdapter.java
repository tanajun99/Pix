package com.example.android.pix.Adapter;

    import android.support.v7.widget.RecyclerView;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;

    import com.example.android.pix.NatureItem;
    import com.example.android.pix.R;

    import java.util.ArrayList;
    import java.util.List;


/**
 * Created by Edwin on 18/01/2015.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    List<NatureItem> mItems;

    public CardAdapter() {
        super();
        mItems = new ArrayList<NatureItem>();
        NatureItem nature = new NatureItem();
        nature.setName("Stanford Memorial Church");
        nature.setDes("Stanford Memorial Church stands at the center of the campus, and is the University’s architectural crown jewel. It was one of the earliest, and is still among the most prominent, interdenominational churches in the West. "
                );
        nature.setThumbnail(R.drawable.ch);
        mItems.add(nature);

        nature = new NatureItem();
        nature.setName("Oakland Bay Bridge");
        nature.setDes("The San Francisco–Oakland Bay Bridge (known locally as the Bay Bridge) is a complex of bridges spanning San Francisco Bay in California. ");
        nature.setThumbnail(R.drawable.br);
        mItems.add(nature);

        nature = new NatureItem();
        nature.setName("Cool Android Developer!");
        nature.setDes("Android runs on hundreds of millions of handheld devices around the world" +
                "and it now supports these exciting, new form-factors.");
        nature.setThumbnail(R.drawable.android);
        mItems.add(nature);

        nature = new NatureItem();
        nature.setName("Stanford Memorial Night");
        nature.setDes("Stanford Memorial Church (also referred to informally as MemChu)[1] is located on the Main Quad at the center of the Stanford University campus in Stanford, California, United States.");
        nature.setThumbnail(R.drawable.chn);
        mItems.add(nature);


        nature = new NatureItem();
        nature.setName("Santa Cruz Beach");
        nature.setDes("Whether you enjoy surfing, playing beach volleyball, or hiking along the coastal bluffs, our beaches offer something for everyone. ");
        nature.setThumbnail(R.drawable.sc);
        mItems.add(nature);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_card_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        NatureItem nature = mItems.get(i);
        viewHolder.tvNature.setText(nature.getName());
        viewHolder.tvDesNature.setText(nature.getDes());
        viewHolder.imgThumbnail.setImageResource(nature.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgThumbnail;
        public TextView tvNature;
        public TextView tvDesNature;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView)itemView.findViewById(R.id.img_thumbnail);
            tvNature = (TextView)itemView.findViewById(R.id.tv_nature);
            tvDesNature = (TextView)itemView.findViewById(R.id.tv_des_nature);
        }
    }
}


