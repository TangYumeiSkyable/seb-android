package com.rihuisoft.loginmode.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

public class CropImageView extends ImageViewTouchBase {

    ArrayList<HighlightView> highlightViews = new ArrayList<HighlightView>();
    HighlightView motionHighlightView;
    Context context;

    private float lastX;
    private float lastY;
    private int motionEdge;

    @SuppressWarnings("UnusedDeclaration")
    public CropImageView(Context context) {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    	try {
    		 super.onLayout(changed, left, top, right, bottom);
    	        if (bitmapDisplayed.getBitmap() != null) {
    	            for (HighlightView hv : highlightViews) {

    	                hv.matrix.set(getUnrotatedMatrix());
    	                hv.invalidate();
    	                if (hv.hasFocus()) {
    	                    centerBasedOnHighlightView(hv);
    	                }
    	            }
    	        }
    	} catch(Exception e) {
			e.printStackTrace();
		}
       
    }

    @Override
    protected void zoomTo(float scale, float centerX, float centerY) {
    	try {
    		 super.zoomTo(scale, centerX, centerY);
    	        for (HighlightView hv : highlightViews) {
    	            hv.matrix.set(getUnrotatedMatrix());
    	            hv.invalidate();
    	        }
    	} catch(Exception e) {
			e.printStackTrace();
		}
       
    }

    @Override
    protected void zoomIn() {
    	try {
    		super.zoomIn();
            for (HighlightView hv : highlightViews) {
                hv.matrix.set(getUnrotatedMatrix());
                hv.invalidate();
            }
    	} catch(Exception e) {
			e.printStackTrace();
		}
        
    }

    @Override
    protected void zoomOut() {
    	try {
    		 super.zoomOut();
    	        for (HighlightView hv : highlightViews) {
    	            hv.matrix.set(getUnrotatedMatrix());
    	            hv.invalidate();
    	        }
    	} catch(Exception e) {
			e.printStackTrace();
		}
       
    }

    @Override
    protected void postTranslate(float deltaX, float deltaY) {
    	try {
    		super.postTranslate(deltaX, deltaY);
            for (HighlightView hv : highlightViews) {
                hv.matrix.postTranslate(deltaX, deltaY);
                hv.invalidate();
            }
    	} catch(Exception e) {
			e.printStackTrace();
		}
        
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	try {
    		CropImageActivity cropImageActivity = (CropImageActivity) context;
            if (cropImageActivity.isSaving()) {
                return false;
            }

            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (HighlightView hv : highlightViews) {
                    int edge = hv.getHit(event.getX(), event.getY());
                    if (edge != HighlightView.GROW_NONE) {
                        motionEdge = edge;
                        motionHighlightView = hv;
                        lastX = event.getX();
                        lastY = event.getY();
                        motionHighlightView.setMode((edge == HighlightView.MOVE)
                                ? HighlightView.ModifyMode.Move
                                : HighlightView.ModifyMode.Grow);
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP://UPʱ����image������֤�ü��������ʾ
                if (motionHighlightView != null) {
                    centerBasedOnHighlightView(motionHighlightView);
                    motionHighlightView.setMode(HighlightView.ModifyMode.None);
                }
                motionHighlightView = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (motionHighlightView != null) {
                    motionHighlightView.handleMotion(motionEdge, event.getX()
                            - lastX, event.getY() - lastY);
                    lastX = event.getX();
                    lastY = event.getY();
                    ensureVisible(motionHighlightView);
                }
                break;
            }

            switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                center(true, true);
                break;
            case MotionEvent.ACTION_MOVE:
                // if we're not zoomed then there's no point in even allowing
                // the user to move the image around. This call to center puts
                // it back to the normalized location (with false meaning don't
                // animate).
                if (getScale() == 1F) {
                    center(true, true);
                }
                break;
            }

    	} catch(Exception e) {
			e.printStackTrace();
		}
        
        return true;
    }

    /**
     * ���ݲü����λ�ã����ƶ�image����֤�ü���ʼ����ʾ��ͼƬ��Χ��
     * Pan the displayed image to make sure the cropping rectangle is visible.
     * @param hv
     */
    private void ensureVisible(HighlightView hv) {
    	try {
    		 Rect r = hv.drawRect;

    	        int panDeltaX1 = Math.max(0, getLeft() - r.left);
    	        int panDeltaX2 = Math.min(0, getRight() - r.right);

    	        int panDeltaY1 = Math.max(0, getTop() - r.top);
    	        int panDeltaY2 = Math.min(0, getBottom() - r.bottom);

    	        int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
    	        int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;

    	        if (panDeltaX != 0 || panDeltaY != 0) {
    	            panBy(panDeltaX, panDeltaY);
    	        }
    	} catch(Exception e) {
			e.printStackTrace();
		}
       
    }

    // If the cropping rectangle's size changed significantly, change the
    // view's center and scale according to the cropping rectangle.
    private void centerBasedOnHighlightView(HighlightView hv) {
    	try {
    		Rect drawRect = hv.drawRect;

            float width = drawRect.width();
            float height = drawRect.height();

            float thisWidth = getWidth();
            float thisHeight = getHeight();

            float z1 = thisWidth / width * .6F;
            float z2 = thisHeight / height * .6F;

            float zoom = Math.min(z1, z2);
            zoom = zoom * this.getScale();
            zoom = Math.max(1F, zoom);

            if ((Math.abs(zoom - getScale()) / zoom) > .1) {
                float[] coordinates = new float[] { hv.cropRect.centerX(), hv.cropRect.centerY() };
                getUnrotatedMatrix().mapPoints(coordinates);
                zoomTo(zoom, coordinates[0], coordinates[1], 300F);
            }

            ensureVisible(hv);
    	} catch(Exception e) {
			e.printStackTrace();
		}
        
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	try {
    		super.onDraw(canvas);
            for (HighlightView mHighlightView : highlightViews) {
                mHighlightView.draw(canvas);
            }
    	} catch(Exception e) {
			e.printStackTrace();
		}
        
    }

    public void add(HighlightView hv) {
    	try {
    		 highlightViews.add(hv);
    	        invalidate();
    	} catch(Exception e) {
			e.printStackTrace();
		}
       
    }
}
