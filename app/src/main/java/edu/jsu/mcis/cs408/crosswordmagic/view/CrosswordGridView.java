package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputType;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;

public class CrosswordGridView extends View implements AbstractView {

    private CrosswordMagicController controller;

    private final char BLOCK = '*';

    private final float TEXT_NUMBER_SCALE = 4.75f; // text scale (larger value for smaller text)
    private final float TEXT_LETTER_SCALE = 1.5f; // number scale (larger value for smaller numbers)

    private final float TEXT_SCALE_MIN = 63;

    private final Paint gridPaint;
    private final TextPaint gridTextPaint;

    private int viewWidth, viewHeight, gridWidth, gridHeight;
    private int squareWidth, squareHeight, xBegin, yBegin, xEnd, yEnd;

    private Character[][] letters;
    private Integer[][] numbers;

    private Context appContext;

    public CrosswordGridView(Context context, AttributeSet attrs) {

        super(context, attrs);

        this.appContext = context;

        gridTextPaint = new TextPaint();
        gridTextPaint.setAntiAlias(true);
        gridTextPaint.setColor(Color.BLACK);

        gridPaint = new Paint();
        gridPaint.setColor(Color.BLACK);
        gridPaint.setAntiAlias(true);
        gridPaint.setStyle(Paint.Style.STROKE);

        setOnTouchListener(new OnTouchHandler(context));

        this.controller = ((MainActivity)context).getController();

        controller.addView(this);

        controller.getGridDimensions();
        controller.getGridLetters();
        controller.getGridNumbers();

    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {

        super.onSizeChanged(xNew, yNew, xOld, yOld);

        this.viewWidth = xNew;
        this.viewHeight = yNew;

    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {

        super.onDraw(canvas);

        if (gridWidth > 0 && gridHeight > 0) {

            int gridSize = Math.min(viewWidth, viewHeight);

            this.squareWidth = (gridSize / gridWidth);
            this.squareHeight = (gridSize / gridHeight);

            this.yBegin = (viewHeight - (squareHeight * gridHeight)) / 2;
            this.xBegin = (viewWidth - (squareWidth * gridWidth)) / 2;

            this.yEnd = yBegin + (squareHeight * gridHeight);
            this.xEnd = xBegin + (squareWidth * gridWidth);

            drawGrid(canvas);
            drawBlocks(canvas);
            drawNumbers(canvas);
            drawLetters(canvas);

            //Toast.makeText(this.getContext(), "Square Width: " + squareWidth, Toast.LENGTH_SHORT).show();

        }

    }

    private void drawLetters(Canvas canvas) {

        if (letters != null) {

            float letterTextSize = (squareWidth < TEXT_SCALE_MIN ? (squareWidth / TEXT_LETTER_SCALE) : (squareWidth / (TEXT_LETTER_SCALE * 2)));
            gridTextPaint.setTextSize(letterTextSize * getResources().getDisplayMetrics().density);

            for (int y = 0; y < letters.length; ++y) {

                for (int x = 0; x < letters[y].length; ++x) {

                    String text = String.valueOf(letters[y][x]);
                    int width = (int)gridTextPaint.measureText(text);

                    int xBeginLetter = ((x * squareWidth) + xBegin) + ((squareWidth - width) / 2);
                    int yBeginLetter = (y * squareWidth) + yBegin;

                    StaticLayout staticLayout = new StaticLayout(text, gridTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);

                    canvas.save();
                    canvas.translate(xBeginLetter, yBeginLetter);
                    staticLayout.draw(canvas);
                    canvas.restore();

                }

            }

        }

    }

    private void drawNumbers(Canvas canvas) {

        if (numbers != null) {

            float numberTextSize = (squareWidth < TEXT_SCALE_MIN ? (squareWidth / TEXT_NUMBER_SCALE) : (squareWidth / (TEXT_NUMBER_SCALE * 2)));
            gridTextPaint.setTextSize(numberTextSize * getResources().getDisplayMetrics().density);

            for (int y = 0; y < numbers.length; ++y) {

                for (int x = 0; x < numbers[y].length; ++x) {

                    if (numbers[y][x] != 0) {

                        String text = String.valueOf(numbers[y][x]);
                        int width = (int) gridTextPaint.measureText(text);

                        int xBeginNumber = (x * squareWidth) + xBegin;
                        int yBeginNumber = (y * squareWidth) + yBegin;

                        StaticLayout staticLayout = new StaticLayout(text, gridTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);

                        canvas.save();
                        canvas.translate(xBeginNumber, yBeginNumber);
                        staticLayout.draw(canvas);
                        canvas.restore();

                    }

                }

            }

        }

    }

    private void drawGrid(Canvas canvas) {

        if (gridWidth > 0 && gridHeight > 0) {

            // draw bounding rectangle

            canvas.drawRect(xBegin, yBegin, xEnd, yEnd, gridPaint);

            // draw grid lines (vertical)

            for (int i = 1; i < gridWidth; ++i) {
                canvas.drawLine(((i * squareWidth) + xBegin), yBegin, ((i * squareWidth) + xBegin), yEnd, gridPaint);
            }

            // draw grid lines (horizontal)

            for (int i = 1; i < gridWidth; ++i) {
                canvas.drawLine(xBegin, ((i * squareHeight) + yBegin), xEnd, ((i * squareHeight) + yBegin), gridPaint);
            }

        }

    }

    private void drawBlocks(Canvas canvas) {

        if (letters != null) {

            gridPaint.setStyle(Paint.Style.FILL);

            for (int y = 0; y < letters.length; ++y) {

                for (int x = 0; x < letters[y].length; ++x) {

                    if (letters[y][x] == BLOCK) {

                        int xBeginBlock = (x * squareWidth) + xBegin;
                        int yBeginBlock = (y * squareWidth) + yBegin;

                        int xEndBlock = (xBeginBlock + squareWidth);
                        int yEndBlock = (yBeginBlock + squareHeight);

                        canvas.drawRect(xBeginBlock, yBeginBlock, xEndBlock, yEndBlock, gridPaint);

                    }

                }

            }

            gridPaint.setStyle(Paint.Style.STROKE);

        }

    }



    @Override
    public void modelPropertyChange(final PropertyChangeEvent evt) {

        String name = evt.getPropertyName();
        Object value = evt.getNewValue();

        if (name.equals(CrosswordMagicController.GRID_LETTERS_PROPERTY)) {

            if (value instanceof Character[][]) {

                this.letters = (Character[][]) value;

                invalidate();

            }

        }

        if (name.equals(CrosswordMagicController.GRID_NUMBERS_PROPERTY)) {

            if (value instanceof Integer[][]) {

                this.numbers = (Integer[][]) value;

                invalidate();

            }

        }

        if (name.equals(CrosswordMagicController.GRID_DIMENSION_PROPERTY)) {

            if (value instanceof Integer[]) {

                Integer[] dimension = (Integer[]) value;

                this.gridHeight = dimension[0];
                this.gridWidth = dimension[1];

                invalidate();

            }

        }

        if (name.equals(CrosswordMagicController.GUESS_PROPERTY)) {

            if (value instanceof Integer) {

                String message = this.getContext().getString((Integer)value);

                Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();

            }

        }

    }

    private class OnTouchHandler implements OnTouchListener {

        private Context context;

        public OnTouchHandler(Context context) {
            this.context = context;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            int eventX = (int)event.getX();
            int eventY = (int)event.getY();

            if (eventX >= xBegin && eventX <= xEnd && eventY >= yBegin && eventY <= yEnd) {

                int x = ((eventX - xBegin) / squareWidth);
                int y = ((eventY - yBegin) / squareHeight);
                int n = numbers[y][x];

                if (n != 0) {

                    //String text = String.format(Locale.getDefault(),"X: %d, Y: %d, Box: %d", x, y, n);
                    //Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.guess_dialog_title);
                    builder.setMessage(R.string.guess_dialog_message);
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton(R.string.guess_dialog_button_guess, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface d, int i) {

                            String userInput = input.getText().toString();

                            HashMap<String, String> params = new HashMap<>();
                            params.put("num", String.valueOf(n));
                            params.put("guess", userInput.toUpperCase().trim());

                            controller.setGuess(params);

                        }

                    });

                    builder.setNegativeButton(R.string.guess_dialog_button_cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface d, int i) {

                            String userInput = "";
                            d.cancel();

                        }

                    });

                    builder.show();

                }

            }

            return false;

        }

    }

}