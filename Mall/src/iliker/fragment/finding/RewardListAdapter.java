package iliker.fragment.finding;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.iliker.mall.R;
import iliker.entity.Reward_Item;

 class RewardListAdapter extends RecyclerView.Adapter<RewardListAdapter.RewardListHolder> {

    private final List<Reward_Item> list;
    private final Context context;

     RewardListAdapter(Context context, List<Reward_Item> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RewardListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RewardListHolder(View.inflate(context, R.layout.income_item_layout, null));
    }

    @Override
    public void onBindViewHolder(RewardListHolder holder, final int position) {
        Reward_Item reward_item=list.get(position);
        holder.incomeTime.setText(reward_item.getIncomeTime());
        holder.incomeDESC.setText(reward_item.getIncomeDESC());
        holder.incomeAmount.setText(reward_item.getIncomeAmount());
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastFactory.getMyToast(""+position).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RewardListHolder extends RecyclerView.ViewHolder {
        private final TextView incomeTime;
        private final TextView incomeAmount;
        private final TextView incomeDESC;

         RewardListHolder(View itemView) {
            super(itemView);
            this.incomeTime = (TextView) itemView.findViewById(R.id.incomeTime);
            this.incomeDESC = (TextView) itemView.findViewById(R.id.incomeDESC);
            this.incomeAmount = (TextView) itemView.findViewById(R.id.incomeAmount);
        }
    }
}
