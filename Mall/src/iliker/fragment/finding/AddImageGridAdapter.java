package iliker.fragment.finding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.iliker.mall.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class AddImageGridAdapter extends BaseAdapter {
    // 定义Context
    private final Context context;
    // 图片地址
    private List<Bitmap> imageList = new ArrayList<>();

    public AddImageGridAdapter(Context context, List<Bitmap> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    // 获取图片的个数
    public int getCount() {
        return imageList.size();
    }

    // 获取图片在库中的位置
    public Object getItem(int position) {
        return position;
    }

    // 获取图片ID
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.image_add_grid_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_view);
        imageView.setImageBitmap(imageList.get(position));
        return view;
    }
}
