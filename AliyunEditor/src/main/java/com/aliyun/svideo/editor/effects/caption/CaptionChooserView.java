package com.aliyun.svideo.editor.effects.caption;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.svideo.base.Form.PasterForm;
import com.aliyun.svideo.base.Form.ResourceForm;
import com.aliyun.svideo.editor.R;
import com.aliyun.svideo.editor.editor.PasterUICaptionImpl;
import com.aliyun.svideo.editor.editor.AbstractPasterUISimpleImpl;
import com.aliyun.svideo.editor.editor.PasterUITextImpl;
import com.aliyun.svideo.editor.effectmanager.MoreCaptionActivity;
import com.aliyun.svideo.editor.effects.control.BaseChooser;
import com.aliyun.svideo.editor.effects.control.EffectInfo;
import com.aliyun.svideo.editor.effects.control.OnItemClickListener;
import com.aliyun.svideo.editor.effects.control.SpaceItemDecoration;
import com.aliyun.svideo.editor.effects.control.UIEditorPage;
import com.aliyun.svideo.downloader.DownloaderManager;
import com.aliyun.svideo.downloader.FileDownloaderModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CaptionChooserView extends BaseChooser {

    private static final int CAPTION_TYPE = 6;

    private RecyclerView mCategoryList;
    private CategoryAdapter mCategoryAdapter;
    private CaptionAdapter mCaptionAdapter;
    private ArrayList<ResourceForm> mCaptionData;
    private AsyncTask<Void, Void, List<FileDownloaderModel>> mLoadTask;

    public CaptionChooserView(@NonNull Context context) {
        this(context, null);
    }

    public CaptionChooserView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptionChooserView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.alivc_editor_view_chooser_caption, this);
        initTitleView(view);
        mCaptionData = new ArrayList<>();
        initListener();
        RecyclerView captionList = view.findViewById(R.id.effect_list);
        captionList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        captionList.addItemDecoration(
            new SpaceItemDecoration(getContext().getResources().getDimensionPixelSize(R.dimen.list_item_space)));
        mCaptionAdapter = new CaptionAdapter(getContext());
        mCaptionAdapter.setOnItemClickListener(mOnItemClickListener);
        captionList.setAdapter(mCaptionAdapter);
        mCategoryList = view.findViewById(R.id.category_list);
        mCategoryList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mCategoryAdapter = new CategoryAdapter(getContext());
        mCategoryAdapter.addShowFontCategory();
        mCategoryAdapter.setOnItemClickListener(mOnItemClickListener);
        mCategoryAdapter.setMoreClickListener(mOnMoreClickListener);
        mCategoryList.setAdapter(mCategoryAdapter);
        loadLocalPaster();
    }

    private void initTitleView(View view) {

        ImageView ivEffect = view.findViewById(R.id.iv_effect_icon);
        TextView tvTitle = view.findViewById(R.id.tv_effect_title);
        ivEffect.setImageResource(R.mipmap.aliyun_svideo_icon_caption);
        tvTitle.setText(R.string.alivc_editor_effect_caption);

        view.findViewById(R.id.iv_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dealCancel();
            }
        });
        view.findViewById(R.id.iv_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnEffectActionLister != null) {
                    mOnEffectActionLister.onComplete();
                }
            }
        });
    }

    private void dealCancel() {
        if (mOnEffectActionLister != null) {
            mOnEffectActionLister.onCancel();
        }
    }

    @Override
    public boolean isPlayerNeedZoom() {
        return true;
    }

    /**
     * ??????????????????
     *
     * @return FrameLayout
     */
    @Override
    protected FrameLayout getThumbContainer() {
        return findViewById(R.id.fl_thumblinebar);
    }

    private PasterForm addPasterForm(FileDownloaderModel model) {
        PasterForm pasterForm = new PasterForm();
        pasterForm.setPreviewUrl(model.getSubpreview());
        pasterForm.setSort(model.getSubsort());
        pasterForm.setId(model.getSubid());
        pasterForm.setFontId(model.getFontid());
        pasterForm.setMD5(model.getMd5());
        pasterForm.setType(model.getSubtype());
        pasterForm.setIcon(model.getSubicon());
        pasterForm.setDownloadUrl(model.getUrl());
        pasterForm.setName(model.getSubname());
        return pasterForm;
    }

    private void loadLocalPaster() {
        mLoadTask = new MyLoadAsyncTask();
        mLoadTask.execute();
    }

    /**
     * ??????????????????
     *
     * @param id ?????????????????????
     */
    public void initResourceLocalWithSelectId(int id, List<FileDownloaderModel> modelsTemp) {
        mCaptionData.clear();

        ArrayList<ResourceForm> resourceForms = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();
        List<FileDownloaderModel> models = new ArrayList<>();
        if (modelsTemp != null && modelsTemp.size() > 0) {
            for (FileDownloaderModel model : modelsTemp) {
                if (new File(model.getPath()).exists()) {
                    models.add(model);
                }
            }
            ResourceForm form = null;
            ArrayList<PasterForm> pasterForms = null;
            for (FileDownloaderModel model : models) {
                if (!ids.contains(model.getId())) {
                    if (form != null) {
                        form.setPasterList(pasterForms);
                        resourceForms.add(form);
                    }
                    ids.add(model.getId());
                    form = new ResourceForm();
                    pasterForms = new ArrayList<>();
                    form.setPreviewUrl(model.getPreview());
                    form.setIcon(model.getIcon());
                    form.setLevel(model.getLevel());
                    form.setName(model.getName());
                    form.setNameEn(model.getNameEn());
                    form.setId(model.getId());
                    form.setDescription(model.getDescription());
                    form.setSort(model.getSort());
                    form.setIsNew(model.getIsnew());
                }
                PasterForm pasterForm = addPasterForm(model);
                pasterForms.add(pasterForm);
            }
            if (form != null) {
                form.setPasterList(pasterForms);
                resourceForms.add(form);
            }
        }
        mCaptionData.addAll(resourceForms);
        ResourceForm form = new ResourceForm();
        form.setMore(true);
        mCaptionData.add(form);
        mCategoryAdapter.setData(mCaptionData);
        if (mCaptionData.size() == 1) {
            mCaptionAdapter.clearData();
        } else {
            int categoryIndex = 0;
            if (id == 0 || !ids.contains(id)) {
                //?????????????????????
                mCaptionAdapter.showFontData();
            } else if (ids.size() > 0) {
                //???????????????
                for (ResourceForm resourceForm : mCaptionData) {
                    if (resourceForm.getId() == id) {
                        mCaptionAdapter.setData(resourceForm);
                        break;
                    }
                    categoryIndex++;
                }
            }
            mCategoryList.smoothScrollToPosition(categoryIndex);
            mCategoryAdapter.selectPosition(categoryIndex);
        }
    }

    public void setCurrResourceID(int id) {
        if (id != -1) {
            this.mCurrID = id;
        }
        loadLocalPaster();
    }

    /**
     * ????????????
     */
    private CategoryAdapter.OnMoreClickListener mOnMoreClickListener;
    /**
     * ??????????????????
     */
    private OnItemClickListener mOnItemClickListener;

    /**
     * ??????????????????????????????????????????????????????????????????listener??????????????????????????????
     */
    private void initListener() {

        mOnMoreClickListener = new CategoryAdapter.OnMoreClickListener() {
            /**
             * startActivityForResult ???????????????
             * @see com.aliyun.svideo.editor.editor.EditorActivity#onActivityResult(int, int, Intent)
             */
            @Override
            public void onMoreClick() {
                Intent moreIntent = new Intent(getContext(), MoreCaptionActivity.class);
                ((Activity) getContext()).startActivityForResult(moreIntent, CAPTION_REQUEST_CODE);
            }
        };

        mOnItemClickListener = new OnItemClickListener() {
            @Override
            public boolean onItemClick(EffectInfo effectInfo, int index) {
                if (effectInfo.isCategory) {
                    if (index == 0) {
                        //?????????
                        mCaptionAdapter.showFontData();
                        mCurrID = 0;
                    } else {
                        ResourceForm resourceForm = mCaptionData.get(index);
                        mCurrID = resourceForm.getId();
                        mCaptionAdapter.setData(resourceForm);
                    }
                } else {
                    if (mOnEffectChangeListener != null) {
                        mOnEffectChangeListener.onEffectChange(effectInfo);
                    }
                }
                return true;
            }
        };

    }

    @Override
    protected UIEditorPage getUIEditorPage() {
        return UIEditorPage.CAPTION;
    }

    @Override
    protected void onRemove() {
        super.onRemove();
        if (mLoadTask != null) {
            mLoadTask.cancel(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dealCancel();
    }

    @Override
    public boolean isHostPaster(AbstractPasterUISimpleImpl uic) {

        return uic != null && (uic instanceof PasterUITextImpl || uic instanceof PasterUICaptionImpl);
    }

    /**
     * ?????????????????????AsyncTask
     */
    private class MyLoadAsyncTask extends AsyncTask<Void, Void, List<FileDownloaderModel>> {

        @Override
        protected List<FileDownloaderModel> doInBackground(Void... voids) {
            return DownloaderManager.getInstance().getDbController().getResourceByType(CAPTION_TYPE);
        }

        @Override
        protected void onPostExecute(List<FileDownloaderModel> fileDownloaderModels) {
            super.onPostExecute(fileDownloaderModels);
            initResourceLocalWithSelectId(mCurrID, fileDownloaderModels);
        }
    }

}
