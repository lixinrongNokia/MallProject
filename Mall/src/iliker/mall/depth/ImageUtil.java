package iliker.mall.depth;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class ImageUtil {

    public static Bitmap getCompressedImgPath(Resources resources, int sourceId) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(resources, sourceId, opts);

            int w = opts.outWidth;
            int h = opts.outHeight;
            float standardW = 320f;
            float standardH = 180f;

            int zoomRatio = 1;
            if (w > h && w > standardW) {
                zoomRatio = (int) (w / standardW);
            } else if (w < h && h > standardH) {
                zoomRatio = (int) (h / standardH);
            }
            if (zoomRatio <= 0)
                zoomRatio = 1;
            opts.inSampleSize = zoomRatio;
            opts.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(resources, sourceId, opts);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
