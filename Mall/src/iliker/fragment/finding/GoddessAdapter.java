package iliker.fragment.finding;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import cn.iliker.mall.R;
import iliker.entity.Goddess;
import iliker.entity.UserInfo;
import iliker.fragment.finding.holder.Goddess_item_Hodler;

class GoddessAdapter extends RecyclerView.Adapter<Goddess_item_Hodler> {
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private final Context context;
    private final Goddess goddess;

    GoddessAdapter(Goddess goddess, Context context) {
        this.goddess = goddess;
        this.context = context;
    }


    @Override
    public Goddess_item_Hodler onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new Goddess_item_Hodler(View.inflate(context, R.layout.goddess_item_layout, null));
    }

    @Override
    public void onBindViewHolder(Goddess_item_Hodler goddess_item_hodler, final int position) {
        UserInfo userInfo = goddess.getUsers().get(position);
        goddess_item_hodler.setData(userInfo, goddess.getPraises());
        if (onRecyclerViewItemClickListener != null) {
            goddess_item_hodler.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRecyclerViewItemClickListener.onItemClick(position);
                }
            });
        }

    }


    interface OnRecyclerViewItemClickListener {
        void onItemClick(int position);
    }

    void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public int getItemCount() {
        return goddess.getUsers().size();
    }
}
