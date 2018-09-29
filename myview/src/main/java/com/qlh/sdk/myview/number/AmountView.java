package com.qlh.sdk.myview.number;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qlh.sdk.myview.R;


/**
 * 自定义组件：购买数量，带减少增加按钮
 * Created by hiwhitley on 2016/7/4.
 */
public class AmountView extends LinearLayout implements View.OnClickListener, TextWatcher {

    private static final String TAG = "AmountView";
    private int amount = 1; //购买数量
    private int minValue = 0;//能减到的最小值
    private String minValueTip = "已经是最小值了~~";
    private int maxValue = Integer.MAX_VALUE;//能加到的最大值
    private String maxValueTip = "已经是最大值了~~";
    private int step = 1;//步长

    private OnAmountChangeListener mListener;

    private EditText etAmount;
    private Button btnDecrease;
    private Button btnIncrease;

    private float mDensity;


    public AmountView(Context context) {
        this(context, null);
    }

    public AmountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDensity = getResources().getDisplayMetrics().density;
        LayoutInflater.from(context).inflate(R.layout.view_amount, this);
        etAmount = (EditText) findViewById(R.id.etAmount);
        btnDecrease = (Button) findViewById(R.id.btnDecrease);
        btnIncrease = (Button) findViewById(R.id.btnIncrease);
        btnDecrease.setOnClickListener(this);
        btnIncrease.setOnClickListener(this);
        etAmount.addTextChangedListener(this);

        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.AmountView);
        //宽度
        int btnWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_btnWidth, LayoutParams.WRAP_CONTENT);
        int tvWidth = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_numTvWidth, 80);
        //文字大小
        int tvTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_numTextSize, 0);
        int btnTextSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.AmountView_btnTextSize, 0);
        //文字颜色
        int tvColor = obtainStyledAttributes.getColor(R.styleable.AmountView_NumTvColor, getResources().getColor(R.color.black));
        int btnTvColor = obtainStyledAttributes.getColor(R.styleable.AmountView_btnTvColor, getResources().getColor(R.color.black));
        //背景
        int btnDecreaseBg = obtainStyledAttributes.getResourceId(R.styleable.AmountView_btnLeftBg, R.drawable.btn_amount);
        int btnIncreaseBg = obtainStyledAttributes.getResourceId(R.styleable.AmountView_btnRightBg, R.drawable.btn_amount);
        int numTvBg = obtainStyledAttributes.getResourceId(R.styleable.AmountView_numTvBg, R.color.white);

        minValue = obtainStyledAttributes.getInt(R.styleable.AmountView_minValue, minValue);
        minValueTip = TextUtils.isEmpty(obtainStyledAttributes.getString(R.styleable.AmountView_minValueTip))
                ? minValueTip
                : obtainStyledAttributes.getString(R.styleable.AmountView_minValueTip);
        maxValue = obtainStyledAttributes.getInt(R.styleable.AmountView_maxValue, maxValue);
        maxValueTip = TextUtils.isEmpty(obtainStyledAttributes.getString(R.styleable.AmountView_maxValueTip))
                ? maxValueTip
                : obtainStyledAttributes.getString(R.styleable.AmountView_maxValueTip);
        step = obtainStyledAttributes.getInt(R.styleable.AmountView_step,step);
        obtainStyledAttributes.recycle();

        //-----------------增减按钮----------------------------------
        LayoutParams btnParams = new LayoutParams(btnWidth, LayoutParams.MATCH_PARENT);
        btnDecrease.setLayoutParams(btnParams);
        btnIncrease.setLayoutParams(btnParams);
        //文字大小
        if (btnTextSize != 0) {
            btnDecrease.setTextSize(btnTextSize);
            btnIncrease.setTextSize(btnTextSize);
        }
        //文字颜色
        if (btnTvColor != 0) {
            btnDecrease.setTextColor(btnTvColor);
            btnIncrease.setTextColor(btnTvColor);
        }
        //按钮背景
        if (btnDecreaseBg != 0) {
            btnDecrease.setBackgroundResource(btnDecreaseBg);
        }
        if (btnIncreaseBg != 0) {
            btnIncrease.setBackgroundResource(btnIncreaseBg);
        }

        //---------------------输入框----------------------------------
        LayoutParams textParams = new LayoutParams(tvWidth, LayoutParams.MATCH_PARENT);
        etAmount.setLayoutParams(textParams);
        if (tvTextSize != 0) {
            etAmount.setTextSize(tvTextSize);
        }
        if (tvColor != 0) {
            etAmount.setTextColor(tvColor);
        }
        if (numTvBg != 0) {
            etAmount.setBackgroundResource(numTvBg);
        }
        if (minValue>Integer.MIN_VALUE){
            etAmount.setText(minValue+"");
        }


    }

    /**
     * 设置数值监听器
     **/
    public void setOnAmountChangeListener(OnAmountChangeListener onAmountChangeListener) {
        this.mListener = onAmountChangeListener;
    }

    /**
     * 设置最大值
     **/
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * 设置最小值
     **/
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    /**
     * 最小值提示
     **/
    public void setMinValueTip(String minValueTip) {
        this.minValueTip = minValueTip;
    }

    /**
     * 最大值提示
     **/
    public void setMaxValueTip(String maxValueTip) {
        this.maxValueTip = maxValueTip;
    }

    /**
     * 按钮宽度
     **/
    public void setBtnWidth(int btnWidthDp) {
        LayoutParams btnParams = new LayoutParams((int) (btnWidthDp * mDensity), LayoutParams.MATCH_PARENT);
        btnDecrease.setLayoutParams(btnParams);
        btnIncrease.setLayoutParams(btnParams);
    }

    /**
     * 按钮文本颜色
     **/
    public void setBtnTextColor(int color) {
        setBtnLeftTextColor(color);
        setBtnRightTextColor(color);
    }

    /**
     * 左边按钮文本颜色
     **/
    public void setBtnLeftTextColor(int color) {
        btnDecrease.setTextColor(color);
    }

    /**
     * 右边按钮文本颜色
     **/
    public void setBtnRightTextColor(int color) {
        btnIncrease.setTextColor(color);
    }

    /**
     * 按钮文本大小
     **/
    public void setBtnTextSize(int sizeDp) {
        setBtnLeftTextSize(sizeDp);
        setBtnRightTextSize(sizeDp);
    }

    /**
     * 左边按钮文本大小
     **/
    public void setBtnLeftTextSize(int sizeDp) {
        btnDecrease.setTextSize(mDensity * sizeDp);
    }

    /**
     * 右边按钮文本大小
     **/
    public void setBtnRightTextSize(int sizeDp) {
        btnIncrease.setTextSize(mDensity * sizeDp);
    }

    /**
     * 按钮背景
     **/
    public void setBtnBg(int resourceId) {
        setLeftBtnBg(resourceId);
        setRightBtnBg(resourceId);
    }

    /**
     * 左边按钮背景
     **/
    public void setLeftBtnBg(int resourceId) {
        btnDecrease.setBackgroundResource(resourceId);
    }

    /**
     * 右边按钮背景
     **/
    public void setRightBtnBg(int resourceId) {
        btnIncrease.setBackgroundResource(resourceId);
    }

    /**
     * 输入框宽度
     **/
    public void setNumTextWidth(int textWidthDp) {
        LayoutParams textParams = new LayoutParams((int) (mDensity * textWidthDp), LayoutParams.MATCH_PARENT);
        etAmount.setLayoutParams(textParams);
    }

    /**
     * 输入框文字颜色
     **/
    public void setNumTextColor(int color) {
        etAmount.setTextColor(color);
    }

    /**
     * 输入框文字大小
     **/
    public void setNumTextSize(int textSizeDp) {
        etAmount.setTextSize(mDensity * textSizeDp);
    }

    /**
     * 输入框背景
     **/
    public void setNumTextBg(int resourceId) {
        etAmount.setBackgroundResource(resourceId);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnDecrease) {
            if (amount -step>= minValue) {
                amount-=step;
                etAmount.setText(amount + "");
            } else {
               etAmount.setText(minValue+"");
                Toast.makeText(getContext(), minValueTip, Toast.LENGTH_SHORT).show();
            }
        } else if (i == R.id.btnIncrease) {
            if (amount+step <= maxValue) {
                amount+=step;
                etAmount.setText(amount + "");
            } else {
                etAmount.setText(maxValue+"");
                Toast.makeText(getContext(), maxValueTip, Toast.LENGTH_SHORT).show();
            }
        }

        etAmount.clearFocus();

        if (mListener != null) {
            mListener.onAmountChange(this, amount);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().isEmpty())
            return;
        amount = Integer.valueOf(s.toString());
        //输入的值不能超过最大值
        if (amount > maxValue) {
            etAmount.setText(maxValue + "");
            Toast.makeText(getContext(),"输入的值超过最大值"+maxValue+"~~",Toast.LENGTH_SHORT).show();
            return;
        }

        if (mListener != null) {
            mListener.onAmountChange(this, amount);
        }
    }


    /**
     * 数量变化监听器
     **/
    public interface OnAmountChangeListener {
        void onAmountChange(View view, int amount);
    }

}
