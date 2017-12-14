package comfolioreader.android.sample;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.folioreader.model.HighLight;
import com.folioreader.ui.base.OnSaveHighlight;
import com.folioreader.util.FolioReader;
import com.folioreader.util.OnHighlightListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

public class HomeActivity extends AppCompatActivity implements OnHighlightListener {

    private FolioReader folioReader;
    private TextView title,title2,authors,authors2;
    private String bookName1 = "books/test.epub", bookName2 = "books/TheSilverChair.epub";
    private ImageView cover,cover2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        folioReader = new FolioReader(this);
        folioReader.registerHighlightListener(this);
       
        getHighlightsAndSave();
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        LinearLayout book1 = (LinearLayout) findViewById(R.id.bookOne);
        book1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folioReader.openBook("file:///android_asset/12economic.epub");
            }
        });
        LinearLayout book2 = (LinearLayout) findViewById(R.id.bookTwo);
        book2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folioReader.openBook("file:///android_asset/TheSilverChair.epub");
            }
        });
        LinearLayout book3 = (LinearLayout) findViewById(R.id.bookThree);
        book3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folioReader.openBook("file:///android_asset/TheSilverChair.epub");
            }
        });
        LinearLayout book4 = (LinearLayout) findViewById(R.id.bookFour);
        book4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folioReader.openBook("file:///android_asset/test.epub");
            }
        });
        cover =findViewById(R.id.image_book);
        title =  findViewById(R.id.title_book);
        authors =  findViewById(R.id.author_book);
        cover2 = findViewById(R.id.image_book2);
        title2 =  findViewById(R.id.title_book2);
        authors2 =  findViewById(R.id.author_book2);

        loadBook(bookName1,title,authors,cover);
        loadBook(bookName2,title2,authors2,cover2);
        
      
    }

    /*
     * For testing purpose, we are getting dummy highlights from asset. But you can get highlights from your server
     * On success, you can save highlights to DB.
     */
    private void getHighlightsAndSave() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HighLight> highlightList = null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    highlightList = objectMapper.readValue(
                            loadAssetTextAsString("highlights/highlights_data.json"),
                            new TypeReference<List<HighlightData>>() {
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (highlightList == null) {
                    folioReader.saveReceivedHighLights(highlightList, new OnSaveHighlight() {
                        @Override
                        public void onFinished() {
                            //You can do anything on successful saving highlight list
                        }
                    });
                }
            }
        }).start();
    }

    private String loadAssetTextAsString(String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("HomeActivity", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("HomeActivity", "Error closing asset " + name);
                }
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        folioReader.unregisterHighlightListener();
    }

    @Override
    public void onHighlight(HighLight highlight, HighLight.HighLightAction type) {
        Toast.makeText(this,
                "highlight id = " + highlight.getUUID() + " type = " + type,
                Toast.LENGTH_SHORT).show();
    }
    public void loadBook(String bookName, TextView textTitle, TextView textAuthor, ImageView image) {

        AssetManager assetManager = getAssets();

        try {
            InputStream epubInputStream = assetManager.open(bookName);
            Book book = (new EpubReader()).readEpub(epubInputStream);

            String authorBook = String.valueOf(book.getMetadata().getAuthors());
            String titleBook = book.getTitle();
            Bitmap coverImage = BitmapFactory.decodeStream(book.getCoverImage().getInputStream());

          //  textAuthor.setText(authorBook);
            //textTitle.setText(titleBook);
          //  image.setImageBitmap(coverImage);

            Log.i("epub", "author(s): " + authorBook);
            Log.i("epub", "title: " + titleBook);
            Log.i("epub", "Coverimage is " + coverImage.getWidth() + " by " + coverImage.getHeight() + " pixels");

            logTableOfContents(book.getTableOfContents().getTocReferences(), 0);

        } catch (IOException e) {
            Log.e("epub", e.getMessage());
        }
    }

    private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
        if (tocReferences == null) {
            return;
        }
        for (TOCReference tocReference : tocReferences) {
            StringBuilder tocString = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }
            tocString.append(tocReference.getTitle());
            Log.i("epublib", tocString.toString());
            logTableOfContents(tocReference.getChildren(), depth + 1);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}