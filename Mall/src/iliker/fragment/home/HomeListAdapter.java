package iliker.fragment.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.ImageManager;

import java.net.URLEncoder;
import java.util.List;

import cn.iliker.mall.R;
import iliker.entity.BaseRec;
import iliker.entity.Category;
import iliker.entity.Fashion;
import iliker.entity.Recommend;
import iliker.entity.Result;
import iliker.utils.BitmapHelp;
import iliker.utils.GeneralUtil;

/*
 * ExpandableListView适配器
 */
public class HomeListAdapter extends BaseExpandableListAdapter {
    private final List<List<BaseRec>> data;
    private final LayoutInflater inflater;
    private final ImageManager bitmapUtils;
    private final String[] str = {"身形数据", "产品推荐", "服装搭配", "锦上添花"};
    private final Drawable opendrawable;
    private final Drawable closedrawable;

    public HomeListAdapter(Context context, List<List<BaseRec>> data) {
        this.data = data;
        inflater = LayoutInflater.from(context);
        bitmapUtils = BitmapHelp.getBitmapUtils();
        opendrawable = context.getResources().getDrawable(R.drawable.collapse);
        opendrawable.setBounds(0, 0, opendrawable.getMinimumWidth(), opendrawable.getMinimumHeight());
        closedrawable = context.getResources().getDrawable(R.drawable.expanded);
        closedrawable.setBounds(0, 0, closedrawable.getMinimumWidth(), closedrawable.getMinimumHeight());
    }

    @Override
    public int getGroupCount() {
        return str.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return str[groupPosition];
    }

    @Override
    public BaseRec getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupHolder groupHolder;
        if (convertView == null) {
            groupHolder = new GroupHolder();
            convertView = inflater.inflate(R.layout.group, null);
            groupHolder.textView = (TextView) convertView
                    .findViewById(R.id.group);
            groupHolder.textView.setTextSize(15);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        groupHolder.textView.setText(getGroup(groupPosition).toString());
        if (isExpanded)
            groupHolder.textView.setCompoundDrawables(closedrawable, null, null, null);
        else
            groupHolder.textView.setCompoundDrawables(opendrawable, null, null, null);
        return convertView;

    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("deprecation")
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        BaseRec baseRec = getChild(groupPosition, childPosition);
        if (baseRec instanceof Result) {
            View view = inflater.inflate(R.layout.result, null);
            ResultHolder holder = new ResultHolder();
            holder.typeimg = (ImageView) view.findViewById(R.id.typeimg);
            holder.typename = (TextView) view.findViewById(R.id.typename);
            holder.bmi = (TextView) view.findViewById(R.id.bmi);
            holder.descText = (TextView) view.findViewById(R.id.descText);

            Result result = (Result) baseRec;
            holder.typename.setText(result.getTypename());
            holder.bmi.setText(String.valueOf("你的BMI值:" + result.getBmi()));
            String builder = "尊敬的顾客，你的BMI值是:" +
                    result.getBmi() +
                    "(" +
                    result.getDesc() +
                    ");" +
                    "经过我们专家的数据测评，你的身材为:" +
                    result.getTypename() +
                    "适合穿着" +
                    result.getCupType() +
                    "杯文胸,内裤建议为" +
                    result.getUw() +
                    "码。岁月流逝，身形渐变，请通过深度测试保持你身形数据的更新!\n祝你生活愉快!";
            holder.descText.setText(builder);
            bitmapUtils.bind(holder.typeimg, GeneralUtil.HOST + result.getImgpath());
            return view;
        }
        if (baseRec instanceof Recommend) {
            View view = inflater.inflate(R.layout.recommend, null);
            ViewHolder holder = new ViewHolder();
            holder.recommend_img = (ImageView) view
                    .findViewById(R.id.reimageView);
            holder.seriesName = (TextView) view
                    .findViewById(R.id.seriesName);
            holder.memberPrice = (TextView) view
                    .findViewById(R.id.memberPrice);
            Recommend re = (Recommend) baseRec;
            String imgPath = re.getImgPath();
            holder.seriesName.setText(re.getSeriesName());
            holder.memberPrice.setText(String.valueOf("会员价:￥" + re.getMemberPrice()));
            String startstr = imgPath
                    .substring(0, imgPath.lastIndexOf("/") + 1);
            String endstr = imgPath.substring(imgPath.lastIndexOf("/") + 1,
                    imgPath.lastIndexOf("."));
            String parsUrl = URLEncoder.encode(endstr);
            bitmapUtils.bind(holder.recommend_img, GeneralUtil.HOST + startstr + parsUrl + ".png");
            return view;
        }

        if (baseRec instanceof Fashion) {
            View view = inflater.inflate(R.layout.category_item, null);
            IfViewHolder holder = new IfViewHolder();
            holder.coll_item_img = (ImageView) view
                    .findViewById(R.id.coll_item_img);
            holder.coll_item_name = (TextView) view
                    .findViewById(R.id.coll_item_name);
            Fashion fa = (Fashion) baseRec;
            holder.coll_item_name.setText(fa.getName());// imgPath
            String imgPath = fa.getImgPath();
            String startstr = imgPath
                    .substring(0, imgPath.lastIndexOf("/") + 1);
            String parsstr = imgPath.substring(imgPath.lastIndexOf("/") + 1,
                    imgPath.lastIndexOf("/") + 3);
            String endstr = imgPath.substring(imgPath.lastIndexOf("/") + 3,
                    imgPath.length());
            String parsUrl = URLEncoder.encode(parsstr);
            String url = GeneralUtil.HOST + startstr + parsUrl + endstr;
            bitmapUtils.bind(holder.coll_item_img, url);
            return view;
        }
        if (baseRec instanceof Category) {
            View view = inflater.inflate(R.layout.category, null);
            CateViewHolder holder = new CateViewHolder();
            holder.imgPath = (ImageView) view
                    .findViewById(R.id.cateImageView);
            holder.icfcname = (TextView) view.findViewById(R.id.icfcname);
            holder.icfname = (TextView) view.findViewById(R.id.icfname);
            Category cate = (Category) baseRec;
            holder.icfcname.setText(cate.getIcfcname());
            holder.icfname.setText(cate.getIcfname());
            String imgPath = cate.getImgPath();
            String startstr = imgPath
                    .substring(0, imgPath.lastIndexOf("/") + 1);
            String parsstr = imgPath.substring(imgPath.lastIndexOf("/") + 1,
                    imgPath.lastIndexOf("/") + 3);
            String endstr = imgPath.substring(imgPath.lastIndexOf("/") + 3,
                    imgPath.length());
            String parsUrl = URLEncoder.encode(parsstr);
            String url = GeneralUtil.HOST + startstr + parsUrl + endstr;
            bitmapUtils.bind(holder.imgPath, url);
            return view;
        }
        return null;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ResultHolder {
        private ImageView typeimg;
        private TextView typename;
        private TextView bmi;
        private TextView descText;
    }

    private class ViewHolder {
        ImageView recommend_img;
        TextView seriesName;
        TextView memberPrice;
    }

    private class IfViewHolder {
        ImageView coll_item_img;
        TextView coll_item_name;
    }

    private class CateViewHolder {
        private TextView icfname;
        private TextView icfcname;
        private ImageView imgPath;
    }

    private class GroupHolder {
        TextView textView;
    }
}
