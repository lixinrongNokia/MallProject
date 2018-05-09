package iliker.fragment.faxian;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.iliker.mall.R;

import java.util.LinkedList;

import static iliker.utils.BitmapHelp.getBitmapUtils;

/**
 * 显示图片
 *
 * @author lixinrong
 */
public class ImageActivity extends Activity {
    private ViewPager view_pager;
    private Dialog progressDialog;// 等待弹出框
    public static final String CURRENT_INDEX = "currentIndex";
    private String[] imgpaths;
    private int index;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageshower);
        imgpaths = this.getIntent().getStringArrayExtra("imgpaths");
        this.index = this.getIntent().getIntExtra(CURRENT_INDEX, 0);
        view_pager = (ViewPager) this.findViewById(R.id.view_pager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        view_pager.setAdapter(new SamplePagerAdapter());
        view_pager.setCurrentItem(index);
    }

    private class SamplePagerAdapter extends PagerAdapter {
        LinkedList<ImageView> convertView = new LinkedList<ImageView>();

        @Override
        public int getCount() {
            return imgpaths.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            int index = position % imgpaths.length;
            ImageView view;
            if (convertView.size() > 0) {
                view = convertView.remove(0);
            } else {
                view = new ImageView(container.getContext());
            }
            getBitmapUtils().bind(view, imgpaths[index]);
            container.addView(view); // 加载的view对象
            return view; // 返回的对象
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            convertView.add(view);// 把移除的对象 添加到缓存集合中
            container.removeView(view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    /*private class AsyncDownLoad extends AsyncTask<String, String, byte[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new Dialog(ImageActivity.this,
                    R.style.like_toast_dialog_style);
            progressDialog.setContentView(R.layout.customprogressdialog);
            Window window = progressDialog.getWindow();
            if (window != null)
                window.setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected byte[] doInBackground(String... params) {
            return ParsJsonUtil.AsyncDownLoad(params[0]);
        }

        @Override
        protected void onPostExecute(byte[] result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (result == null) {
                Toast.makeText(ImageActivity.this, "下载失败", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Bitmap bmp = BitmapFactory.decodeByteArray(result, 0,
                        result.length);
                ContentResolver cr = getContentResolver();
                MediaStore.Images.Media.insertImage(cr, bmp,
                        System.currentTimeMillis() + ".png", "");
                Toast.makeText(ImageActivity.this, "已保存在内部存储!",
                        Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 判断SDK版本是不是4.4或者高于4.4
                    String[] paths = new String[]{Environment
                            .getExternalStorageDirectory().toString()};
                    MediaScannerConnection.scanFile(ImageActivity.this, paths,
                            null, null);
                } else {
                    sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_MOUNTED,
                            Uri.parse("file://"
                                    + Environment.getExternalStorageDirectory())));

                }
            }

        }
    }*/
}
