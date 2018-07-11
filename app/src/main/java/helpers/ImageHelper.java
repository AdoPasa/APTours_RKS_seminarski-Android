package helpers;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class ImageHelper {
    public static  byte[] GetBytesFromBitmap(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }
}
